package com.tianji.learning.controller;


import com.tianji.api.dto.leanring.LearningLessonDTO;
import com.tianji.api.dto.leanring.LearningRecordFormDTO;
import com.tianji.learning.domain.dto.LearningPlanDTO;
import com.tianji.learning.service.ILearningRecordService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;

/**
 * <p>
 * 学习记录表 前端控制器
 * </p>
 *
 * @author hb
 * @since 2023-04-17
 */
@Api(tags = "学习记录相关接口")
@RestController
@RequestMapping("/learning-records")
public class LearningRecordController {

    @Resource
    private ILearningRecordService recordService;

    @ApiOperation("查询指定课程的学习记录")
    @GetMapping("/course/{courseId}")
    public LearningLessonDTO queryLearningRecordByCourse(
            @ApiParam(value = "课程id", example = "2") @PathVariable("courseId") Long courseId) {
        return recordService.queryLearningRecordByCourse(courseId);
    }

    @ApiOperation("提交指定课程学习记录")
    @PostMapping
    public void addLearningRecord(@RequestBody LearningRecordFormDTO learningRecordForm) {
        recordService.addLearningRecord(learningRecordForm);
    }



}
