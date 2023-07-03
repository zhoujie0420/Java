package com.tianji.learning.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.plugins.pagination.PageDto;
import com.tianji.api.cache.CategoryCache;
import com.tianji.api.client.course.CatalogueClient;
import com.tianji.api.client.course.CourseClient;
import com.tianji.api.client.search.SearchClient;
import com.tianji.api.client.user.UserClient;
import com.tianji.api.dto.course.CataSimpleInfoDTO;
import com.tianji.api.dto.course.CourseFullInfoDTO;
import com.tianji.api.dto.course.CourseSimpleInfoDTO;
import com.tianji.api.dto.user.UserDTO;
import com.tianji.common.domain.dto.PageDTO;
import com.tianji.common.exceptions.BadRequestException;
import com.tianji.common.utils.BeanUtils;
import com.tianji.common.utils.CollUtils;
import com.tianji.common.utils.StringUtils;
import com.tianji.common.utils.UserContext;
import com.tianji.learning.domain.dto.QuestionFormDTO;
import com.tianji.learning.domain.dto.QuestionFormUpdateDTO;
import com.tianji.learning.domain.po.InteractionQuestion;
import com.tianji.learning.domain.po.InteractionReply;
import com.tianji.learning.domain.query.QuestionAdminPageQuery;
import com.tianji.learning.domain.query.QuestionPageQuery;
import com.tianji.learning.domain.vo.QuestionAdminVO;
import com.tianji.learning.domain.vo.QuestionVO;
import com.tianji.learning.mapper.InteractionQuestionMapper;
import com.tianji.learning.mapper.InteractionReplyMapper;
import com.tianji.learning.service.IInteractionQuestionService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tianji.learning.service.IInteractionReplyService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * 互动提问的问题表 服务实现类
 * </p>
 *
 * @author hb
 * @since 2023-04-19
 */
@Service
public class InteractionQuestionServiceImpl extends ServiceImpl<InteractionQuestionMapper, InteractionQuestion> implements IInteractionQuestionService {

    @Resource
    private UserClient userClient;

    @Resource
    private SearchClient searchClient;

    @Resource
    private CourseClient courseClient;

    @Resource
    private CatalogueClient catalogueClient;

    @Resource
    private CategoryCache categoryCache;

    @Resource
    private InteractionReplyMapper replyMapper;

    @Override
    public void saveQuestion(QuestionFormDTO questionForm) {
        // 1-获取用户信息
        Long userId = UserContext.getUser();

        // 2-填充属性
        InteractionQuestion entity = BeanUtils.copyBean(questionForm, InteractionQuestion.class);
        entity.setUserId(userId);

        // 3-保存
        save(entity);
    }

    @Override
    public void updateQuestion(Long id, QuestionFormUpdateDTO questionForm) {

        // 1-获取当前登录用户
        Long userId = UserContext.getUser();

        // 2-查询问题
        InteractionQuestion question = getById(id);
        if (Objects.isNull(question)) {
            throw new BadRequestException("问题不存在");
        }

        // 3-判断是否是同一个用户
        if (!question.getUserId().equals(userId)) {
            throw new BadRequestException("无权修改别人的问题");
        }
        // 4-属性赋值
        InteractionQuestion entity = BeanUtils.copyBean(questionForm, InteractionQuestion.class);
        entity.setCourseId(null);
        entity.setId(id);

        // 5-更新
        updateById(entity);
    }

    @Override
    public void deleteQuestion(Long id) {

        // 1-获取当前登录用户
        Long userId = UserContext.getUser();

        // 2-查询问题
        InteractionQuestion question = getById(id);
        if (Objects.isNull(question)) {
            throw new BadRequestException("问题不存在");
        }

        // 3-判断是否是同一个用户
        if (!question.getUserId().equals(userId)) {
            throw new BadRequestException("无权删除别人的问题");
        }

        // 4-删除问题
        removeById(id);
    }

