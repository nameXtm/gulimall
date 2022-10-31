package com.atguigu.gulimall.order;

import com.atguigu.gulimall.order.entity.OrderReturnReasonEntity;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class GulimallOrderApplicationTests {

    @Autowired
    AmqpAdmin amqpAdmin;

    @Autowired
    RabbitTemplate rabbitTemplate;



    @Test
    public void sendTest() {
        //发送消息
        OrderReturnReasonEntity orderReturnReasonEntity = new OrderReturnReasonEntity();
        orderReturnReasonEntity.setId(22l);
        orderReturnReasonEntity.setName("hh");
        //1、发送消息,如果发送的消息是个对象，会使用序列化机制，将对象写出去，对象必须实现Serializable接口

        //2、发送的对象类型的消息，可以是一个jsonMyRabbitMQConfig
        //MyRabbitConfig
        rabbitTemplate.convertAndSend("hello-java-exchange","hello.java",orderReturnReasonEntity);
        log.info("消息已发送");
    }

    @Test
    public void contextLoads() {
        //创建交换机
        /**
         *
         */
        DirectExchange directExchange = new DirectExchange("hello-java-exchange", true, false);
        amqpAdmin.declareExchange(directExchange);//参数放交换机名称；
        log.info("exchanges擦创建成功");

    }

    @Test
    public void queue() {

        Queue queue = new Queue("hello-java-queue", true, false, false);
        amqpAdmin.declareQueue(queue);
        log.info("queue创建成功");
    }


    @Test
    public void bind() {
        Binding binding = new Binding("hello-java-queue",
                Binding.DestinationType.QUEUE,
                "hello-java-exchange",
                "hello.java",
                null);
        amqpAdmin.declareBinding(binding);
        log.info("binding创建成功");

    }
}