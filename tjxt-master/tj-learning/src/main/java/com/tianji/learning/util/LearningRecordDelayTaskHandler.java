package com.tianji.learning.util;

import com.tianji.common.utils.JsonUtils;
import com.tianji.common.utils.StringUtils;
import com.tianji.learning.domain.po.LearningLesson;
import com.tianji.learning.domain.po.LearningRecord;
import com.tianji.learning.mapper.LearningRecordMapper;
import com.tianji.learning.service.ILearningLessonService;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.DelayQueue;

/**
 * @author dell
 * @Description:
 * @Date: 2023/4/18 15:08
 */
@Slf4j
@Component
public class LearningRecordDelayTaskHandler {

    @Resource
    private RedisTemplate redisTemplate;
    @Resource
    private LearningRecordMapper recordMapper;
    @Resource
    private ILearningLessonService lessonService;

    @Resource
    private DelayQueue<DelayTask<RecordTaskData>> queue = new DelayQueue<>();

    private final static String RECORD_KEY_TEMPLATE = "learning:record:{}";
    private static volatile boolean begin = true;

    /**
     * 注入完成后，启动延迟任务
     */
    @PostConstruct
    public void init() {
        CompletableFuture.runAsync(this::handleDelayTask);
    }

    /**
     * 销毁bean时，停止延迟任务
     */
    @PreDestroy
    public void destroy() {
        begin = false;
        log.debug("延迟任务停止");
    }

    public void handleDelayTask() {
        while (begin) {
            try {
                DelayTask<RecordTaskData> task = queue.take();
                RecordTaskData data = task.getData();
                // 1-获取redis中的记录
                LearningRecord record = readRecordCache(data.getLessonId(), data.getSectionId());
                if(record == null){
                    continue;
                }
                if(!Objects.equals(record.getMoment(), data.getMoment())){
                    continue;
                }
                record.setFinished(null);
                recordMapper.updateById(record);
                LearningLesson lesson = new LearningLesson();
                lesson.setId(record.getLessonId());
                lesson.setLatestLearnTime(LocalDateTime.now());
                lesson.setLatestSectionId(data.getSectionId());
                lessonService.updateById(lesson);
            }catch (Exception e){
                log.error("处理延迟任务失败",e);
            }
        }
    }


    public void addLearningRecordTask(LearningRecord record) {
        //添加数据到redis
        writeRecordCache(record);
        queue.add(new DelayTask<>(new RecordTaskData(record), Duration.ofSeconds(20)));
    }


    public void writeRecordCache(LearningRecord record) {
        log.debug("更新学习记录的缓存数据");
        try {
            String json = JsonUtils.toJsonStr(new RecordCacheData(record));
            String key = StringUtils.format(RECORD_KEY_TEMPLATE, record.getLessonId());
            redisTemplate.opsForHash().put(key, record.getSectionId().toString(), json);
            redisTemplate.expire(key, Duration.ofMinutes(1));
        } catch (Exception e) {
            log.error("更新学习记录的缓存数据失败，lessonId:{},sectionId:{}", record.getLessonId(), record.getSectionId(), e);
        }
    }

    public LearningRecord readRecordCache(Long lessonId, Long selectionId) {
        try {
            String key = StringUtils.format(RECORD_KEY_TEMPLATE, lessonId);
            Object cacheData = redisTemplate.opsForHash().get(key, selectionId.toString());
            if (cacheData == null) {
                return null;
            }
            return JsonUtils.toBean(cacheData.toString(), LearningRecord.class);

        } catch (Exception e) {
            log.error("获取学习记录缓存数据失败，lessonId:{},sectionId:{}", lessonId, selectionId, e);
            return null;
        }
    }


    /**
     * 删除数据
     */
    public void cleanRecordCache(Long lessonId, Long sectionId) {
        //删除数据
        String key = StringUtils.format(RECORD_KEY_TEMPLATE, lessonId);
        redisTemplate.opsForHash().delete(key, sectionId.toString());

    }

    @Data
    @NoArgsConstructor
    private static class RecordCacheData {
        private Long id;
        private Integer moment;
        private Boolean finished;

        public RecordCacheData(LearningRecord record) {
            this.id = record.getId();
            this.moment = record.getMoment();
            this.finished = record.getFinished();
        }
    }

    @Data
    @NoArgsConstructor
    private static class RecordTaskData {
        private Long lessonId;
        private Long sectionId;
        private Integer moment;

        public RecordTaskData(LearningRecord record) {
            this.lessonId = record.getLessonId();
            this.sectionId = record.getSectionId();
            this.moment = record.getMoment();
        }
    }


}
