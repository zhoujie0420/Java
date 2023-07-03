package com.tianji.learning.service.impl;

import cn.hutool.log.Log;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tianji.api.client.course.CatalogueClient;
import com.tianji.api.client.course.CourseClient;
import com.tianji.api.dto.IdAndNumDTO;
import com.tianji.api.dto.course.CataSimpleInfoDTO;
import com.tianji.api.dto.course.CourseFullInfoDTO;
import com.tianji.api.dto.course.CourseSimpleInfoDTO;
import com.tianji.api.dto.trade.OrderBasicDTO;
import com.tianji.common.domain.dto.PageDTO;
import com.tianji.common.domain.query.PageQuery;
import com.tianji.common.exceptions.BadRequestException;
import com.tianji.common.utils.*;
import com.tianji.learning.domain.dto.LearningPlanDTO;
import com.tianji.learning.domain.po.LearningLesson;
import com.tianji.learning.domain.po.LearningRecord;
import com.tianji.learning.domain.vo.LearningLessonVO;
import com.tianji.learning.domain.vo.LearningPlanPageVO;
import com.tianji.learning.domain.vo.LearningPlanVO;
import com.tianji.learning.enums.LessonStatus;
import com.tianji.learning.enums.PlanStatus;
import com.tianji.learning.mapper.LearningLessonMapper;
import com.tianji.learning.mapper.LearningRecordMapper;
import com.tianji.learning.service.ILearningLessonService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.w3c.dom.stylesheets.LinkStyle;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * 学生课程表 服务实现类
 * </p>
 *
 * @author hb
 * @since 2023-04-16
 */
@Slf4j
@Service
public class LearningLessonServiceImpl extends ServiceImpl<LearningLessonMapper, LearningLesson> implements ILearningLessonService {

    @Resource
    private CourseClient courseClient;

    @Resource
    private CatalogueClient catalogueClient;

    @Resource
    private LearningRecordMapper recordMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addUserLessons(OrderBasicDTO order) {
        // 查询课程有效期
        List<CourseSimpleInfoDTO> cInfoList = courseClient.getSimpleInfoList(order.getCourseIds());
        if(CollUtils.isEmpty(cInfoList)){
            //课程不存在，无法添加
            log.error("课程信息不存在");
            return;
        }
        ArrayList<LearningLesson> list = new ArrayList<>(cInfoList.size());
        for (CourseSimpleInfoDTO cInfo : cInfoList) {
            LearningLesson lesson = new LearningLesson();
            Integer validDuration = cInfo.getValidDuration();
            if(validDuration != null && validDuration > 0){
                LocalDateTime now = LocalDateTime.now();
                lesson.setCreateTime(now);
                lesson.setExpireTime(now.plusMonths(validDuration));
            }
            // 填充userid和 courseId
            lesson.setUserId(order.getUserId());
            lesson.setCourseId(order.getOrderId());
            list.add(lesson);
        }
        saveBatch(list);
    }

    @Override
    public PageDTO<LearningLessonVO> queryMyLessons(PageQuery query) {
        //获取当前登录信息
        Long user = UserContext.getUser();
        Page<LearningLesson> page = lambdaQuery()
                .eq(LearningLesson::getUserId, user)
                .page(query.toMpPage("latest_learn_time", false));
        List<LearningLesson> records = page.getRecords();
        if (CollUtils.isEmpty(records)) {
            return PageDTO.empty(page);
        }

        //查询课表信息
        Map<Long, CourseSimpleInfoDTO> cMap = queryCourseSimpleInfoList(records);

        List<LearningLessonVO> learningLessons = new ArrayList<>(records.size());
        for (LearningLesson r : records) {
            LearningLessonVO vo = BeanUtils.copyBean(r, LearningLessonVO.class);
            CourseSimpleInfoDTO cInfo = cMap.get(r.getCourseId());
            vo.setCourseName(cInfo.getName());
            vo.setCourseCoverUrl(cInfo.getCoverUrl());
            vo.setSections(cInfo.getSectionNum());
            learningLessons.add(vo);
        }
        return PageDTO.of(page,learningLessons);


    }

