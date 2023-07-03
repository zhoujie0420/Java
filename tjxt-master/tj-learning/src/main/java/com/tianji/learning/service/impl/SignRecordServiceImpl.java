package com.tianji.learning.service.impl;

import com.tianji.common.autoconfigure.mq.RabbitMqHelper;
import com.tianji.common.constants.MqConstants;
import com.tianji.common.exceptions.BizIllegalException;
import com.tianji.common.utils.CollUtils;
import com.tianji.common.utils.DateUtils;
import com.tianji.common.utils.UserContext;
import com.tianji.learning.domain.vo.SignResultVO;
import com.tianji.learning.mq.message.SignInMessage;
import com.tianji.learning.service.ISignRecordService;
import org.springframework.data.redis.connection.BitFieldSubCommands;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @Description:
 * @Date: 2023/4/21 16:43
 */
@Service
public class SignRecordServiceImpl implements ISignRecordService {

    @Resource
    private RedisTemplate redisTemplate;

    @Resource
    private RabbitMqHelper mqHelper;

    @Override
    public SignResultVO addSignRecords() {

        // 1-签到
        // 1.1 获取用户信息
        Long userId = UserContext.getUser();

        // 1.2 获取日期信息
        LocalDateTime now = LocalDateTime.now();

        // 1.3 拼接缓存key
        String key = "sign:uid:"
                + userId
                + now.format(DateUtils.SIGN_DATE_SUFFIX_FORMATTER);

        // 1.4 计算offset(脚标从0开始)
        int offset = now.getDayOfMonth() - 1;

        // 1.5 保存
        Boolean exists = redisTemplate.opsForValue().setBit(key, offset, true);
        if (null != exists && exists) {
            throw new BizIllegalException("请勿重复签到");
        }

        // 2-计算是否连续签到，是的话多加积分
        int signDays = countSignDays(key, now.getDayOfMonth());

        // 3-计算签到积分
        int rewardPoints;
        switch (signDays) {
            case 7:
                rewardPoints = 10;
                break;
            case 14:
                rewardPoints = 20;
                break;
            case 28:
                rewardPoints = 40;
                break;
            default:
                rewardPoints = 0;
                break;

        }

        // 4-保存积分明细记录(奖励积分+基础签到得分1)
        mqHelper.send(
                MqConstants.Exchange.LEARNING_EXCHANGE,
                MqConstants.Key.SIGN_IN,
                SignInMessage.of(userId, rewardPoints + 1));

        // 5-封装结果返回
        SignResultVO resultVO = new SignResultVO();
        resultVO.setSignDays(signDays);
        resultVO.setSignPoints(0);
        resultVO.setRewardPoints(0);
        return resultVO;
    }

    @Override
    public Byte[] querySignRecords() {
        // 1.获取登录用户
        Long userId = UserContext.getUser();
        // 2.获取日期
        LocalDate now = LocalDate.now();
        int dayOfMonth = now.getDayOfMonth();
        // 3.拼接key
        String key = "sign:uid:"
                + userId
                + now.format(DateUtils.SIGN_DATE_SUFFIX_FORMATTER);
        // 4.读取
        List<Long> result = redisTemplate.opsForValue()
                .bitField(key, BitFieldSubCommands.create().get(
                        BitFieldSubCommands.BitFieldType.unsigned(dayOfMonth)).valueAt(0));
        if (CollUtils.isEmpty(result)) {
            return new Byte[0];
        }
        int num = result.get(0).intValue();

        Byte[] arr = new Byte[dayOfMonth];
        int pos = dayOfMonth - 1;
        while (pos >= 0){
            arr[pos--] = (byte)(num & 1);
            // 把数字右移一位，抛弃最后一个bit位，继续下一个bit位
            num >>>= 1;
        }
        return arr;
    }

    /**
     * 计算连续得分天数
     * @param key          存储数据key
     * @param dayOfMonth   天数(获取截止目前的数据offset)
     * @return
     */
    private int countSignDays(String key, int dayOfMonth) {

        // 1-获取本月第一天开始至今的签到数据(因为可以执行多个子命令，所以返回值也是多个)
        List<Long> result = redisTemplate.opsForValue()
                .bitField(key, BitFieldSubCommands.create().get(
                        BitFieldSubCommands.BitFieldType.unsigned(dayOfMonth)).valueAt(0));
        if (CollUtils.isEmpty(result)) {
            return 0;
        }

        int num = result.get(0).intValue();
        // 2-定义计数器，统计连续天数
        int count = 0;
        // 3-循环条件：与1做与运算，得到是1就说明是连续的，继续，0则终止
        while ((num & 1) == 1) {
            // 计数器+1
            count ++;
            // 数字右移1位，实现不断的遍历
            num >>>= 1;
        }
        return count;
    }
}