    @Override
    public PageDTO<QuestionVO> pageQueryQuestion(QuestionPageQuery query) {

        // 1-参数健壮性校验(课程id和小节id不能同时为空)
        Long courseId = query.getCourseId();
        Long sectionId = query.getSectionId();
        if (Objects.isNull(courseId) && Objects.isNull(sectionId)) {
            throw new BadRequestException("课程id和小节id不能同时为空");
        }

        // 2.分页查询基础数据
        Page<InteractionQuestion> page = lambdaQuery()
                .eq(query.getOnlyMine(), InteractionQuestion::getUserId, UserContext.getUser())
                .eq(null != courseId, InteractionQuestion::getCourseId, courseId)
                .eq(null != sectionId, InteractionQuestion::getSectionId, sectionId)
                .eq(InteractionQuestion::getHidden, false)
                .page(query.toMpPageDefaultSortByCreateTimeDesc());
        List<InteractionQuestion> records = page.getRecords();
        if (CollUtils.isEmpty(records)) {
            return PageDTO.empty(page);
        }

        // 3.查询用户信息和回答信息
        Set<Long> userIds = new HashSet<>();
        Set<Long> answerIds = new HashSet<>();

        // 3.1 获取全部的用户id和回答id
        for (InteractionQuestion record : records) {
            // 3.2 匿名用户无需返回用户信息，因此不用添加
            if (Boolean.FALSE.equals(record.getAnonymity())) {
                userIds.add(record.getUserId());
            }
            answerIds.add(record.getLatestAnswerId());
        }

        // 3.3 查询回答信息并组装成Map用作后续数据VO填充
        Map<Long, InteractionReply> replyMap = new HashMap<>();

        // 将所有回复中的userId拿到，因为reply表中也只有用户id，而后面组装VO需要name，所以提前合并查询
        Set<Long> replyUserIds = new HashSet<>();

        // 可能都是新创建的问题，没有任何回答，这里可能为空
        answerIds.remove(null);

        if (CollUtils.isNotEmpty(answerIds)) {
            List<InteractionReply> replies = replyMapper.selectBatchIds(answerIds);
            replyMap = replies.stream().collect(Collectors.toMap(InteractionReply::getId, v-> v));

            // 回复中的用户信息加进去
            replyUserIds = replies.stream().map(InteractionReply::getUserId).collect(Collectors.toSet());
            if (!replyUserIds.isEmpty()) {
                userIds.addAll(replyUserIds);
            }
        }

        // 3.4 查询用户信息并组装成Map用作后续数据VO填充
        Map<Long, UserDTO> userMap = new HashMap<>();
        List<UserDTO> userList = userClient.queryUserByIds(userIds);
        if (CollUtils.isNotEmpty(userList)) {
            userMap = userList.stream().collect(Collectors.toMap(UserDTO::getId, v->v));
        }

        // 4.封装VO
        List<QuestionVO> resultList = new ArrayList<>();

        for (InteractionQuestion record : records) {

            // 4.1 基础对象转换PO->VO
            QuestionVO vo = BeanUtils.copyBean(record, QuestionVO.class);

            // 4.2 封装回答者信息，使用userMap(匿名的用户这里map获取为空，所以只要存在就直接赋值就好)
            UserDTO user = userMap.getOrDefault(record.getUserId(), null);
            if (Objects.nonNull(user) && Boolean.TRUE.equals(!record.getAnonymity())) {
                vo.setUserId(user.getId());
                vo.setUserName(user.getName());
                vo.setUserIcon(user.getIcon());
            }

            // 4.3 封装最近一次回答信息，使用replyMap
            InteractionReply reply = replyMap.getOrDefault(record.getLatestAnswerId(), null);
            if (Objects.nonNull(reply)) {
                vo.setLatestReplyContent(reply.getContent());

                // 赋值最近一次回复用户id，使用3.3得到的map数据
                UserDTO replyUser = userMap.getOrDefault(reply.getUserId(), null);
                if (Objects.nonNull(replyUser)) {
                    vo.setLatestReplyUser(replyUser.getName());
                }
            }

            resultList.add(vo);
        }

        return PageDTO.of(page, resultList);
    }

    @Override
    public QuestionVO queryQuestionById(Long id) {

        // 1-根据ID查到原始数据
        InteractionQuestion question = getById(id);

        if (Objects.isNull(question) || Boolean.TRUE.equals(question.getHidden())) {
            // 没有数据或被隐藏了无需返回
            return null;
        }

        // 2-查询用户信息
        UserDTO user = null;
        if (Boolean.FALSE.equals(question.getAnonymity())) {
            user = userClient.queryUserById(question.getUserId());
        }

        // 3-基础信息填充
        QuestionVO vo = BeanUtils.copyBean(question, QuestionVO.class);

        // 4-用户信息填充
        if (Objects.nonNull(user)) {
            vo.setUserName(user.getName());
            vo.setUserIcon(user.getIcon());
        }
        return vo;
    }

