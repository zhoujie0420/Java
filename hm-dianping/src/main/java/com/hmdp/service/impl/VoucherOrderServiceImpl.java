package com.hmdp.service.impl;

import com.hmdp.dto.Result;
import com.hmdp.entity.SeckillVoucher;
import com.hmdp.entity.VoucherOrder;
import com.hmdp.mapper.VoucherOrderMapper;
import com.hmdp.service.ISeckillVoucherService;
import com.hmdp.service.IVoucherOrderService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hmdp.utils.RedisIdWorker;
import com.hmdp.utils.UserHolder;
import net.sf.jsqlparser.expression.operators.conditional.OrExpression;
import org.springframework.aop.framework.AopContext;
import org.springframework.boot.autoconfigure.aop.AopAutoConfiguration;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDateTime;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author 虎哥
 * @since 2021-12-22
 */
@Service
public class VoucherOrderServiceImpl extends ServiceImpl<VoucherOrderMapper, VoucherOrder> implements IVoucherOrderService {


    @Resource
     private ISeckillVoucherService seckillVoucherService;
    @Resource
     private RedisIdWorker redisIdWorker;


    @Override
    public Result seckillVoucher(Long voucherId) {
        // query coupon
        SeckillVoucher voucher = seckillVoucherService.getById(voucherId);
        // judge whether start?
        if(voucher.getBeginTime().isAfter(LocalDateTime.now())){
            // no
            return Result.fail("秒杀尚未开始");
        }
        // judge whether end;
        if(voucher.getEndTime().isBefore(LocalDateTime.now())){
            // yes
            return Result.fail("秒杀已经结束");
        }
        // judge whether stock ?
        if(voucher.getStock() < 1){
            // yes
            return Result.fail("库存不足");
        }
        // success -> create order
        Long userId = UserHolder.getUser().getId();
        synchronized (userId.toString().intern()) {
            //获取代理对象（事务）
            IVoucherOrderService proxy = (IVoucherOrderService) AopContext.currentProxy();
            return proxy.createVoucherOrder(voucherId);
        }
    }

    @Transactional
    public Result createVoucherOrder(Long voucherId) {
        // one people one order
        Long userId = UserHolder.getUser().getId();

            // query order
            int count = query().eq("user_id",userId).eq("voucher_id",voucherId).count();
            // exists?
            if(count > 0){
                return Result.fail("用户已经购买过");
            }

            // reduce stock
            boolean success = seckillVoucherService.update()
                    .setSql("stock = stock - 1")  // set stock = stock - 1
                    .eq("voucher_id", voucherId).gt("stock", 0) // where id = ? and stock > 0
                    //使用乐观锁的方式 对stock进行判断
                    .update();

            if(!success){
                return  Result.fail("库存不足");
            }

            // create order
            VoucherOrder voucherOrder = new VoucherOrder();

            // get order id
            long orderId = redisIdWorker.nextId("order");
            voucherOrder.setId(orderId);
            // people id
            voucherOrder.setUserId(voucherId);
            // voucher id
            voucherOrder.setVoucherId(voucherId);
            save(voucherOrder);


            return Result.ok(orderId);


    }
}
