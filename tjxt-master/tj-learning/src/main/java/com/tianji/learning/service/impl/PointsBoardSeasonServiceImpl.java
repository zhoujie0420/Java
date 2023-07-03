package com.tianji.learning.service.impl;

import com.tianji.common.utils.BeanUtils;
import com.tianji.common.utils.CollUtils;
import com.tianji.learning.domain.po.PointsBoardSeason;
import com.tianji.learning.domain.vo.PointsBoardSeasonVO;
import com.tianji.learning.mapper.PointsBoardSeasonMapper;
import com.tianji.learning.service.IPointsBoardSeasonService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author hb
 * @since 2023-04-21
 */
@Service
public class PointsBoardSeasonServiceImpl extends ServiceImpl<PointsBoardSeasonMapper, PointsBoardSeason> implements IPointsBoardSeasonService {

    @Override
    public List<PointsBoardSeasonVO> queryPointsBoardSeasons() {
        // 1.获取时间
        LocalDateTime now = LocalDateTime.now();

        // 2.查询赛季列表，必须是当前赛季之前的（开始时间小于等于当前时间）
        List<PointsBoardSeason> list =  lambdaQuery()
                .le(PointsBoardSeason::getBeginTime, now).list();
        if (CollUtils.isEmpty(list)) {
            return CollUtils.emptyList();
        }
        // 3.返回VO
        return BeanUtils.copyToList(list, PointsBoardSeasonVO.class);
    }

    @Override
    public Integer querySeasonByTime(LocalDateTime lastMonthTime) {
        /**
         * 下面这个容易搞混：
         * 我们需要的是: lastMonthTime大于BeginTime、小于EndTime，
         * 而lambdaQuery传参第二个才是传递参数，因此这里需要反过来
         * 下面的意思就是：BeginTime小于lastMonthTime，EndTime大于lastMonthTime
         */
        Optional<PointsBoardSeason> optional = lambdaQuery()
                .le(PointsBoardSeason::getBeginTime, lastMonthTime)
                .ge(PointsBoardSeason::getEndTime, lastMonthTime)
                .oneOpt();
        return optional.map(PointsBoardSeason::getId).orElse(null);
    }
}
