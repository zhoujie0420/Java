package com.tianji.promotion.controller;

import com.tianji.common.domain.dto.PageDTO;
import com.tianji.promotion.domain.dto.CouponFormDTO;
import com.tianji.promotion.domain.dto.CouponIssueFormDTO;
import com.tianji.promotion.domain.query.CouponQuery;
import com.tianji.promotion.domain.vo.CouponDetailVO;
import com.tianji.promotion.domain.vo.CouponPageVO;
import com.tianji.promotion.domain.vo.CouponVO;
import com.tianji.promotion.service.ICouponService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/coupons")
@Api(tags = "优惠券管理相关接口")
public class CouponController {

    @Resource
    private ICouponService couponService;

    @PostMapping
    @ApiOperation("新增优惠券接口")
    public void saveCoupon(@RequestBody @Valid CouponFormDTO coupon) {
        couponService.saveCoupon(coupon);
    }

    @ApiOperation("根据id查询优惠券")
    @GetMapping("{id}")
    public CouponDetailVO queryCouponById(@ApiParam("优惠券id") @PathVariable("id") Long id){
        return couponService.queryCouponById(id);
    }

    @ApiOperation("分页查询优惠券")
    @GetMapping("page")
    public PageDTO<CouponPageVO> queryCouponPage(CouponQuery query){
        return couponService.queryCouponPage(query);
    }

    @ApiOperation("修改优惠券")
    @PutMapping("{id}")
    public void updateCoupon(
            @ApiParam("优惠券id") @PathVariable("id") Long id,
            @RequestBody CouponFormDTO couponDTO){
        couponDTO.setId(id);
        couponService.updateCoupon(couponDTO);
    }

    @ApiOperation("删除优惠券")
    @DeleteMapping("{id}")
    public void deleteById(@ApiParam("优惠券id") @PathVariable("id") Long id) {
        couponService.deleteById(id);
    }

    @ApiOperation("发放优惠券")
    @PutMapping("/{id}/begin")
    public void beginIssue(@ApiParam("优惠券id") @PathVariable("id") Long id, @RequestBody @Valid CouponIssueFormDTO issue) {
        issue.setId(id);
        couponService.beginIssue(issue);
    }

    @ApiOperation("暂停优惠券发放")
    @PutMapping("/{id}/pause")
    public void pauseIssue(@ApiParam("优惠券id") @PathVariable("id") Long id) {
        couponService.pauseIssue(id);
    }

    @ApiOperation("查询发放中优惠券")
    @GetMapping("/list")
    public List<CouponVO> queryIssuingCoupons() {
        return couponService.queryIssuingCoupons();
    }
}
