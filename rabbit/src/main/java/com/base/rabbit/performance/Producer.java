/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.base.rabbit.performance;

import com.base.rabbit.message.QueueMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import javax.annotation.PostConstruct;
import java.util.LinkedList;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicLong;

//@Component
public class Producer {

    private static final Logger logger = LoggerFactory.getLogger(Consumer.class);

    @Value("${rabbit.exchange.bench}")
    private String benchExchange;
    @Value("${rabbit.queue.bench}")
    private String benchQueue;
    @Value("${rabbit.routingKey.bench}")
    private String benchRoutingKey;


    @Autowired
    private RabbitTemplate rabbitTemplate;

    @PostConstruct
    public void init() {

        final ExecutorService sendThreadPool = Executors.newFixedThreadPool(64);

        final StatsBenchmarkProducer statsBenchmark = new StatsBenchmarkProducer();

        final Timer timer = new Timer("BenchmarkTimerThread", true);

        final LinkedList<Long[]> snapshotList = new LinkedList<Long[]>();

        final int[] delays = new int[]{5, 10, 30, 60, 300, 600, 1800, 3600};

        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                snapshotList.addLast(statsBenchmark.createSnapshot());
                if (snapshotList.size() > 10) {
                    snapshotList.removeFirst();
                }
            }
        }, 1000, 1000);

        timer.scheduleAtFixedRate(new TimerTask() {
            private void printStats() {
                if (snapshotList.size() >= 10) {
                    Long[] begin = snapshotList.getFirst();
                    Long[] end = snapshotList.getLast();

                    final long sendTps = (long) (((end[3] - begin[3]) / (double) (end[0] - begin[0])) * 1000L);
                    final double averageRT = (end[5] - begin[5]) / (double) (end[3] - begin[3]);
                    final long sendQps = (long) (((end[6] - begin[6]) / (double) (end[0] - begin[0])) * 1000L);

                    System.out.printf("Send QPS: %d TPS: %d Max RT: %d Average RT: %7.3f Send Failed: %d Response Failed: %d%n",
                            sendQps, sendTps, statsBenchmark.getSendMessageMaxRT().get(), averageRT, end[2], end[4]);
                }
            }

            @Override
            public void run() {
                try {
                    this.printStats();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, 10000, 10000);


        for (int i = 0; i < 64; i++) {
            sendThreadPool.execute(new Runnable() {
                @Override
                public void run() {
                    while (true) {
                        statsBenchmark.getSendRequestCount().incrementAndGet();
                        try {
                            final Message msg;
                            try {
//                                SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
//                                sf.format(new Date())
                                int delay = delays[new Random().nextInt(delays.length)];
                                StringBuilder sb = new StringBuilder();
                                sb.append(delay);
                                sb.append(":");
                                for (int i = 0; i < 10; i += 10) {
                                    sb.append("hello baby");
                                }
                                QueueMessage queueMessage = new QueueMessage(sb.toString());
                                queueMessage.setDelay(delay);
//                                logger.info("produce msg={}", queueMessage);
                                msg = queueMessage.toAmqpMessageForDelay(delay);
                            } catch (Exception e) {
                                e.printStackTrace();
                                return;
                            }
                            final long beginTimestamp = System.currentTimeMillis();
                            rabbitTemplate.send(benchExchange, benchRoutingKey, msg);
                            statsBenchmark.getSendRequestSuccessCount().incrementAndGet();
                            statsBenchmark.getReceiveResponseSuccessCount().incrementAndGet();
                            final long currentRT = System.currentTimeMillis() - beginTimestamp;
                            statsBenchmark.getSendMessageSuccessTimeTotal().addAndGet(currentRT);
                            long prevMaxRT = statsBenchmark.getSendMessageMaxRT().get();
                            while (currentRT > prevMaxRT) {
                                boolean updated = statsBenchmark.getSendMessageMaxRT().compareAndSet(prevMaxRT, currentRT);
                                if (updated)
                                    break;

                                prevMaxRT = statsBenchmark.getSendMessageMaxRT().get();
                            }
                            Thread.sleep(15);
                        } catch (Exception e) {
                            e.printStackTrace();
                            statsBenchmark.getSendRequestFailedCount().incrementAndGet();
                            try {
                                Thread.sleep(3000);
                            } catch (InterruptedException e1) {
                            }
                        }
                    }
                }
            });
        }
    }
}

class StatsBenchmarkProducer {
    private final AtomicLong sendRequestCount = new AtomicLong(0L);

    private final AtomicLong sendRequestSuccessCount = new AtomicLong(0L);

    private final AtomicLong sendRequestFailedCount = new AtomicLong(0L);

    private final AtomicLong receiveResponseSuccessCount = new AtomicLong(0L);

    private final AtomicLong receiveResponseFailedCount = new AtomicLong(0L);

    private final AtomicLong sendMessageSuccessTimeTotal = new AtomicLong(0L);

    private final AtomicLong sendMessageMaxRT = new AtomicLong(0L);

    public Long[] createSnapshot() {
        Long[] snap = new Long[]{
                System.currentTimeMillis(),
                this.sendRequestSuccessCount.get(),
                this.sendRequestFailedCount.get(),
                this.receiveResponseSuccessCount.get(),
                this.receiveResponseFailedCount.get(),
                this.sendMessageSuccessTimeTotal.get(),
                this.sendRequestCount.get()
        };

        return snap;
    }

    public AtomicLong getSendRequestCount() {
        return sendRequestCount;
    }

    public AtomicLong getSendRequestSuccessCount() {
        return sendRequestSuccessCount;
    }

    public AtomicLong getSendRequestFailedCount() {
        return sendRequestFailedCount;
    }

    public AtomicLong getReceiveResponseSuccessCount() {
        return receiveResponseSuccessCount;
    }

    public AtomicLong getReceiveResponseFailedCount() {
        return receiveResponseFailedCount;
    }

    public AtomicLong getSendMessageSuccessTimeTotal() {
        return sendMessageSuccessTimeTotal;
    }

    public AtomicLong getSendMessageMaxRT() {
        return sendMessageMaxRT;
    }
}