    @Override
    public PageDTO<QuestionAdminVO> adminPageQueryQuestion(QuestionAdminPageQuery query) {

        // 1-根据课程名称获取课程id集合
        List<Long> courseIds = null;
        if (StringUtils.isNotEmpty(query.getCourseName())) {
            courseIds = searchClient.queryCoursesIdByName(query.getCourseName());
            if (CollUtils.isEmpty(courseIds)) {
                return PageDTO.empty(0L, 0L);
            }
        }

        // 2-分页查询基础数据
        Page<InteractionQuestion> page = lambdaQuery()
                .in(null != courseIds, InteractionQuestion::getCourseId, courseIds)
                .eq(null != query.getStatus(), InteractionQuestion::getStatus, query.getStatus())
                .gt(null != query.getBeginTime(), InteractionQuestion::getCreateTime, query.getBeginTime())
                .lt(null != query.getEndTime(), InteractionQuestion::getCreateTime, query.getEndTime())
                .page(query.toMpPageDefaultSortByCreateTimeDesc());
        List<InteractionQuestion> records = page.getRecords();
        if (CollUtils.isEmpty(records)) {
            return PageDTO.empty(page);
        }

        // 3-获取组装数据的外围参数：用户id、课程id、章节id
        Set<Long> userIds = new HashSet<>();
        Set<Long> cIds = new HashSet<>();
        Set<Long> cataIds = new HashSet<>();

        // 3.1-获取各种数据id集合(因为是管理端，所以不用管是否隐藏)
        for (InteractionQuestion r : records) {
            userIds.add(r.getUserId());
            cIds.add(r.getCourseId());
            cataIds.add(r.getChapterId());
            cataIds.add(r.getSectionId());
        }

        // 3.2-根据id查用户信息
        List<UserDTO> userList = userClient.queryUserByIds(userIds);
        Map<Long, UserDTO> userMap = new HashMap<>(userList.size());
        if (CollUtils.isNotEmpty(userList)) {
            userMap = userList.stream().collect(Collectors.toMap(UserDTO::getId, u -> u));
        }

        // 3.3-根据id查课程信息
        List<CourseSimpleInfoDTO> cInfoList = courseClient.getSimpleInfoList(courseIds);
        Map<Long, CourseSimpleInfoDTO> cInfoMap = new HashMap<>(cInfoList.size());
        if (CollUtils.isNotEmpty(cInfoList)) {
            cInfoMap = cInfoList.stream().collect(Collectors.toMap(CourseSimpleInfoDTO::getId, c -> c));
        }

        // 3.4-根据id查章节
        List<CataSimpleInfoDTO> cataList = catalogueClient.batchQueryCatalogue(cataIds);
        Map<Long, String> cataMap = new HashMap<>(cataList.size());
        if (CollUtils.isNotEmpty(cataList)) {
            cataMap = cataList.stream().collect(Collectors.toMap(CataSimpleInfoDTO::getId, CataSimpleInfoDTO::getName));
        }

        // 4-封装VO
        List<QuestionAdminVO> resultList = new ArrayList<>(records.size());
        for (InteractionQuestion question : records) {

            // 4.1-基础属性拷贝
            QuestionAdminVO vo = BeanUtils.copyBean(question, QuestionAdminVO.class);

            // 4.2-填充用户信息
            UserDTO user = userMap.get(question.getUserId());
            if (Objects.nonNull(user)) {
                vo.setUserName(user.getName());
            }

            // 4.3-填充课程名称
            CourseSimpleInfoDTO courseInfo = cInfoMap.get(question.getCourseId());
            if (Objects.nonNull(courseInfo)) {
                vo.setCourseName(courseInfo.getName());
                // 填充分类信息(调用封装好的API)
                vo.setCategoryName(categoryCache.getCategoryNames(courseInfo.getCategoryIds()));
            }

            // 4.4-填充章节信息
            vo.setChapterName(cataMap.getOrDefault(question.getChapterId(), ""));
            vo.setSectionName(cataMap.getOrDefault(question.getSectionId(), ""));

            resultList.add(vo);
        }

        return PageDTO.of(page, resultList);
    }

    @Override
    public QuestionAdminVO adminQueryQuestionById(Long id) {

        // 1-查询基础数据
        InteractionQuestion question = getById(id);
        if (Objects.isNull(question)) {
            throw new BadRequestException("互动问题不存在");
        }

        // 2-基础属性拷贝
        QuestionAdminVO vo = BeanUtils.copyBean(question, QuestionAdminVO.class);

        // 2-查询用户信息
        UserDTO user = userClient.queryUserById(question.getUserId());
        if (Objects.nonNull(user)) {
            vo.setUserName(user.getName());
            vo.setUserIcon(user.getIcon());
        }

        // 3-查询课程信息
        CourseFullInfoDTO courseInfo = courseClient.getCourseInfoById(question.getCourseId(), true, true);
        if (Objects.nonNull(courseInfo)) {
            vo.setCourseName(courseInfo.getName());
            vo.setCategoryName(categoryCache.getCategoryNames(courseInfo.getCategoryIds()));
            // 获取授课教师信息
            List<Long> teacherIds = courseInfo.getTeacherIds();
            if (CollUtils.isNotEmpty(teacherIds)) {
                List<UserDTO> tearcherList = userClient.queryUserByIds(teacherIds);
                if (CollUtils.isNotEmpty(tearcherList)) {
                    vo.setTeacherName(tearcherList.stream()
                            .map(UserDTO::getName).collect(Collectors.joining("/")));
                }
            }
        }

        // 4-查询章节信息
        List<CataSimpleInfoDTO> catas = catalogueClient.batchQueryCatalogue(
                List.of(question.getChapterId(), question.getSectionId()));
        Map<Long, String> cataMap = new HashMap<>(catas.size());
        if (CollUtils.isNotEmpty(catas)) {
            cataMap = catas.stream()
                    .collect(Collectors.toMap(CataSimpleInfoDTO::getId, CataSimpleInfoDTO::getName));
        }
        vo.setChapterName(cataMap.getOrDefault(question.getChapterId(), ""));
        vo.setSectionName(cataMap.getOrDefault(question.getSectionId(), ""));

        return vo;
    }

    @Override
    public void hiddenQuestion(Long id, Boolean hidden) {
        InteractionQuestion question = new InteractionQuestion();
        question.setId(id);
        question.setHidden(hidden);
        updateById(question);
    }
}
