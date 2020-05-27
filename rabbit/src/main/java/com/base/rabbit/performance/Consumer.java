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
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.LinkedList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicLong;

@Component
public class Consumer {
    private static final Logger logger = LoggerFactory.getLogger(Consumer.class);
    private final StatsBenchmarkConsumer statsBenchmarkConsumer = new StatsBenchmarkConsumer();


    @PostConstruct
    public void init() {

        final Timer timer = new Timer("BenchmarkTimerThread", true);

        final LinkedList<Long[]> snapshotList = new LinkedList<Long[]>();

        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                snapshotList.addLast(statsBenchmarkConsumer.createSnapshot());
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

                    final long consumeTps =
                            (long) (((end[1] - begin[1]) / (double) (end[0] - begin[0])) * 1000L);
                    final double averageB2CRT = (end[2] - begin[2]) / (double) (end[1] - begin[1]);
                    final double averageS2CRT = (end[3] - begin[3]) / (double) (end[1] - begin[1]);
                    final long failCount = end[4] - begin[4];
                    final double averageMissRT = (end[5] - begin[5]) / (double) (end[1] - begin[1]);
                    final long b2cMax = statsBenchmarkConsumer.getBorn2ConsumerMaxRT().get();
                    final long s2cMax = statsBenchmarkConsumer.getStore2ConsumerMaxRT().get();

                    statsBenchmarkConsumer.getBorn2ConsumerMaxRT().set(0);
                    statsBenchmarkConsumer.getStore2ConsumerMaxRT().set(0);
                    statsBenchmarkConsumer.getDelayMissCount().set(0);

                    System.out.printf("Receive TPS: %d AVG(MISSRT): %7.3f FAIL: %d AVG(B2C) RT: %7.3f AVG(S2C) RT: %7.3f MAX(B2C) RT: %d MAX(S2C) RT: %d%n",
                            consumeTps, averageMissRT, failCount, averageB2CRT, averageS2CRT, b2cMax, s2cMax
                    );
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
    }


    @RabbitListener(queues = "${rabbit.queue.bench}")
    public void testConsume(Message message) {
        try {
            QueueMessage msg = QueueMessage.fromAmqpMessage(message);
//            logger.info("receive msg={}", msg);
            Integer receivedDelay = message.getMessageProperties().getReceivedDelay();
            long now = System.currentTimeMillis();

            statsBenchmarkConsumer.getReceiveMessageTotalCount().incrementAndGet();

            long born2ConsumerRT = now - msg.getTimestamp() - receivedDelay;
            statsBenchmarkConsumer.getBorn2ConsumerTotalRT().addAndGet(born2ConsumerRT);

            long store2ConsumerRT = now - msg.getTimestamp() - receivedDelay;
            statsBenchmarkConsumer.getStore2ConsumerTotalRT().addAndGet(store2ConsumerRT);

            long delay = now - msg.getTimestamp();
            if (Math.abs(delay - receivedDelay) / (double) delay > 0.2)
                statsBenchmarkConsumer.getDelayMissCount().incrementAndGet();


            compareAndSetMax(statsBenchmarkConsumer.getBorn2ConsumerMaxRT(), born2ConsumerRT);

            compareAndSetMax(statsBenchmarkConsumer.getStore2ConsumerMaxRT(), store2ConsumerRT);

            if (ThreadLocalRandom.current().nextDouble() < 0.0) {
                statsBenchmarkConsumer.getFailCount().incrementAndGet();
            } else {
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("failed to process message {}, but it's consumed yet.", message);
        }
    }


    public static void compareAndSetMax(final AtomicLong target, final long value) {
        long prev = target.get();
        while (value > prev) {
            boolean updated = target.compareAndSet(prev, value);
            if (updated)
                break;

            prev = target.get();
        }
    }
}

class StatsBenchmarkConsumer {
    private final AtomicLong receiveMessageTotalCount = new AtomicLong(0L);

    private final AtomicLong born2ConsumerTotalRT = new AtomicLong(0L);

    private final AtomicLong store2ConsumerTotalRT = new AtomicLong(0L);

    private final AtomicLong born2ConsumerMaxRT = new AtomicLong(0L);

    private final AtomicLong store2ConsumerMaxRT = new AtomicLong(0L);

    private final AtomicLong failCount = new AtomicLong(0L);

    private final AtomicLong delayMissCount = new AtomicLong(0L);

    public Long[] createSnapshot() {
        Long[] snap = new Long[]{
                System.currentTimeMillis(),
                this.receiveMessageTotalCount.get(),
                this.born2ConsumerTotalRT.get(),
                this.store2ConsumerTotalRT.get(),
                this.failCount.get(),
                this.delayMissCount.get()
        };

        return snap;
    }

    public AtomicLong getReceiveMessageTotalCount() {
        return receiveMessageTotalCount;
    }

    public AtomicLong getBorn2ConsumerTotalRT() {
        return born2ConsumerTotalRT;
    }

    public AtomicLong getStore2ConsumerTotalRT() {
        return store2ConsumerTotalRT;
    }

    public AtomicLong getBorn2ConsumerMaxRT() {
        return born2ConsumerMaxRT;
    }

    public AtomicLong getStore2ConsumerMaxRT() {
        return store2ConsumerMaxRT;
    }

    public AtomicLong getFailCount() {
        return failCount;
    }

    public AtomicLong getDelayMissCount() {
        return delayMissCount;
    }
}
