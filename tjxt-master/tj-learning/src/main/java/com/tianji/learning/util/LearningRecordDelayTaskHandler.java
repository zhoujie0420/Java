package com.tianji.learning.util;

import com.tianji.common.utils.JsonUtils;
import com.tianji.common.utils.StringUtils;
import com.tianji.learning.domain.po.LearningLesson;
import com.tianji.learning.domain.po.LearningRecord;
import com.tianji.learning.mapper.LearningLessonMapper;
import com.tianji.learning.mapper.LearningRecordMapper;
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
 * @Description:
 * @Date: 2023/4/18 15:08
 */
@Slf4j
@Component
public class LearningRecordDelayTaskHandler {

    @Resource
    private RedisTemplate redisTemplate;

    @Resource
    private LearningRecordMapper learningRecordMapper;

    @Resource
    private LearningLessonMapper learningLessonMapper;

    private final static String RECORD_KEY_TEMPLATE = "learning:record:{}";

    private final static DelayQueue<DelayTask<RecordTaskData>> queue = new DelayQueue<>();

    private static volatile boolean BEGIN = true;

    @PostConstruct
    public void init() {
        CompletableFuture.runAsync(this::handleDelayTask);
    }

    @PreDestroy
    public void destory() {
        BEGIN = false;
        log.debug("异步任务停止执行");
    }

    public void handleDelayTask() {
        while (BEGIN) {
            try {
                // 1-获取到期的延迟任务
                DelayTask<RecordTaskData> task = queue.take();
                RecordTaskData data = task.getData();
                log.debug("延迟队列开始处理数据了, 课程id:{}, 小节id:{}", data.getLessonId(), data.getSectionId());

                // 2-查询redis缓存
                LearningRecord learningRecord = readRecordCache(data.getLessonId(), data.getSectionId());
                if (null == learningRecord) {
                    continue;
                }

                // 3-比较redis和队列中的moment
                if (!Objects.equals(data.getMoment(), learningRecord.getMoment())) {
                    // 3.1-不一致，说明用户还在持续提交播放进度，放弃旧数据
                    continue;
                }
                // 3.2-一致，持久化播放进度到数据库

                // 3.2.1-更新学习记录(只需更新moment，因此finished置空，避免误更新)
                learningRecord.setFinished(null);
                learningRecordMapper.updateById(learningRecord);

                // 3.2.2-更新课表最近学习小节、时间
                LearningLesson lesson = new LearningLesson();
                lesson.setId(data.getLessonId());
                lesson.setLatestSectionId(data.getSectionId());
                lesson.setLatestLearnTime(LocalDateTime.now());
                learningLessonMapper.updateById(lesson);
            } catch (InterruptedException e) {
                log.error("延迟队列处理异常", e);
            }
        }
    }

    public void addLearningRecordTask(LearningRecord record) {

        // 1-添加数据到Redis缓存
        writeRecordCache(record);

        // 2-提交延迟任务到延迟队列DelayQueue(前端15s一次，这边20s够够的)
        queue.add(new DelayTask<>(new RecordTaskData(record), Duration.ofSeconds(20)));
    }

    public void writeRecordCache(LearningRecord record) {
        log.debug("存储学习记录的缓存数据");
        // 1-数据转换
        String json = JsonUtils.toJsonStr(new RecordCacheData(record));
        // 2-写入Redis
        String key = StringUtils.format(RECORD_KEY_TEMPLATE, record.getLessonId());
        log.debug("key=" + key);
        redisTemplate.opsForHash().put(key, record.getSectionId().toString(), json);
        // 3-添加缓存过期时间
        redisTemplate.expire(key, Duration.ofMillis(3));
    }

    public LearningRecord readRecordCache(Long lessonId, Long sectionId) {
        try {
            String key = StringUtils.format(RECORD_KEY_TEMPLATE, lessonId);
            Object cacheData = redisTemplate.opsForHash().get(key, sectionId.toString());
            if (null == cacheData) {
                return null;
            }

            return JsonUtils.toBean(cacheData.toString(), LearningRecord.class);
        } catch (Exception e) {
            log.error("缓存读取异常", e);
            return null;
        }
    }

    public void cleanRecordCache(Long lessonId, Long sectionId) {
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
            this.lessonId = record.getId();
            this.sectionId = record.getSectionId();
            this.moment = record.getMoment();
        }
    }
}