    private Map<Long, CourseSimpleInfoDTO> queryCourseSimpleInfoList(List<LearningLesson> records) {
        Set<Long> cIds = records.stream().map(LearningLesson::getCourseId).collect(Collectors.toSet());
        List<CourseSimpleInfoDTO> simpleInfoList = courseClient.getSimpleInfoList(cIds);
        if(CollUtils.isEmpty(simpleInfoList)){
            throw new BadRequestException("课程信息不存在");
        }
        Map<Long, CourseSimpleInfoDTO> collect = simpleInfoList.stream()
                .collect(Collectors.toMap(CourseSimpleInfoDTO::getId, c -> c));
        return collect;

    }


//
//        // 3-查询课程信息，用户后续vo填充
//        List<Long> courseIds = records.stream()
//                .map(LearningLesson::getCourseId)
//                .distinct()
//                .collect(Collectors.toList());
//
//        List<CourseSimpleInfoDTO> courseInfoList = courseClient.getSimpleInfoList(courseIds);
//        if (CollUtils.isEmpty(courseInfoList)) {
//            throw new BadRequestException("课程信息不存在");
//        }
//
//        // 聚合分组
//        Map<Long, CourseSimpleInfoDTO> courseMap = courseInfoList.stream()
//                .collect(Collectors.toMap(CourseSimpleInfoDTO::getId, a -> a));
//
//        // 4-VO组装
//        List<LearningLessonVO> result = new ArrayList<>(records.size());
//        for (LearningLesson record : records) {
//            // 拷贝基础属性
//            LearningLessonVO vo = BeanUtils.copyBean(record, LearningLessonVO.class);
//            // 获取课程详细信息
//            CourseSimpleInfoDTO courseInfo = courseMap.get(vo.getCourseId());
//
//            vo.setCourseName(courseInfo.getName());
//            vo.setCourseCoverUrl(courseInfo.getCoverUrl());
//            vo.setSections(courseInfo.getSectionNum());
//
//            result.add(vo);
//        }
//
//        return new PageDTO<>(page.getTotal(), page.getPages(), result);


    @Override
    public LearningLessonVO queryMyCurrentLesson() {
        Long userId = UserContext.getUser();
        LearningLesson lesson = lambdaQuery()
                .eq(LearningLesson::getUserId, userId)
                .eq(LearningLesson::getStatus, LessonStatus.LEARNING.getValue())
                .orderByDesc(LearningLesson::getLatestLearnTime)
                .last("limit 1")
                .one();
        if(lesson == null){
            return null;
        }
        LearningLessonVO vo = BeanUtils.copyBean(lesson, LearningLessonVO.class);
        CourseFullInfoDTO cInfo  = courseClient.getCourseInfoById(lesson.getCourseId(), false, false);
        if(cInfo == null){
            throw  new BadRequestException("课程不存在");
        }
        vo.setCourseName(cInfo.getName());
        vo.setCourseCoverUrl(cInfo.getCoverUrl());
        vo.setSections(cInfo.getSectionNum());

        Integer count = lambdaQuery()
                .eq(LearningLesson::getUserId, userId)
                .count();
        vo.setCourseAmount(count);
        List<CataSimpleInfoDTO> cataInfos  =
                catalogueClient.batchQueryCatalogue(CollUtils.singletonList(lesson.getLatestSectionId()));
        if (!CollUtils.isEmpty(cataInfos)){
            CataSimpleInfoDTO cataSimpleInfoDTO = cataInfos.get(0);
            vo.setLatestSectionIndex(cataSimpleInfoDTO.getCIndex());
            vo.setLatestSectionName(cataSimpleInfoDTO.getName());
        }
        return vo;

    }

    @Override
    public void removeUserLessons(OrderBasicDTO order) {
        this.removeByIds(order.getCourseIds());
    }

    @Override
    public void deleteLesson(Long courseId) {
        this.removeById(courseId);
    }

    @Override
    public LearningLessonVO queryThisLessonLearningStatus(Long courseId) {

        // 1-查询课程基本信息(课程ID+用户ID，只会查询到一条数据)
        Long userId = UserContext.getUser();
        LearningLesson lesson = lambdaQuery()
                .eq(LearningLesson::getCourseId, courseId)
                .eq(LearningLesson::getUserId, userId)
                .last("limit 1")
                .one();

        if (Objects.isNull(lesson)) {
            throw new BadRequestException("课程信息不存在");
        }

        // 2-基础信息拷贝
        LearningLessonVO lessonVO = BeanUtils.copyBean(lesson, LearningLessonVO.class);

        // 3-查询课程信息
        CourseFullInfoDTO courseInfo = courseClient.getCourseInfoById(lessonVO.getCourseId(), false, false);
        if (Objects.isNull(courseInfo)) {
            throw new BadRequestException("课程不存在");
        }
        lessonVO.setCourseName(courseInfo.getName());
        lessonVO.setCourseCoverUrl(courseInfo.getCoverUrl());
        lessonVO.setSections(courseInfo.getSectionNum());

        return lessonVO;
    }

    @Override
    public Long validLesson(Long courseId) {

        // 1-获取当前登录用户信息
        Long userId = UserContext.getUser();

        // 2-查询当前课程信息
        LearningLesson lesson = lambdaQuery()
                .eq(LearningLesson::getCourseId, courseId)
                .eq(LearningLesson::getUserId, userId)
                .last("limit 1")
                .one();
        if (Objects.isNull(lesson)) {
            throw new BadRequestException("课程信息不存在");
        }

        // 3-判断课程状态是否有效
        if (lesson.getStatus().getValue() == LessonStatus.EXPIRED.getValue()) {
            log.error("当前课程信息状态已过期");
            return null;
        }

        return lesson.getId();
    }

    @Override
    public Integer countLearningLessonByCourse(Long courseId) {
        // 统计查询
       return lambdaQuery()
                .eq(LearningLesson::getCourseId, courseId)
                .eq(LearningLesson::getStatus, LessonStatus.LEARNING.getValue())
                .count();
    }

