package com.tianji.learning.controller;


import com.tianji.common.domain.dto.PageDTO;
import com.tianji.common.domain.query.PageQuery;
import com.tianji.learning.domain.dto.LearningPlanDTO;
import com.tianji.learning.domain.vo.LearningLessonVO;
import com.tianji.learning.domain.vo.LearningPlanPageVO;
import com.tianji.learning.domain.vo.LearningPlanVO;
import com.tianji.learning.service.ILearningLessonService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;

/**
 * <p>
 * 学生课程表 前端控制器
 * </p>
 *
 * @author hb
 * @since 2023-04-16
 */
@Api(tags = "我的课表相关接口")
@RestController
@RequestMapping("/lessons")
public class LearningLessonController {

    @Resource
    private ILearningLessonService lessonService;

    @GetMapping("/page")
    @ApiOperation("分页查询我的课表")
    public PageDTO<LearningLessonVO> queryMyLessons(PageQuery query) {
        return lessonService.queryMyLessons(query);
    }

    @GetMapping("/now")
    @ApiOperation("查询我正在学习的课程")
    public LearningLessonVO queryMyCurrentLesson() {
        return lessonService.queryMyCurrentLesson();
    }

    @DeleteMapping("/delete")
    @ApiOperation("删除课程")
    public void deleteLesson(Long courseId) {
        lessonService.deleteLesson(courseId);
    }

    @GetMapping("/{courseId}")
    @ApiOperation("查询指定ID的课程学习状态")
    public LearningLessonVO queryThisLessonLearningStatus(@PathVariable("courseId") Long courseId) {
        return lessonService.queryThisLessonLearningStatus(courseId);
    }

    @GetMapping("/{courseId}/valid")
    @ApiOperation("校验当前用户是否可以播放当前视频")
    public Long validLesson(@PathVariable("courseId") Long courseId) {
        return lessonService.validLesson(courseId);
    }

    @GetMapping("/lessons/{courseId}/count")
    @ApiOperation("统计指定课程学习人数")
    public Integer countLearningLessonByCourse(@PathVariable("courseId") Long courseId) {
        return lessonService.countLearningLessonByCourse(courseId);
    }

    @ApiOperation("创建学习计划")
    @PostMapping("/plans")
    public void createLearningPlans(@Valid @RequestBody LearningPlanDTO learningPlan) {
        lessonService.createLearningPlans(learningPlan);
    }

    @ApiOperation("查询我的学习计划")
    @GetMapping("/plans")
    public LearningPlanPageVO queryMyPlans(PageQuery query) {
        return lessonService.queryMyPlans(query);
    }
}
