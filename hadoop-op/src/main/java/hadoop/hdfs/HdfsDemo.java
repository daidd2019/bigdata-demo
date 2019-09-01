package hadoop.hdfs;

import org.apache.commons.cli.*;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.Path;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.Properties;

public class HdfsDemo {
    private static final Logger log = LoggerFactory.getLogger(HdfsDemo.class);

    public static void main(String[] args) {
        Options opts = new Options();
        opts.addOption("a", true, "应用程序配置文件");
        opts.addOption("f", true, "Hadoop 配置文件");
        opts.addOption("h", false, "帮助信息");

        CommandLine cl = null;
        String appFile = null;
        String hadoopFile = null;
        try {
            CommandLineParser parser = new DefaultParser();
            cl = parser.parse(opts, args);
            if (cl.hasOption("h") || cl.getOptions().length == 0) {
                PrintHelp(opts);
                System.exit(0);
            }
            appFile = cl.getOptionValue("a");
            hadoopFile = cl.getOptionValue("f");

        } catch (ParseException e) {
            log.error("解析参数报错" + e.getMessage());
            PrintHelp(opts);
            System.exit(1);
        }

        try(InputStream is = new FileInputStream(new File(appFile))) {
            Properties properties = new Properties();
            properties.load(is);
            String keytab = properties.getProperty("kerberos.keytab");
            String principal = properties.getProperty("kerberos.principal");
            String krbfile = properties.getProperty("kerberos.krbfile");

            HadoopUtils.KerberosLogin(keytab, principal, krbfile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            org.apache.hadoop.fs.FileSystem fs = HadoopUtils.getFs(hadoopFile);
            //查看根目录
            FileStatus[] statuses = fs.listStatus(new Path("/"));
            for (FileStatus status: statuses) {
                System.out.println(status.getPath() + "\t" + status.getPermission() + "\t" + status.getReplication());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void PrintHelp(Options opts) {
        HelpFormatter hf = new HelpFormatter();
        hf.printHelp("参数", opts);
    }
}
