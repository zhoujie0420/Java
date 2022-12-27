package com.hmdp.utils;

import lombok.Data;

import java.time.LocalDateTime;
//逻辑过期时间，可以直接在data实体类继承，但是会修改以前的代码，这样的封装更好
@Data
public class RedisData {
    private LocalDateTime expireTime;
    private Object data;
}
