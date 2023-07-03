package com.tianji.learning.handler;

import com.tianji.common.utils.CollUtils;
import com.tianji.common.utils.DateUtils;
import com.tianji.learning.constants.RedisConstants;
import com.tianji.learning.domain.po.PointsBoard;
import com.tianji.learning.service.IPointsBoardSeasonService;
import com.tianji.learning.service.IPointsBoardService;
import com.tianji.learning.util.TableInfoContext;
import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

@Component
public class PointsBoardSaveDataHandler {

    @Resource
    private IPointsBoardSeasonService seasonService;

    @Resource
    private IPointsBoardService boardService;

    @XxlJob("savePointsBoard2DB")
    public void savePointsBoard2DB(){
        // 1.获取上月时间
        LocalDateTime time = LocalDateTime.now().minusMonths(1);

        // 2.计算动态表名
        // 2.1.查询赛季信息
        Integer season = seasonService.querySeasonByTime(time);
        // 2.2.将表名存入ThreadLocal
        TableInfoContext.setInfo("points_board_" + season);

        // 3.查询榜单数据
        // 3.1.拼接KEY
        String key = RedisConstants.POINTS_BOARD_KEY_PREFIX + time.format(DateUtils.POINTS_BOARD_SUFFIX_FORMATTER);
        // 3.2.查询数据
        int index = XxlJobHelper.getShardIndex();
        int total = XxlJobHelper.getShardTotal();
        // 起始页，就是分片序号+1
        int pageNo = index + 1;
        int pageSize = 1000;
        while (true) {
            List<PointsBoard> boardList = boardService.queryCurrentBoardList(key, pageNo, pageSize);
            if (CollUtils.isEmpty(boardList)) {
                break;
            }
            // 4.持久化到数据库
            // 4.1.把排名信息写入id
            boardList.forEach(b -> {
                // id主动生成，跟rank保持一致
                b.setId(b.getRank().longValue());
                // rank字段不需要，写null则mp不会操作
                b.setRank(null);
            });
            // 4.2.持久化(插入到指定表)
            boardService.saveBySeason(season, boardList);
            // 5.翻页，跳过N个页，N就是分片数量
            pageNo+=total;
        }
        // 任务结束，移除动态表名
        TableInfoContext.remove();
    }


}
