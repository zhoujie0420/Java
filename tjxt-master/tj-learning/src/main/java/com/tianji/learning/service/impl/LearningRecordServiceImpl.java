package com.tianji.learning.service.impl;

import com.tianji.api.client.course.CourseClient;
import com.tianji.api.dto.course.CourseFullInfoDTO;
import com.tianji.api.dto.leanring.LearningLessonDTO;
import com.tianji.api.dto.leanring.LearningRecordDTO;
import com.tianji.api.dto.leanring.LearningRecordFormDTO;
import com.tianji.common.exceptions.BizIllegalException;
import com.tianji.common.exceptions.DbException;
import com.tianji.common.utils.BeanUtils;
import com.tianji.common.utils.UserContext;
import com.tianji.learning.domain.po.LearningLesson;
import com.tianji.learning.domain.po.LearningRecord;
import com.tianji.learning.enums.LessonStatus;
import com.tianji.learning.enums.SectionType;
import com.tianji.learning.mapper.LearningRecordMapper;
import com.tianji.learning.service.ILearningLessonService;
import com.tianji.learning.service.ILearningRecordService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tianji.learning.util.LearningRecordDelayTaskHandler;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;

/**
 * <p>
 * 学习记录表 服务实现类
 * </p>
 *
 * @author hb
 * @since 2023-04-17
 */
@Service
public class LearningRecordServiceImpl extends ServiceImpl<LearningRecordMapper, LearningRecord> implements ILearningRecordService {

    @Resource
    private ILearningLessonService lessonService;

    @Resource
    private CourseClient courseClient;

    @Resource
    private LearningRecordDelayTaskHandler taskHandler;

    @Override
    public LearningLessonDTO queryLearningRecordByCourse(Long courseId) {

        // 1-获取登陆信息
        Long userId = UserContext.getUser();

        // 2-查询课表
        LearningLesson lesson = lessonService.queryByUserIdAndCourseId(userId, courseId);
        if (Objects.isNull(lesson)) {
            return null;
        }

        // 3-查询指定课程的学习记录
        // SELECT * FROM xx WHERE LESSON_ID = xx
        List<LearningRecord> records = lambdaQuery()
                .eq(LearningRecord::getLessonId, lesson.getId())
                .list();
        // 拷贝po->dto
        List<LearningRecordDTO> learningLessonDTOList = BeanUtils.copyList(records, LearningRecordDTO.class);

        // 4-封装返回结果
        LearningLessonDTO result = new LearningLessonDTO();
        result.setId(lesson.getId());
        result.setLatestSectionId(lesson.getLatestSectionId());
        result.setRecords(learningLessonDTOList);
        return result;
    }

    @Override
    @Transactional
    public void addLearningRecord(LearningRecordFormDTO learningRecordForm) {

        // 1-获取用户信息
        Long userId = UserContext.getUser();

        // 2-处理学习记录相关
        Boolean finished = false;
        if (learningRecordForm.getSectionType() == SectionType.VIDEO.getValue()) {
            // 2.1-处理视频相关
            finished = handleVideoRecord(userId, learningRecordForm);
        } else {
            // 2.2-处理考试相关
            finished = handleExamRecord(userId, learningRecordForm);
        }

        // 没有新学完的小节，无需更新课表中的学习进度
        if (!finished) {
            return;
        }

        // 3-处理课表相关
        handleLearningLesson(learningRecordForm);
    }

    /**
     * 更新课表相关信息
     * @param learningRecordForm    前端请求参数
     */
    private void handleLearningLesson(LearningRecordFormDTO learningRecordForm) {

        // 1-查询课表(用户后续比较是否已经全部学完)
        LearningLesson lesson = lessonService.getById(learningRecordForm.getLessonId());
        if (Objects.isNull(lesson)) {
            throw new BizIllegalException("课程不存在，无法更新数据");
        }

        // 2-判断是否有新完成小节，有则查询课程数据
        boolean allLearned = false;

        CourseFullInfoDTO courseInfoById =
                courseClient.getCourseInfoById(lesson.getCourseId(), false, false);
        if (Objects.isNull(courseInfoById)) {
            throw new BizIllegalException("课程不存在，无法更新数据");
        }
        // 3-比较课程是否已经全部学完：已学课程数量 VS 全部课程数量
        allLearned = lesson.getLearnedSections() + 1 >= courseInfoById.getSectionNum();

        // 4-更新课表
        lessonService.lambdaUpdate()
                // 第一次来学习的时候，需要更新为学习中
                .set(lesson.getLearnedSections() == LessonStatus.NOT_BEGIN.getValue(),
                        LearningLesson::getStatus, LessonStatus.LEARNING.getValue())
                // 都学完则更新课表状态为已学完
                .set(allLearned, LearningLesson::getStatus, LessonStatus.FINISHED.getValue())
                // 学完一小节，已学数量+1
                .setSql("learned_sections = learned_sections + 1")
                .eq(LearningLesson::getId, lesson.getId())
                .update();
    }

