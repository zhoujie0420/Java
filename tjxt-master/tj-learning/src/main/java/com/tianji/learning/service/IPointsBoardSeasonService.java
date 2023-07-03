package com.tianji.learning.service;

import com.tianji.learning.domain.po.PointsBoardSeason;
import com.baomidou.mybatisplus.extension.service.IService;
import com.tianji.learning.domain.vo.PointsBoardSeasonVO;

import java.time.LocalDateTime;
import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author hb
 * @since 2023-04-21
 */
public interface IPointsBoardSeasonService extends IService<PointsBoardSeason> {

    List<PointsBoardSeasonVO> queryPointsBoardSeasons();

    Integer querySeasonByTime(LocalDateTime lastMonthTime);
}
