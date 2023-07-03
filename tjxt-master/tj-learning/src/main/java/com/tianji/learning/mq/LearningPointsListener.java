package com.tianji.learning.mq;

import com.tianji.common.constants.MqConstants;
import com.tianji.learning.enums.PointsRecordType;
import com.tianji.learning.mq.message.SignInMessage;
import com.tianji.learning.service.IPointsRecordService;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @Description:
 * @Date: 2023/4/21 19:05
 */
@Component
public class LearningPointsListener {

    @Resource
    private IPointsRecordService recordService;

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = "qa.points.queue", durable = "true"),
            exchange = @Exchange(name = MqConstants.Exchange.LEARNING_EXCHANGE, type = ExchangeTypes.TOPIC),
            key = MqConstants.Key.WRITE_REPLY
    ))
    public void listenerWriteReplyMessage(Long userId) {
        recordService.addPointsRecord(userId, 5, PointsRecordType.QA);
    }

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = "sign.points.queue", durable = "true"),
            exchange = @Exchange(name = MqConstants.Exchange.LEARNING_EXCHANGE, type = ExchangeTypes.TOPIC),
            key = MqConstants.Key.SIGN_IN
    ))
    public void listenerSignMessage(SignInMessage message) {
        recordService.addPointsRecord(message.getUserId(), message.getPoints(), PointsRecordType.SIGN);
    }

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = "learning.points.queue", durable = "true"),
            exchange = @Exchange(name = MqConstants.Exchange.LEARNING_EXCHANGE, type = ExchangeTypes.TOPIC),
            key = MqConstants.Key.LEARN_SECTION
    ))
    public void listenLearnSectionMessage(Long userId){
        recordService.addPointsRecord(userId, 10, PointsRecordType.LEARNING);
    }

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = "note.new.points.queue", durable = "true"),
            exchange = @Exchange(name = MqConstants.Exchange.LEARNING_EXCHANGE, type = ExchangeTypes.TOPIC),
            key = MqConstants.Key.WRITE_NOTE
    ))
    public void listenWriteNodeMessage(Long userId){
        recordService.addPointsRecord(userId, 3, PointsRecordType.NOTE);
    }

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = "note.gathered.points.queue", durable = "true"),
            exchange = @Exchange(name = MqConstants.Exchange.LEARNING_EXCHANGE, type = ExchangeTypes.TOPIC),
            key = MqConstants.Key.NOTE_GATHERED
    ))
    public void listenNodeGatheredMessage(Long userId){
        recordService.addPointsRecord(userId, 2, PointsRecordType.NOTE);
    }
}
