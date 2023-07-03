package com.tianji.learning.controller;


import com.tianji.common.domain.dto.PageDTO;
import com.tianji.learning.domain.dto.QuestionFormDTO;
import com.tianji.learning.domain.dto.QuestionFormUpdateDTO;
import com.tianji.learning.domain.query.QuestionPageQuery;
import com.tianji.learning.domain.vo.QuestionVO;
import com.tianji.learning.service.IInteractionQuestionService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;

/**
 * <p>
 * 互动提问的问题表 前端控制器
 * </p>
 *
 * @author hb
 * @since 2023-04-19
 */
@Api(tags = "问答系统相关接口")
@RestController
@RequestMapping("/questions")
public class InteractionQuestionController {

    @Resource
    private IInteractionQuestionService questionService;

    @PostMapping
    @ApiOperation("新增互动问题")
    public void saveQuestion(@RequestBody @Valid QuestionFormDTO questionForm) {
        questionService.saveQuestion(questionForm);
    }

    @PutMapping("/{id}")
    @ApiOperation("编辑互动问题")
    public void updateQuestion(@PathVariable("id") Long id, @RequestBody @Valid QuestionFormUpdateDTO questionForm) {
        questionService.updateQuestion(id, questionForm);
    }

    @DeleteMapping("/{id}")
    @ApiOperation("删除互动问题")
    public void updateQuestion(@PathVariable("id") Long id) {
        questionService.deleteQuestion(id);
    }

    @GetMapping("page")
    @ApiOperation("用户端分页查询问题")
    public PageDTO<QuestionVO> pageQueryQuestion(QuestionPageQuery query) {
        return questionService.pageQueryQuestion(query);
    }

    @GetMapping("/{id}")
    @ApiOperation("用户端ID查询问题详情")
    public QuestionVO queryQuestionById(@PathVariable("id") Long id) {
        return questionService.queryQuestionById(id);
    }

}
