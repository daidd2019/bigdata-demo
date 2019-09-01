package hadoop.hdfs;

import com.sun.corba.se.impl.ior.OldJIDLObjectKeyTemplate;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.junit.Test;

import java.io.*;
import java.util.Properties;

import static org.junit.Assert.*;

public class HadoopUtilsTest {

    @Test
    public void getConf() throws IOException {
        Properties properties = new Properties();
        properties.setProperty("fs.defaultFS","hdfs://hdp");
        properties.setProperty("dfs.nameservices","hdp");
        String propFile = "/tmp/tmp.properties";
        try(OutputStream os = new FileOutputStream(new File(propFile))) {
            properties.store(os, "test");
            Configuration conf = HadoopUtils.getConf(propFile);
            properties.keySet().forEach( x ->
                    assertEquals( properties.getProperty((String)x), conf.get((String)x)));
        }
    }

//    @Test
    public void KerberosLogin() {
        HadoopUtils.KerberosLogin("/tmp/ck.dev.keytab", "dev1@CENTOS74.COM", "/tmp/krb5.conf");
    }

//    @Test
    public void HadoopTest() {
        //load app.properties
        String appFile = "../hadoop-op/src/main/conf/app.properties";
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
            FileSystem fs = HadoopUtils.getFs("../hadoop-op/src/main/conf/hadoop.properties");
            FileStatus[] statuses = fs.listStatus(new Path("/"));
            for (FileStatus status: statuses) {
                System.out.println(status.getPath() + "\t" + status.getPermission() + "\t" + status.getReplication());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}