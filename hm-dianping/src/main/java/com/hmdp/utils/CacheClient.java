package com.hmdp.utils;

import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.StrUtil;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.time.LocalDateTime;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

import static com.hmdp.utils.RedisConstants.CACHE_NULL_TTL;
import static com.hmdp.utils.RedisConstants.LOCK_SHOP_KEY;

public class CacheClient {
    private final StringRedisTemplate stringRedisTemplate;

    public CacheClient(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }
    private static final ExecutorService CACHE_REBUILD_EXECUTOR = Executors.newFixedThreadPool(10);
    //创建一个线程池

    public void set(String key, Object value, long time , TimeUnit unit){
        stringRedisTemplate.opsForValue().set(key, JSONUtil.toJsonStr(value),time,unit);
    }

    //设置逻辑过期时间
    public void setWithLogicalExpire(String key,Object value,long time,TimeUnit unit){
        RedisData redisData = new RedisData();
        redisData.setData(value);
        //当前时间加上设置的时间的秒数令为逻辑过期时间
        redisData.setExpireTime(LocalDateTime.now().plusSeconds(unit.toSeconds(time)));

        //写入redis
        stringRedisTemplate.opsForValue().set(key,JSONUtil.toJsonStr(redisData));
    }

    //解决缓存穿透  //实现过程和其中的lambda 不是很熟悉
    public <R , ID> R queryWithPassThrough(
            String keyPrefix, ID id, Class<R> type , Function<ID,R> dbFallBack,long time,TimeUnit unit){
        String key = keyPrefix + id;
        //1.get shop cache from redis
        String json = stringRedisTemplate.opsForValue().get(key);
        //2.judge whether it exists
        if(StrUtil.isNotBlank(json)){
            // exists , return
            return JSONUtil.toBean(json,type);
        }
        //3.judge whether its null ?
        if(json != null){
            // return fail information
            return null;
        }
        //no exists , query by id from database
        R r = dbFallBack.apply(id);
        //no exists , return fail
        if(r == null){
            // set null into redis
            stringRedisTemplate.opsForValue().set(key,"",CACHE_NULL_TTL,TimeUnit.MINUTES);
            return null;
        }

        // exists , set into redis
        this.set(key,r,time,unit);
        return r;
    }

    //逻辑过期解决缓存击穿
    public <R ,ID> R queryWithLogicalExpire(
            String keyPrefix, ID id, Class<R> type , Function<ID,R> dbFallBack,long time,TimeUnit unit){
        String key = keyPrefix+id;
        // 1.query shop cache from redis
        String json = stringRedisTemplate.opsForValue().get(key);
        // 2.judge whether it exists
        if(StrUtil.isBlank(key)){
            // no exists ,return
            return null;
        }
        // 3.exists,  json -> object
        RedisData redisData = JSONUtil.toBean(json, RedisData.class);
        R r = JSONUtil.toBean((JSONObject) redisData.getData(), type);
        LocalDateTime expireTime = redisData.getExpireTime();
        // 4.judge is  expiration?
        if(expireTime.isAfter(LocalDateTime.now())){
            // no
            return r;
        }
        // yes
        // 5.set cache
        String lockKey = LOCK_SHOP_KEY + id;
        boolean isLock = tryLock(lockKey);
        // get lock ?
        if(isLock){
            //yes , start new thread to set cache
            CACHE_REBUILD_EXECUTOR.submit(()->{
                try {
                    // query in database
                    R newR = dbFallBack.apply(id);
                    // set cache
                    this.setWithLogicalExpire(key,newR,time,unit);
                }catch (Exception e){
                    throw new RuntimeException(e);
                }finally {
                    //release lock
                    unlock(key);
                }
            });
        }
        // 6. return expiration information
        return r;
    }

    //互斥锁解决缓存击穿
    public <R, ID> R queryWithMutex(
           String keyPrefix, ID id, Class<R> type, Function<ID, R> dbFallback, Long time, TimeUnit unit) {
        String key = keyPrefix + id;
        // 1.query shop cache from redis
        String shopJson = stringRedisTemplate.opsForValue().get(key);
        // 2.judge whether is exists?
        if (StrUtil.isNotBlank(shopJson)) {
            // 3.yes,return
            return JSONUtil.toBean(shopJson, type);
        }
        // judge whether is null?
        if (shopJson != null) {
            // return fail information
            return null;
        }

        // 4.set cache
        // get lock
        String lockKey = LOCK_SHOP_KEY+id;
        R r = null;
        try {
            boolean isLock = tryLock(key);
            // whether isLock?
            if(!isLock){
                // no , sleep , repeat call
                Thread.sleep(50);
                return queryWithMutex(keyPrefix,id,type,dbFallback,time,unit);
            }
            // yes ,query from database
            r = dbFallback.apply(id);
            // no ,return fail
            if(r == null){
                //set null to redis
                stringRedisTemplate.opsForValue().set(key,"", CACHE_NULL_TTL,TimeUnit.MINUTES);
                return null ;
            }
            // yes , set into redis
            this.set(key,r,time,unit);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }finally {
            //release lock
            unlock(lockKey);
        }

        return r;
    }

    private boolean tryLock(String key){  //setIfAbsent setnx  如果缺席就可以set 这里充当互斥锁使用
        Boolean flag = stringRedisTemplate.opsForValue().setIfAbsent(key, "1", 10, TimeUnit.SECONDS);

        //由于自动拆箱的存在，直接return flag 可能出现  null pointer exception
        return BooleanUtil.isTrue(flag);
    }

    private void unlock(String key){
        stringRedisTemplate.delete(key);
    }

}
