package com.ejkj.rabbitmq;

import java.io.Serializable;

/**
 * @author bridge
 * 发送 RabbitMQ 消息体
 */
public class RabbitMQ implements Serializable {

    /**主键*/
    private Integer id;
    /**名字*/
    private String name;
    /**消息ID*/
    private String messageId;


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    @Override
    public String toString() {
        return "RabbitMQ{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", messageId='" + messageId + '\'' +
                '}';
    }
}
