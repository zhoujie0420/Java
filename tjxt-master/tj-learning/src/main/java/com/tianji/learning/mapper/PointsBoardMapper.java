package com.tianji.learning.mapper;

import com.tianji.learning.domain.po.PointsBoard;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;


public interface PointsBoardMapper extends BaseMapper<PointsBoard> {

    void createPointsBoardTable(@Param("tableName") String tableName);

    void saveBySeason(@Param("tableName") String tableName, @Param("boardList") List<PointsBoard> boardList);

    List<PointsBoard> queryHistoryBoard(@Param("tableName")String tableName,
                                        @Param("season")Long season,
                                        @Param("userId")Long userId);
}
