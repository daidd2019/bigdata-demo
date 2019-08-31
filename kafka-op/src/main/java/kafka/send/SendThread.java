package kafka.send;

import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;
import java.util.Random;

/**
 * Created by sponge on 2017/3/25 0025.
 */
public class SendThread extends Thread {

    private static final Logger log = LoggerFactory.getLogger(SendThread.class);

    private final KafkaProducer<Integer, String> producer;
    private final String topic;
    private final int numEvents;
    private final Random random;


    public SendThread(String topic, Properties props, int numEvents) {
        this.topic = topic;
        this.numEvents = numEvents;
        this.random = new Random();

        props.put("key.serializer", "org.apache.kafka.common.serialization.IntegerSerializer");
        props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        producer = new KafkaProducer<Integer, String>(props);
    }

    public void run() {
        int messageNo = 1;
        log.info("Begin send message!");
        while (messageNo ++ <= this.numEvents) {
            // produce random word
            StringBuilder sb = new StringBuilder();
            int wordNums = randomInt(5, 16);
            for (int i =0; i < wordNums; i ++) {
                sb.append(randString(randomInt(3,10))).append(" ");
            }
            String messageStr = sb.toString();

            long startTime = System.currentTimeMillis();

            producer.send(new ProducerRecord<Integer, String>(topic,
                    messageNo,
                    messageStr), new DemoCallBack(startTime, messageNo, messageStr));
            log.debug("Send message messageNo[" + messageNo + "] Message: " + messageStr);

            try {
                Thread.sleep(randomInt(500, 1000));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private static class DemoCallBack implements Callback {

        private long startTime;
        private int key;
        private String message;

        public DemoCallBack(long startTime, int key, String message) {
            this.startTime = startTime;
            this.key = key;
            this.message = message;
        }

        /**
         * A callback method the user can implement to provide asynchronous handling of request completion. This method will
         * be called when the record sent to the server has been acknowledged. Exactly one of the arguments will be
         * non-null.
         *
         * @param metadata  The metadata for the record that was sent (i.e. the partition and offset). Null if an error
         *                  occurred.
         * @param exception The exception thrown during processing of this record. Null if no error occurred.
         */
        public void onCompletion(RecordMetadata metadata, Exception exception) {
            long elapsedTime = System.currentTimeMillis() - startTime;
            if (metadata != null) {
                log.info(
                        "topic [" + metadata.topic() + "] " + "message(" + key + ", " + message + ") sent to partition("
                                + metadata.partition() + "), " +
                                "offset(" + metadata.offset() + ") in " + elapsedTime + " ms");
            } else {
                exception.printStackTrace();
            }
        }
    }

    private  int randomInt(int min, int max) {
        return this.random.nextInt(max) % (max - min + 1) + min;
    }

    private String randString(int length) {
        String base = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        int charLength = base.length();
        StringBuilder sb = new StringBuilder();
        for(int i =0; i < length; i++){
            sb.append(base.charAt(this.random.nextInt(charLength)));
        }
        return sb.toString();
    }

}