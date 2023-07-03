package com.tianji.learning.service;

import com.tianji.learning.domain.po.PointsRecord;
import com.baomidou.mybatisplus.extension.service.IService;
import com.tianji.learning.domain.vo.PointsStatisticsVO;
import com.tianji.learning.enums.PointsRecordType;

import java.util.List;

/**
 * <p>
 * 学习积分记录，每个月底清零 服务类
 * </p>
 *
 * @author hb
 * @since 2023-04-21
 */
public interface IPointsRecordService extends IService<PointsRecord> {

    /**
     * 新增用户积分信息
     * @param userId        用户id
     * @param point         增加积分
     * @param recordType    积分类型
     */
    void addPointsRecord(Long userId, int point, PointsRecordType recordType);

    List<PointsStatisticsVO> queryMyPointsToday();
}
