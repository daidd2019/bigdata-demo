package hadoop.hdfs;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.security.UserGroupInformation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

public class HadoopUtils {
    private static final Logger log = LoggerFactory.getLogger(HadoopUtils.class);
    public static FileSystem getFs(String propFile) throws IOException {
        Configuration conf = getConf(propFile);
        FileSystem fs =  null;
        fs = FileSystem.get(conf);
        return fs;
    }

    public static Configuration getConf(String propsFile) throws IOException {
        Configuration conf = new Configuration();
        if (null != propsFile) {
            try (InputStream is = new FileInputStream(new File(propsFile)))
            {
                Properties  props = new Properties();
                props.load(is);
                Iterator<Map.Entry<Object, Object>> it = props.entrySet().iterator();
                while(it.hasNext()) {
                    Map.Entry<Object, Object> entry = it.next();
                    conf.set((String)entry.getKey(), (String)entry.getValue());
                }
            }
        }
        return  conf;
    }

    public static void KerberosLogin(String keytab, String principal, String krbFile) {
        System.setProperty("java.security.krb5.conf", krbFile);
        Configuration conf = new Configuration();
        conf.set("hadoop.security.authentication", "kerberos");
        UserGroupInformation.setConfiguration(conf);
        try {
            UserGroupInformation.loginUserFromKeytab(principal, keytab);
            log.info("Kerberos certification succed");
        }
        catch (IOException e) {
            log.error("Kerberos certification failed", e);
        }

    }
}
