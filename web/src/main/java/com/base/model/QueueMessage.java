package com.base.model;

import com.base.exception.InvalidQueueMessageException;
import com.google.gson.Gson;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;

import java.util.UUID;

/**
 */
public class QueueMessage {
    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public String getPayload() {
        return payload;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }

    public QueueMessage() {
        taskId = UUID.randomUUID().toString();
    }

    public QueueMessage(String payload) {
        taskId = UUID.randomUUID().toString();
        this.payload = payload;
    }

    public static QueueMessage fromAmqpMessage(Message message) {
        Gson gson = new Gson();
        QueueMessage msg = null;
        try {
            msg = gson.fromJson(new String(message.getBody(), "utf-8"), QueueMessage.class);
        } catch (Exception e) {
            e.printStackTrace();
            throw new InvalidQueueMessageException();
        }
        return msg;
    }

    public Message toAmqpMessage() {
        Gson gson = new Gson();
        Message message = null;
        try {
            message = MessageBuilder.withBody(gson.toJson(this).getBytes("UTF-8")).build();
        } catch (Exception e) {
            e.printStackTrace();
            throw new InvalidQueueMessageException();
        }
        return message;
    }

    public String toJson() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    private String taskId;
    private String payload;
    private int type;
}
