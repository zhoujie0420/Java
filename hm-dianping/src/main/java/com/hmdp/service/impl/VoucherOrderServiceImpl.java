package com.hmdp.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hmdp.dto.Result;
import com.hmdp.entity.SeckillVoucher;
import com.hmdp.entity.VoucherOrder;
import com.hmdp.mapper.VoucherOrderMapper;
import com.hmdp.service.ISeckillVoucherService;
import com.hmdp.service.IVoucherOrderService;
import com.hmdp.utils.RedisIdWorker;
import com.hmdp.utils.SimpleRedisLock;
import com.hmdp.utils.UserHolder;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.aop.framework.AopContext;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.concurrent.*;


/**
 *
 */
@Service
public class VoucherOrderServiceImpl extends ServiceImpl<VoucherOrderMapper, VoucherOrder> implements IVoucherOrderService {


    @Resource
    private ISeckillVoucherService seckillVoucherService;
    @Resource
    private RedisIdWorker redisIdWorker;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Resource
    private RedissonClient redissonClient;
    private static final DefaultRedisScript<Long> SECKILL_SCRIPT;

    private BlockingQueue<VoucherOrder> orderTasks = new ArrayBlockingQueue<>(1024 * 1024);

    private ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(
            10,
            10,
            60,
            TimeUnit.SECONDS,
            new ArrayBlockingQueue<>(1024 * 1024),
            Executors.defaultThreadFactory(),
            new ThreadPoolExecutor.AbortPolicy()
    );

    static {
        SECKILL_SCRIPT = new DefaultRedisScript<>();
        SECKILL_SCRIPT.setLocation(new ClassPathResource("seckill.lua"));
        SECKILL_SCRIPT.setResultType(Long.class);
    }

    @PostConstruct
    private void init() {
        threadPoolExecutor.submit(() -> {
            while (true) {
                try {
                    VoucherOrder voucherOrder = orderTasks.take();
                    createVoucherOrder(voucherOrder);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }


    @Override
    public Result seckillVoucher(Long voucherId) {
        Long userId = UserHolder.getUser().getId();
        // 1.执行lua脚本
        Long result = stringRedisTemplate.execute(
                SECKILL_SCRIPT,
                Collections.emptyList(),
                voucherId.toString(), userId.toString()
        );
        int r = result.intValue();
        // 2.判断结果是否为0
        if (r != 0) {
            // 2.1.不为0 ，代表没有购买资格
            return Result.fail(r == 1 ? "库存不足" : "不能重复下单");
        }
        // 2.2.为0 ，有购买资格，把下单信息保存到阻塞队列
        long orderId = redisIdWorker.nextId("order");
        // 保存阻塞队列
        VoucherOrder voucherOrder = new VoucherOrder();
        long id = redisIdWorker.nextId("order");
        voucherOrder.setId(id);
        voucherOrder.setUserId(userId);
        voucherOrder.setVoucherId(voucherId);
        orderTasks.add(voucherOrder);
        // 3.返回订单id
        return Result.ok(orderId);
    }


    public void createVoucherOrder(VoucherOrder voucherOrder) {
        Long userId = UserHolder.getUser().getId();
        Long voucherId = voucherOrder.getVoucherId();
        RLock redisLock = redissonClient.getLock("lock:order:" + userId);
        boolean isLock = redisLock.tryLock();
        if (!isLock) {
            log.error("不允许重复下单！");
            return;
        }
        try {
            // 5.1.查询订单
            int count = query().eq("user_id", userId).eq("voucher_id", voucherId).count();
            // 5.2.判断是否存在
            if (count > 0) {
                // 用户已经购买过了
                log.error("不允许重复下单！");
                return;
            }

            // 6.扣减库存
            boolean success = seckillVoucherService.update()
                    .setSql("stock = stock - 1")
                    .eq("voucher_id", voucherId).gt("stock", 0)
                    .update();
            if (!success) {
                // 扣减失败
                log.error("库存不足！");
                return;
            }

            save(voucherOrder);
        } finally {
            // 释放锁
            redisLock.unlock();
        }
    }
}

/*
    public Result seckillVoucher(Long voucherId) {
        // query coupon
        SeckillVoucher voucher = seckillVoucherService.getById(voucherId);
        // judge whether start?
        if (voucher.getBeginTime().isAfter(LocalDateTime.now())) {
            // no
            return Result.fail("秒杀尚未开始");
        }
        // judge whether end;
        if (voucher.getEndTime().isBefore(LocalDateTime.now())) {
            // yes
            return Result.fail("秒杀已经结束");
        }
        // judge whether stock ?
        if (voucher.getStock() < 1) {
            // yes
            return Result.fail("库存不足");
        }
        // success -> create order
        Long userId = UserHolder.getUser().getId();


        // solve1  手动创建锁
        // new lock
        SimpleRedisLock lock = new SimpleRedisLock("order" + userId, stringRedisTemplate);
        // try get lock
        boolean isLock = lock.tryLock(1200);

        //judge isLock
        if (!isLock) {
            // fail
            return Result.fail("一人只能下一单");
        }
        try {
            IVoucherOrderService proxy = (IVoucherOrderService) AopContext.currentProxy();
            return proxy.createVoucherOrder(voucherId);
        } finally {
            lock.unlock();
        }


//  solve2   synchronized 锁
//        synchronized (userId.toString().intern()) {
//            //获取代理对象（事务）
//            IVoucherOrderService proxy = (IVoucherOrderService) AopContext.currentProxy();
//            return proxy.createVoucherOrder(voucherId);
//        }
    }

 */
