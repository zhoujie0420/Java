package com.tianji.learning.service;

import com.tianji.api.dto.trade.OrderBasicDTO;
import com.tianji.common.domain.dto.PageDTO;
import com.tianji.common.domain.query.PageQuery;
import com.tianji.learning.domain.dto.LearningPlanDTO;
import com.tianji.learning.domain.po.LearningLesson;
import com.baomidou.mybatisplus.extension.service.IService;
import com.tianji.learning.domain.vo.LearningLessonVO;
import com.tianji.learning.domain.vo.LearningPlanPageVO;
import com.tianji.learning.domain.vo.LearningPlanVO;

/**
 * <p>
 * 学生课程表 服务类
 * </p>
 *
 * @author hb
 * @since 2023-04-16
 */
public interface ILearningLessonService extends IService<LearningLesson> {

    void addUserLessons(OrderBasicDTO order);

    PageDTO<LearningLessonVO> queryMyLessons(PageQuery query);

    LearningLessonVO queryMyCurrentLesson();

    void removeUserLessons(OrderBasicDTO order);

    void deleteLesson(Long courseId);

    LearningLessonVO queryThisLessonLearningStatus(Long courseId);

    Long validLesson(Long courseId);

    Integer countLearningLessonByCourse(Long courseId);

    LearningLesson queryByUserIdAndCourseId(Long userId, Long courseId);

    void createLearningPlans(LearningPlanDTO learningPlan);

    LearningPlanPageVO queryMyPlans(PageQuery query);
}
