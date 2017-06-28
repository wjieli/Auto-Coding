import com.osp.hpe.lwj.domain.Dependency;
import com.osp.hpe.lwj.service.impl.PomXmlServiceImpl;
import com.osp.hpe.lwj.util.ConnectDBUtil;
import com.osp.hpe.lwj.vo.Para;
import org.junit.Test;

import java.sql.Connection;

/**
 * Created by lwenjie on 2017/6/9.
 */
public class AutoCodingTest {

    @Test
    public void testPomXmlService(){
        Dependency dependency = new Dependency();
        dependency.setGroupId("com.hpe.ms");
        dependency.setArtifactId("ms-spring-cloud-service-parent");
        dependency.setVersion("1.5.3.RELEASE");

        Para para = new Para();
        para.setFilePath("test");
        para.setArtifactId("osp-test");
        para.setVersion("1.0-SNAPSHOT");

        PomXmlServiceImpl service = new PomXmlServiceImpl();
        service.geneFilePomXml(dependency, para);
    }

    @Test
    public void testGenerateDomain(){
        String driverClass = "com.mysql.jdbc.Driver";
        String url = "jdbc:mysql://15.116.20.163:3306/osp_ftp?useUnicode=true&characterEncoding=UTF-8&useSSL=false";
        String user = "osp";
        String password = "Osp_ctrm_2014";
        String tableName = "task";
        Connection con = ConnectDBUtil.getConnection(driverClass, url, user, password);
//        ConnectDBUtil.getTableInfo(tableName, con);
    }

    @Test
    public void testString(){
        String s = "abcd";
        char[] chars = s.toCharArray();
        String first = String.valueOf(chars[0]).toUpperCase();
        System.out.println(first);
    }
}
