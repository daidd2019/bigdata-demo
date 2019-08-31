package kafka;

import kafka.read.ReadThread;
import org.apache.commons.cli.*;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class KafakRead {
    private static final Logger log = LoggerFactory.getLogger(KafakRead.class);

    public static void main(String[] args) {
        Options opts = new Options();

        opts.addOption("t",  true, "Topic名字");
        opts.addOption("p", true, "读取的线程数");
        opts.addOption("g", true, "设定读取的group名字");
        opts.addOption("f", true, "配置参数");
        opts.addOption("h", false, "帮助信息");

        CommandLine cl = null;
        try {
            CommandLineParser parser = new DefaultParser();
            cl = parser.parse(opts, args);
            if (cl.hasOption("h") && cl.getOptions().length > 0) {
                PrintHelp(opts);
                System.exit(0);
            }

            String topic = cl.getOptionValue("t", "topic");
            int threads = Integer.parseInt(cl.getOptionValue("p", "1"));
            String group = cl.getOptionValue("g", "group");
            String confFile = cl.getOptionValue("f", "");

            log.info("topic: {}, 组: {}, 读取线程数{}, 配置文件: {}", topic, group, threads, confFile);

            if ("".equals(confFile)) {
                log.info("请设置配置文件参数-f");
                PrintHelp(opts);
                System.exit(0);
            }

            Properties properties = Utils.loadProps(confFile);
            properties.setProperty(ConsumerConfig.GROUP_ID_CONFIG, group);
            ExecutorService pool = Executors.newFixedThreadPool(threads);
            for (int i = 0; i < threads; i ++) {
                pool.submit(new ReadThread(topic, properties, "thread-" + i));
            }

        } catch (ParseException e) {
            log.error("解析参数报错" + e.getMessage());
            PrintHelp(opts);
        } catch (IOException e) {
            log.error("读取配置文件报错 {}", e.getMessage());
        }
    }

    private static void PrintHelp(Options opts) {
        HelpFormatter hf = new HelpFormatter();
        hf.printHelp("参数", opts);
    }
}
