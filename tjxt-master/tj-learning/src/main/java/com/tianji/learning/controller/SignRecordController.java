package com.tianji.learning.controller;

import com.tianji.learning.domain.vo.SignResultVO;
import com.tianji.learning.service.ISignRecordService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @Description:
 * @Date: 2023/4/21 16:42
 */

@Api(tags = "签到相关接口")
@RestController
@RequestMapping("sign-records")
public class SignRecordController {


    @Resource
    private ISignRecordService recordService;

    @PostMapping
    @ApiOperation("签到功能接口")
    public SignResultVO addSignRecords(){
        return recordService.addSignRecords();
    }

    @GetMapping
    @ApiOperation("查询签到记录")
    public Byte[] querySignRecords(){
        return recordService.querySignRecords();
    }
}
