package kafka.read;


import kafka.utils.ShutdownableThread;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.Properties;

/**
 * Created by sponge on 2017/3/24 0024.
 */
public class ReadThread extends ShutdownableThread
{
    private static final Logger log  = LoggerFactory.getLogger(ReadThread.class);

    private final KafkaConsumer<Integer, String> consumer;
    private final String topic;
    private int number = 0;

    public ReadThread(String topic, Properties properties, String threadName)
    {
        super(threadName, false);
        Properties props = new Properties();
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "true");
        props.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, "1000");
        props.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, "30000");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,   "org.apache.kafka.common.serialization.IntegerDeserializer");
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");

        if (properties != null) {
            props.putAll(properties);
        }
        consumer = new KafkaConsumer<>(props);
        this.topic = topic;
    }

    @Override
    public void doWork() {
        consumer.subscribe(Collections.singletonList(this.topic));
        ConsumerRecords<Integer, String> records = consumer.poll(1000);
        for (ConsumerRecord<Integer, String> record : records) {
            log.info("Received message : (" + record.key() + ", " + record.value() + ") at offset "
                    + record.offset() + " from thread :" + this.getName() + " number " + number++);
        }
    }


    @Override
    public boolean isInterruptible() {
        return false;
    }
}