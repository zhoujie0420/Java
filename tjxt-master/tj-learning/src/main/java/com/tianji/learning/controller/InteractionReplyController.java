package com.tianji.learning.controller;


import com.tianji.common.domain.dto.PageDTO;
import com.tianji.learning.domain.dto.ReplyDTO;
import com.tianji.learning.domain.po.InteractionReply;
import com.tianji.learning.domain.query.ReplyPageQuery;
import com.tianji.learning.domain.vo.ReplyVO;
import com.tianji.learning.service.IInteractionReplyService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.checkerframework.checker.units.qual.A;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * <p>
 * 互动问题的回答或评论 前端控制器
 * </p>
 *
 * @author hb
 * @since 2023-04-19
 */
@Api(tags = "互动问答相关接口")
@RestController
@RequestMapping("/replies")
public class InteractionReplyController {

    @Resource
    private IInteractionReplyService replyService;

    @ApiOperation("新增回答或评论")
    @PostMapping
    public void saveReply(@RequestBody ReplyDTO replyDTO) {
        replyService.saveReply(replyDTO);
    }

    @ApiOperation("分页查询回答或评论")
    @GetMapping("page")
    public PageDTO<ReplyVO> queryReplyPage(ReplyPageQuery pageQuery){
        return replyService.queryReplyPage(pageQuery, false);
    }
}
