package com.tianji.learning.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tianji.api.client.user.UserClient;
import com.tianji.api.dto.user.UserDTO;
import com.tianji.common.utils.CollUtils;
import com.tianji.common.utils.DateUtils;
import com.tianji.common.utils.StringUtils;
import com.tianji.common.utils.UserContext;
import com.tianji.learning.constants.RedisConstants;
import com.tianji.learning.domain.po.PointsBoard;
import com.tianji.learning.domain.query.PointsBoardQuery;
import com.tianji.learning.domain.vo.PointsBoardItemVO;
import com.tianji.learning.domain.vo.PointsBoardVO;
import com.tianji.learning.mapper.PointsBoardMapper;
import com.tianji.learning.service.IPointsBoardService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tianji.learning.util.TableInfoContext;
import org.springframework.data.redis.core.BoundZSetOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * 学霸天梯榜 服务实现类
 * </p>
 *
 * @author hb
 * @since 2023-04-21
 */
@Service
public class PointsBoardServiceImpl extends ServiceImpl<PointsBoardMapper, PointsBoard> implements IPointsBoardService {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Resource
    private UserClient userClient;

    @Override
    public PointsBoardVO queryPointsBoardBySeason(PointsBoardQuery query) {

        // 1-判断是否查询当前赛季
        Long season = query.getSeason();
        boolean isCurrent = season == null || season == 0;

        // 2-获取Redis的key
        LocalDateTime now = LocalDateTime.now();
        String key = RedisConstants.POINTS_BOARD_KEY_PREFIX + now.format(DateUtils.POINTS_BOARD_SUFFIX_FORMATTER);

        // 2-查询我的积分排名:查询我的当前榜单(Redis)/查询我的历史榜单(MySQL)
        PointsBoard myBoard = isCurrent ?
                queryMyCurrentBoard(key) : queryMyHistoryBoardList(season);

        // 3-查询榜单列表:查询当前榜单(Redis)/查询历史榜单(MySQL)
        List<PointsBoard> list = isCurrent ?
                queryCurrentBoardList(key, query.getPageNo(), query.getPageSize()) : queryHistoryBoard(query);

        // 4-封装vo返回
        // 封装我的积分信息
        PointsBoardVO vo = new PointsBoardVO();
        if (myBoard != null) {
            vo.setPoints(myBoard.getPoints());
            vo.setRank(myBoard.getRank());
        }
        // 封装赛季列表信息
        if (CollUtils.isNotEmpty(list)) {
            // 获取用户信息
            Set<Long> userId = list.stream().map(PointsBoard::getUserId).collect(Collectors.toSet());
            List<UserDTO> userList = userClient.queryUserByIds(userId);
            if (CollUtils.isEmpty(userList)) {
                return vo;
            }

            Map<Long, String> userMap = userList.stream().collect(Collectors.toMap(UserDTO::getId, UserDTO::getName));

            // 属性转换
            List<PointsBoardItemVO> boardList = new ArrayList<>();
            for (PointsBoard board : list) {
                PointsBoardItemVO item = new PointsBoardItemVO();
                item.setPoints(board.getPoints());
                item.setRank(board.getRank());
                item.setName(userMap.getOrDefault(board.getUserId(), ""));

                boardList.add(item);
            }
            vo.setBoardList(boardList);
        }
        return vo;
    }

    @Override
    public void createPointsBoardTableBySeason(Integer seasonId) {
        getBaseMapper().createPointsBoardTable("points_board_" + seasonId);
    }

    private List<PointsBoard> queryHistoryBoard(PointsBoardQuery query) {

        // 1.计算表名
        String tableName = "points_board_" + query.getSeason();
        TableInfoContext.setInfo(tableName);

        // 2.查询数据
        List<PointsBoard> records = getBaseMapper().queryHistoryBoard(tableName, query.getSeason(), null);

        // 3.数据处理
        if (CollUtils.isEmpty(records)) {
            return CollUtils.emptyList();
        }
        records.forEach(b -> b.setRank(b.getId().intValue()));
        return records;
    }

    @Override
    public List<PointsBoard> queryCurrentBoardList(String key, Integer pageNo, Integer pageSize) {

        // 1-Redis数据查询(因为前面已经绑定了key，这直接查询就可以)
        int from = (pageNo - 1) * pageSize;
        // 起始脚标是0，因此end需要-1：如查询第一页：from=0，pageSize=10，而0-10查出来11个数据，因此-1
        Set<ZSetOperations.TypedTuple<String>> tuples = stringRedisTemplate.opsForZSet()
                .reverseRangeWithScores(key, from, from + pageSize - 1);
        if (CollUtils.isEmpty(tuples)) {
            return CollUtils.emptyList();
        }

        // 2-数据封装
        // 初始化排名，因为是分页，所以排名信息是分页+1
        int rank = from + 1;
        List<PointsBoard> list = new ArrayList<>();
        for (ZSetOperations.TypedTuple<String> tuple : tuples) {
            String userId = tuple.getValue();
            Double points = tuple.getScore();
            if (StringUtils.isBlank(userId) || Objects.isNull(points)) {
                continue;
            }

            PointsBoard p = new PointsBoard();
            p.setUserId(Long.valueOf(userId));
            p.setPoints(points.intValue());
            // 因为redis-sset返回数据顺序就是排名，因此我们从1开始自增就是排名信息
            p.setRank(rank++);
            list.add(p);
        }
        return list;
    }

    @Override
    public void saveBySeason(Integer season, List<PointsBoard> boardList) {
        getBaseMapper().saveBySeason("points_board_" + season, boardList);
    }

    private PointsBoard queryMyHistoryBoardList(Long season) {
        // 1.获取登录用户
        Long userId = UserContext.getUser();

        // 2.计算表名
        String tableName = "points_board_" + season;
        TableInfoContext.setInfo(tableName);

        // 3.查询数据
        List<PointsBoard> records = getBaseMapper().queryHistoryBoard(tableName, season, userId);
        if (CollUtils.isEmpty(records)) {
            return null;
        }
        // 4.转换数据
        PointsBoard pointsBoard = records.get(0);
        pointsBoard.setRank(pointsBoard.getId().intValue());
        return pointsBoard;
    }

    private PointsBoard queryMyCurrentBoard(String key) {

        // 1-redis绑定key，避免每次查询都需要给key(优化实现方案之一，用原来每次给key的也可以)
        BoundZSetOperations<String, String> ops = stringRedisTemplate.boundZSetOps(key);
        // 需要转成String再去redis查询
        String userId = UserContext.getUser().toString();
        // 2-查询redis数据中的得分信息
        Double score = ops.score(userId);

        // 3-查询redis数据中的排名信息
        Long rank = ops.reverseRank(userId);

        // 4-数据封装
        PointsBoard board = new PointsBoard();
        board.setPoints(score == null ? 0 : score.intValue());
        // 排名脚标从0开始，需要 +1
        board.setRank(rank == null ? 0 : rank.intValue() + 1);
        return board;
    }
}
