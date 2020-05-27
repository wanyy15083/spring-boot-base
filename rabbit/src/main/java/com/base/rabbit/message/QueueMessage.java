package com.base.rabbit.message;

import com.google.gson.Gson;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

/**
 *
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
        this.timestamp = System.currentTimeMillis();
    }

    public static QueueMessage fromAmqpMessage(Message message) {
        Gson gson = new Gson();
        QueueMessage msg = null;
        try {
            msg = gson.fromJson(new String(message.getBody(), "utf-8"), QueueMessage.class);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException();
        }
        return msg;
    }

    public Message toAmqpMessage() {
        Gson gson = new Gson();
        Message message = null;
        try {
            message = MessageBuilder.withBody(gson.toJson(this).getBytes(StandardCharsets.UTF_8)).build();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException();
        }
        return message;
    }

    public Message toAmqpMessageForTTL(String expiration) {
        Gson gson = new Gson();
        Message message;
        try {
            message = MessageBuilder.withBody(gson.toJson(this).getBytes(StandardCharsets.UTF_8)).build();
            message.getMessageProperties().setExpiration(expiration);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException();
        }
        return message;
    }

    public Message toAmqpMessageForDelay(Integer delaySec) {
        Gson gson = new Gson();
        Message message;
        try {
            message = MessageBuilder.withBody(gson.toJson(this).getBytes(StandardCharsets.UTF_8)).build();
            message.getMessageProperties().setHeader("x-delay", delaySec * 1000);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException();
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

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public int getDelay() {
        return delay;
    }

    public void setDelay(int delay) {
        this.delay = delay;
    }

    private String taskId;
    private String payload;
    private int type;
    private long timestamp;
    private int delay;

    @Override
    public String toString() {
        return "QueueMessage{" +
                "taskId='" + taskId + '\'' +
                ", payload='" + payload + '\'' +
                ", type=" + type +
                ", timestamp=" + timestamp +
                ", delay=" + delay +
                '}';
    }
}