    /**
     * 处理视频类型的学习记录
     * @param userId                用户ID
     * @param learningRecordForm    学习记录相关信息
     * @return                      是否处理完成
     */
    private Boolean handleVideoRecord(Long userId, LearningRecordFormDTO learningRecordForm) {

        // 1-查询原学习记录是否存在(lesson_id + section_id)
        LearningRecord localData = getLearningRecord(learningRecordForm);

        // 2-判断是否存在
        if (Objects.isNull(localData)) {

            // 3-不存在则新增
            LearningRecord entity = BeanUtils.copyBean(learningRecordForm, LearningRecord.class);
            entity.setUserId(userId);
            // 视频类-第一次肯定不是已完成，这里数据库默认是false，不填充也没关系
            entity.setFinished(false);
            boolean success = save(entity);
            if (!success) {
                throw new DbException("新增视频类型学习记录失败");
            }

            // 第一次，还没学完无需操作已学习小节数，返回false，
            return false;
        }

        // 4-存在则更新
        // 4.1-判断是否是第一次完成(完成状态为未完成 且 播放进度超过50%)
        boolean finished = !localData.getFinished()
                && learningRecordForm.getMoment() * 2 >= learningRecordForm.getDuration();

        if (!finished) {
            LearningRecord record = new LearningRecord();
            record.setId(localData.getId());
            record.setFinished(localData.getFinished());
            record.setLessonId(learningRecordForm.getLessonId());
            record.setSectionId(learningRecordForm.getSectionId());
            record.setMoment(learningRecordForm.getMoment());
            taskHandler.addLearningRecordTask(record);

            return false;
        }

        // 4-2-执行更新(set这块借助于mp的动态判断，true再更新finish相关的两个字段)
        boolean success = lambdaUpdate()
                .set(LearningRecord::getMoment, learningRecordForm.getMoment())
                .set(LearningRecord::getFinished, true)
                .set(LearningRecord::getFinishTime, learningRecordForm.getCommitTime())
                .eq(LearningRecord::getId, localData.getId())
                .update();
        if (!success) {
            throw new DbException("更新学习记录失败");
        }

        // 4-3-删除缓存
        taskHandler.cleanRecordCache(learningRecordForm.getLessonId(), learningRecordForm.getSectionId());

        return true;
    }

    private LearningRecord getLearningRecord(LearningRecordFormDTO learningRecordForm) {

        // 1-首先查询缓存数据
        LearningRecord cacheRecord = taskHandler.readRecordCache(learningRecordForm.getLessonId(), learningRecordForm.getSectionId());

        // 2-缓存有则直接返回
        if (Objects.nonNull(cacheRecord)) {
            return cacheRecord;
        }

        // 3-缓存没有则查询数据库
        cacheRecord = lambdaQuery()
                .eq(LearningRecord::getLessonId, learningRecordForm.getLessonId())
                .eq(LearningRecord::getSectionId, learningRecordForm.getSectionId())
                .one();
        // 4-数据库数据写入缓存
        taskHandler.writeRecordCache(cacheRecord);

        return cacheRecord;
    }

    /**
     * 处理考试类型的学习记录
     * @param userId                用户ID
     * @param learningRecordForm    学习记录相关信息
     * @return                      是否处理完成
     */
    private Boolean handleExamRecord(Long userId, LearningRecordFormDTO learningRecordForm) {

        // 1-拷贝基础属性（DTO->PO）
        LearningRecord entity = BeanUtils.copyBean(learningRecordForm, LearningRecord.class);

        // 2-填充缺失属性
        entity.setUserId(userId);
        // 考试类型提交及为完成
        entity.setFinished(true);
        entity.setFinishTime(learningRecordForm.getCommitTime());

        // 3-操作数据库
        boolean success = save(entity);
        if (!success) {
            throw new DbException("新增考试类型学习记录失败");
        }

        return true;
    }
}
