package com.tianji.remark.controller;


import com.tianji.remark.domain.dto.LikeRecordFormDTO;
import com.tianji.remark.service.ILikedRecordService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.Set;

/**
 * <p>
 * 点赞记录表 前端控制器
 * </p>
 *
 * @author hb
 * @since 2023-04-20
 */
@Api(tags = "点赞业务相关接口")
@RestController
@RequestMapping("/likes")
public class LikedRecordController {

    @Resource
    private ILikedRecordService recordService;

    @PostMapping
    @ApiOperation("点赞或取消点赞")
    public void likeOrNot(@RequestBody LikeRecordFormDTO recordForm) {
        recordService.likeOrNot(recordForm);
    }

    @GetMapping("list")
    @ApiOperation("查询指定业务数据是否点过赞，点过就返回")
    public Set<Long> isBizLiked(@RequestParam("bizIds") List<Long> bizIds) {
        return recordService.isBizLiked(bizIds);
    }
}
