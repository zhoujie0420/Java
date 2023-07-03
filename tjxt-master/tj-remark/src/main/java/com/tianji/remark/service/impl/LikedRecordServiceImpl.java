package com.tianji.remark.service.impl;

import com.tianji.api.dto.remark.LikedTimesDTO;
import com.tianji.common.autoconfigure.mq.RabbitMqHelper;
import com.tianji.common.exceptions.BizIllegalException;
import com.tianji.common.utils.*;
import com.tianji.remark.domain.dto.LikeRecordFormDTO;
import com.tianji.remark.domain.po.LikedRecord;
import com.tianji.remark.mapper.LikedRecordMapper;
import com.tianji.remark.service.ILikedRecordService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.data.redis.connection.StringRedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.tianji.common.constants.MqConstants.Exchange.LIKE_RECORD_EXCHANGE;
import static com.tianji.common.constants.MqConstants.Key.LIKED_TIMES_KEY_TEMPLATE;
import static com.tianji.remark.constants.RedisKeyConstants.LIKES_BIZ_KEY_PREFIX;
import static com.tianji.remark.constants.RedisKeyConstants.LIKES_TIMES_KEY_PREFIX;

/**
 * <p>
 * 点赞记录表 服务实现类
 * </p>
 *
 * @author hb
 * @since 2023-04-20
 */
@Service
public class LikedRecordServiceImpl extends ServiceImpl<LikedRecordMapper, LikedRecord> implements ILikedRecordService {

    @Resource
    private RabbitMqHelper rabbitMqHelper;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public void likeOrNot(LikeRecordFormDTO recordForm) {

        // 1-获取用户信息
        Long userId = UserContext.getUser();

        // 2-点赞或取消点赞
        boolean liked = recordForm.getLiked();
        boolean cacheSuccess = false;
        String likesKey = LIKES_BIZ_KEY_PREFIX + recordForm.getBizId();

        if (liked) {
            // 点赞，数据直接存储
            Long cacheRows = stringRedisTemplate.opsForSet().add(likesKey, userId.toString());
            cacheSuccess = cacheRows != null && cacheRows > 0;
        } else {
            // 取消点赞，直接删除缓存
            Long removeRows = stringRedisTemplate.opsForSet().remove(likesKey, userId.toString());
            cacheSuccess = removeRows != null && removeRows > 0;
        }

        // 保存失败直接返回
        if (!cacheSuccess) {
            return;
        }

        // 3-点赞或取消点赞成功后，缓存新的点赞总数
        // 3-1 统计当前最新的点赞数(缓存统计)
        Long likesSize = stringRedisTemplate.opsForSet().size(likesKey);
        if (null == likesSize) {
            return;
        }
        // 3-2 缓存点赞总数
        String timesKey = LIKES_TIMES_KEY_PREFIX + recordForm.getBizType();
        stringRedisTemplate.opsForZSet().add(timesKey, recordForm.getBizId().toString(), likesSize);
    }

    @Override
    public Set<Long> isBizLiked(List<Long> bizIds) {

        // 1-获取用户信息
        Long userId = UserContext.getUser();

        // 2-缓存判断是否有数据
        List<Object> objects = stringRedisTemplate.executePipelined((RedisCallback<Object>) connection -> {

            // 转换成String连接类型
            StringRedisConnection redisConnection = (StringRedisConnection) connection;

            for (Long bizId : bizIds) {
                String key = LIKES_TIMES_KEY_PREFIX + bizId;
                // 借此完成批量指令添加，而不再使用最初的
                redisConnection.sIsMember(key, userId.toString());
            }
            return null;
        });

        // 3-判断缓存中是否有数据
        return IntStream.range(0, objects.size())
                // 只保留true的数据
                .filter(i -> (boolean) objects.get(i))
                // 为true的去bizIds里获取数据
                .mapToObj(bizIds::get)
                .collect(Collectors.toSet());
    }

    @Override
    public void readCacheAndSendMessage(String bizType, int maxBizSize) {

        // 1-批量读取并移除bizId
        String key = LIKES_TIMES_KEY_PREFIX + bizType;
        // 因为点赞数越少越敏感，这里我们建议从min开始取
        Set<ZSetOperations.TypedTuple<String>> typedTuples = stringRedisTemplate.opsForZSet().popMin(key, maxBizSize);
        if (CollUtils.isEmpty(typedTuples)) {
            return;
        }

        // 2-循环数据转换
        List<LikedTimesDTO> list = new ArrayList<>(typedTuples.size());
        for (ZSetOperations.TypedTuple<String> tuple : typedTuples) {
            String bizId = tuple.getValue();
            Double likeTimes = tuple.getScore();
            if(StringUtils.isBlank(bizId) || Objects.isNull(likeTimes)) {
                continue;
            }
            list.add(LikedTimesDTO.of(Long.valueOf(bizId), likeTimes.intValue()));
        }

        // 3-发送MQ消息
        rabbitMqHelper.send(
                LIKE_RECORD_EXCHANGE,
                StringUtils.format(LIKED_TIMES_KEY_TEMPLATE, bizType),
                list
        );
    }
}
