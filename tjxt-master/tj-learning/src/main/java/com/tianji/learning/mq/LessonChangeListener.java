package com.tianji.learning.mq;

import com.tianji.api.dto.trade.OrderBasicDTO;
import com.tianji.common.constants.MqConstants;
import com.tianji.common.utils.CollUtils;
import com.tianji.learning.service.ILearningLessonService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Objects;

/**
 * @Description:
 * @Date: 2023/4/16 13:53
 */
@Slf4j
@Component
public class LessonChangeListener {

    @Resource
    private ILearningLessonService lessonService;
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "learning.lesson.pay.queue",durable = "true"),
            exchange = @Exchange(name = MqConstants.Exchange.ORDER_EXCHANGE,type = ExchangeTypes.TOPIC),
            key = MqConstants.Key.ORDER_PAY_KEY
    ))
    public void listenLessonPay(OrderBasicDTO order){
        if(order == null || order.getUserId() == null || CollUtils.isEmpty(order.getCourseIds())){
            log.error("接收到的mq消息有误，订单数据为空");
            return;
        }
        log.debug("监听到用户{}的订单，需要添加{}到课表中",order.getUserId(),order.getOrderId());
        lessonService.addUserLessons(order);

    }

//
//
//    @RabbitListener(bindings = @QueueBinding(
//            value = @Queue(name = "learning.lesson.refund.queue", durable = "true"),
//            exchange = @Exchange(name = MqConstants.Exchange.ORDER_EXCHANGE, type = ExchangeTypes.TOPIC),
//            key = MqConstants.Key.ORDER_PAY_KEY
//    ))
//    public void listenLessonRefund(OrderBasicDTO order) {
//        // 1-非空校验
//        if (Objects.isNull(order) || Objects.isNull(order.getOrderId()) || CollUtils.isEmpty(order.getCourseIds())) {
//            log.error("接收到MQ消息，订单数据为空");
//            return;
//        }
//
//        // 2-添加课程
//        lessonService.removeUserLessons(order);
//    }

}
