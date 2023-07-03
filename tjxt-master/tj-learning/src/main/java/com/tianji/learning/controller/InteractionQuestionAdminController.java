package com.tianji.learning.controller;


import com.tianji.common.domain.dto.PageDTO;
import com.tianji.learning.domain.query.QuestionAdminPageQuery;
import com.tianji.learning.domain.vo.QuestionAdminVO;
import com.tianji.learning.domain.vo.QuestionVO;
import com.tianji.learning.service.IInteractionQuestionService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * <p>
 * 互动提问的问题表 前端控制器
 * </p>
 *
 * @author hb
 * @since 2023-04-19
 */
@Api(tags = "问答系统管理端相关接口")
@RestController
@RequestMapping("/admin/questions")
public class InteractionQuestionAdminController {

    @Resource
    private IInteractionQuestionService questionService;


    @GetMapping("page")
    @ApiOperation("管理端分页查询问题")
    public PageDTO<QuestionAdminVO> adminPageQueryQuestion(QuestionAdminPageQuery query) {
        return questionService.adminPageQueryQuestion(query);
    }

    @GetMapping("/{id}")
    @ApiOperation("管理端ID查询问题详情")
    public QuestionAdminVO queryQuestionById(@PathVariable("id") Long id) {
        return questionService.adminQueryQuestionById(id);
    }

    @ApiOperation("隐藏或显示问题")
    @PutMapping("/{id}/hidden/{hidden}")
    public void hiddenQuestion(
            @ApiParam(value = "问题id", example = "1") @PathVariable("id") Long id,
            @ApiParam(value = "是否隐藏，true/false", example = "true") @PathVariable("hidden") Boolean hidden
    ){
        questionService.hiddenQuestion(id, hidden);
    }
}
