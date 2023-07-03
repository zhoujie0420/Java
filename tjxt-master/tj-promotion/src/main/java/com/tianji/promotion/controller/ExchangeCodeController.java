package com.tianji.promotion.controller;

import com.tianji.common.domain.dto.PageDTO;
import com.tianji.promotion.domain.query.CodeQuery;
import com.tianji.promotion.service.IExchangeCodeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;

@Api(tags = "兑换码相关接口")
@RestController
@RequestMapping("/codes")
public class ExchangeCodeController {

    @Resource
    private IExchangeCodeService codeService;

    @ApiOperation("分页查询兑换码")
    @GetMapping("page")
    public PageDTO<String> queryCodePage(@Valid CodeQuery query){
        return codeService.queryCodePage(query);
    }

}