    @Override
    public LearningLesson queryByUserIdAndCourseId(Long userId, Long courseId) {
        return lambdaQuery()
                .eq(LearningLesson::getCourseId, courseId)
                .eq(LearningLesson::getUserId, userId)
                .last("limit 1")
                .one();
    }

    @Override
    public void createLearningPlans(LearningPlanDTO learningPlan) {

        // 1-查询课表中的数据
        Long userId = UserContext.getUser();
        LearningLesson lesson = queryByUserIdAndCourseId(userId, learningPlan.getCourseId());
        AssertUtils.isNotNull(lesson, "课程信息不存在");

        // 2-更新数据(因为只修改个别字段，所以我们新搞一个对象)
        LearningLesson updateEntity = new LearningLesson();
        updateEntity.setId(lesson.getId());
        updateEntity.setWeekFreq(learningPlan.getFreq());
        // 首次创建时应该改成计划进行中
        if (lesson.getPlanStatus() == PlanStatus.NO_PLAN) {
            updateEntity.setPlanStatus(PlanStatus.PLAN_RUNNING);
        }

        this.updateById(updateEntity);
    }

    @Override
    public LearningPlanPageVO queryMyPlans(PageQuery query) {

        LearningPlanPageVO result = new LearningPlanPageVO();

        // 1-查询当前登录用户信息
        Long userId = UserContext.getUser();

        // 2-获取本周起始时间(用于本周计划的查询条件)
        LocalDateTime weekBeginTime = DateUtils.getWeekBeginTime(LocalDate.now());
        LocalDateTime weekEndTime = DateUtils.getWeekEndTime(LocalDate.now());

        // 3-查询总的统计数据
        // 3.1-本周总的已学习小节数量
        Integer weekFinishedCount = recordMapper.selectCount(new LambdaQueryWrapper<LearningRecord>()
                .eq(LearningRecord::getUserId, userId)
                .eq(LearningRecord::getFinished, true)
                .gt(LearningRecord::getFinishTime, weekBeginTime)
                .lt(LearningRecord::getFinishTime, weekEndTime));
        result.setWeekFinished(weekFinishedCount);

        // 3.2-本周总的计划学习小节数量
        Integer totalPlan = getBaseMapper().queryTotalPlan(userId);
        result.setWeekTotalPlan(totalPlan);

        // TODO 3.3-本周学习积分 后续补充

        // 4-查询下面分页列表数据
        // 4.1-分页查询课表信息及学习计划信息
        Page<LearningLesson> page = lambdaQuery()
                .eq(LearningLesson::getUserId, userId)
                .eq(LearningLesson::getPlanStatus, PlanStatus.PLAN_RUNNING)
                .in(LearningLesson::getStatus, LessonStatus.NOT_BEGIN, LessonStatus.LEARNING)
                .page(query.toMpPage("latest_learn_time", false));
        List<LearningLesson> records = page.getRecords();
        if (CollUtils.isEmpty(records)) {
            return result;
        }

        // 4.2-查询课表对应的课程信息
        Map<Long, CourseSimpleInfoDTO> courseMap = queryAndConvertCourse(records);

        // 4.3-统计每一个课程本周已学习小节数量
        List<IdAndNumDTO> list = recordMapper.countLearnedSections(userId, weekBeginTime, weekEndTime);
        Map<Long, Integer> countMap = IdAndNumDTO.toMap(list);

        // 4.4-组装数据VO
        List<LearningPlanVO> voList = new ArrayList<>(records.size());
        for (LearningLesson r : records) {
            // 4.4.1-拷贝基础属性
            LearningPlanVO vo = BeanUtils.copyBean(r, LearningPlanVO.class);
            // 4.4.2-填充课程详细信息
            CourseSimpleInfoDTO cInfo = courseMap.get(r.getCourseId());
            if (Objects.nonNull(cInfo)) {
                vo.setCourseName(cInfo.getName());
                vo.setSections(cInfo.getSectionNum());
            }
            // 4.4.3-每个课程的本周已学小结数量
            Integer alreadyLearn = countMap.getOrDefault(r.getId(), 0);
            vo.setWeekLearnedSections(alreadyLearn);

            voList.add(vo);
        }
        return result.pageInfo(page.getTotal(), page.getPages(), voList);
    }

    public Map<Long, CourseSimpleInfoDTO> queryAndConvertCourse(List<LearningLesson> records) {
        List<Long> courseIds = records.stream()
                .map(LearningLesson::getCourseId)
                .distinct()
                .collect(Collectors.toList());

        List<CourseSimpleInfoDTO> courseInfoList = courseClient.getSimpleInfoList(courseIds);
        if (CollUtils.isEmpty(courseInfoList)) {
            throw new BadRequestException("课程信息不存在");
        }

        // 聚合分组
        return courseInfoList.stream()
                .collect(Collectors.toMap(CourseSimpleInfoDTO::getId, a -> a));
    }
}
