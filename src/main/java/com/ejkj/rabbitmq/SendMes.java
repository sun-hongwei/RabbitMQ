package com.ejkj.rabbitmq;

import com.rabbitmq.client.Channel;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.amqp.rabbit.annotation.*;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.UUID;

@RestController
@Api(description = "RabbitMQ Demo")
public class SendMes {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    /**
     * 向RabbitMQ 发送消息
     *
     * @return
     */
    @ResponseBody
    @ApiOperation("发送MQ消息")
    @RequestMapping(method = RequestMethod.POST, value = "/sendMes")
    public Business sendMes() {

        //定义业务返回对象
        Business menuInfoBusiness = new Business<>();

        //发送消息实体
        RabbitMQ rabbitMQ = new RabbitMQ();
        rabbitMQ.setId(1);
        rabbitMQ.setName("helloWorld");
        //根据业务规则匹配 不能重复
        rabbitMQ.setMessageId(System.currentTimeMillis() + "#" + UUID.randomUUID().toString());

        //定义消息发送ID
        CorrelationData correlationData = new CorrelationData();
        correlationData.setId(rabbitMQ.getMessageId());

        try {
            //发送消息
            rabbitTemplate.convertAndSend(RabbitMQConstants.TASK_EXCHANGE,
                    RabbitMQConstants.TASK_ROUTING_KEY,
                    rabbitMQ,
                    correlationData);
            menuInfoBusiness.setMessage("消息发送成功");
        } catch (Exception e) {
            menuInfoBusiness.setCode(500);
            menuInfoBusiness.setMessage("消息发送失败" + e.getMessage());
        }
        return menuInfoBusiness;
    }

    /**
     * rabbitMQ 接收消息
     *
     * @param rabbitMQ
     * @param headers
     * @param channel
     * @throws Exception
     */
    @RabbitListener(
            bindings = @QueueBinding(
                    value = @Queue(value = "test-queue", durable = "true"),
                    exchange = @Exchange(name = "test-exchange", durable = "true", type = "direct"),
                    key = "testKey.456" //task.#
            )
    )
    @RabbitHandler
    private void dealReceiveMes(@Payload RabbitMQ rabbitMQ,
                                @Headers Map<String, Object> headers,
                                Channel channel) throws Exception {

        RabbitMQ cRabbitMQ = new RabbitMQ();

        System.out.println("消息接受_Id" + rabbitMQ.getId());
        System.out.println("消息接受_name" + rabbitMQ.getName());
        System.out.println("消息接受_UUID" + rabbitMQ.getMessageId());

        cRabbitMQ.setId(rabbitMQ.getId());
        cRabbitMQ.setMessageId(rabbitMQ.getMessageId());
        cRabbitMQ.setName(rabbitMQ.getName());

        //将消息输出
        System.out.println(cRabbitMQ.toString());
        Long deliveryTag = (Long) headers.get(AmqpHeaders.DELIVERY_TAG);
        //手动ACK
        channel.basicAck(deliveryTag, false);
    }
}
