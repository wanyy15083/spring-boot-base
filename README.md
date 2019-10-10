# spring-boot-base

### 简介
    基于Spring Boot整合常用框架和工具，如dubbo、redis。rabbitmq等,。
    
### 技术选型
* 核心框架：Sring boot + Spring Framework + Dubbo
* 持久层框架：Mybatis
* 数据库连接池：HikariCP
* 缓存框架：Redis + Jedis
* 消息框架：RabbitMQ
* 日志框架：slf4j + logback
* 构建工具：gradle
* 其他工具：guava、gson等

### 模块介绍
* api：公共接口和实体类
* provider：Dubbo Spring Boot 提供者实例
* consumer：Dubbo Spring Boot 消费者实例
* web：整合所有框架的web工程