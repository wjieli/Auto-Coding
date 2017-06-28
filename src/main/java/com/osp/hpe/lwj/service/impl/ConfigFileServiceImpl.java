package com.osp.hpe.lwj.service.impl;

import com.osp.hpe.lwj.constans.Const;
import com.osp.hpe.lwj.service.ConfigFileService;
import com.osp.hpe.lwj.util.FileUtil;
import com.osp.hpe.lwj.vo.Para;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by lwenjie on 2017/6/9.
 */
@Slf4j
@Service
public class ConfigFileServiceImpl implements ConfigFileService {

    private final String[] DATA_SOURCE_LINE = {"@Bean(name = \"{level}DataSource\")",
        "@Qualifier(\"{level}DataSource\")", "@ConfigurationProperties(prefix = \"spring.datasource.{level}\")",
            "public DataSource {level}DataSource() {return DataSourceBuilder.create().build();}"
    };

    private final String[] JPA_CONFIG_LINE = {"import org.springframework.beans.factory.annotation.Autowired;",
        "import org.springframework.beans.factory.annotation.Qualifier;",
        "import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties;",
            "import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;",
            "import org.springframework.context.annotation.Bean;",
            "import org.springframework.context.annotation.Configuration;",
            "import org.springframework.data.jpa.repository.config.EnableJpaRepositories;",
            "import org.springframework.jdbc.core.JdbcTemplate;",
            "import org.springframework.orm.jpa.JpaTransactionManager;",
            "import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;",
            "import org.springframework.transaction.PlatformTransactionManager;",
            "import org.springframework.transaction.annotation.EnableTransactionManagement;",
            "import javax.persistence.EntityManager;",
            "import javax.sql.DataSource;"
    };

    private final String[] CLASS_NOTE = {Const.CONFIGURATION_NOTE, "@EnableTransactionManagement",
    "@EnableJpaRepositories(entityManagerFactoryRef = \"{entityManagerFactory}\", transactionManagerRef = \"{transactionManager}\",basePackages = {\"{repositoryPath}\"})"};
    @Override
    public void generateConfigFile(Para para) {
        String[] projectPaths = para.getArtifactId().split("-");
        String childPath = Const.COMMENT_PATH;
        for (String pj: projectPaths) {
            childPath = FileUtil.slicePathAndFileName(childPath, pj);
        }
        childPath = childPath + "/config";
        String path = FileUtil.slicePathAndFileName(para.getFilePath(), childPath);
        FileUtil.createDir(path);

        writeDataSourceFile(path, para);
        writeJpaConfigCode(path, para);

    }

    private void writeDataSourceFile(String path, Para para){
        String dsConfigFileName = FileUtil.slicePathAndFileName(path, "DataSourceConfig.java");
        try {
            File dsConfigFile = new File(dsConfigFileName);
            BufferedWriter dsConfigWriter = new BufferedWriter(new FileWriter(dsConfigFile));
            List<String> dataSourceLineList = fillDataSourceLineList(path, para);

            for (String line: dataSourceLineList){
                dsConfigWriter.write(line);
            }

            dsConfigWriter.flush();
            dsConfigWriter.close();
        } catch (IOException e){
            log.error("IOException: ConfigFileServiceImpl.writeDataSourceFile[{}]", e.getMessage());
        }
    }

    private void writeJpaConfigCode(String path, Para para){
        for (int i = 0; i < para.getDb().length; i++) {
            String configFileName = FileUtil.slicePathAndFileName(path, Const.FILE_NAME[i] +
                    Const.JPA_CONFIG + Const.JAVA_FILE);
            List<String> jpaLineList = fillJpaLineList(path, i);
            try {
                File configFile = new File(configFileName);
                BufferedWriter configWriter = new BufferedWriter(new FileWriter(configFile));

                for (String line: jpaLineList){
                    if (line == ""){
                        configWriter.newLine();
                    } else {
                        configWriter.write(line);
                    }
                }

                configWriter.flush();
                configWriter.close();

            } catch (IOException e){
                log.error("IOException: ConfigFileServiceImpl.writeJpaConfigCode[{}] on [{}]", e.getMessage(), configFileName);
            }
        }
    }

    private List<String> fillDataSourceLineList(String path, Para para){
        List<String> list = new ArrayList<>();

        list.add(FileUtil.getFormatString("package " + path.substring(path.indexOf("com"))
                .replace("/", ".") + ";"));
        list.add("");
        list.add(FileUtil.getFormatString("import org.springframework.beans.factory.annotation.Qualifier;"));
        list.add(FileUtil.getFormatString("import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder;"));
        list.add(FileUtil.getFormatString("import org.springframework.context.annotation.Bean;"));
        list.add(FileUtil.getFormatString("import org.springframework.context.annotation.Configuration;"));
        list.add(FileUtil.getFormatString("import org.springframework.context.annotation.Primary;"));
        list.add(FileUtil.getFormatString("import org.springframework.boot.context.properties.ConfigurationProperties;"));
        list.add("");
        list.add(FileUtil.getFormatString("import javax.sql.DataSource;"));
        list.add("");
        list.add(FileUtil.getFormatString(Const.CONFIGURATION_NOTE));
        list.add(FileUtil.getJavaFormatString("public class DataSourceConfig {"));
        list.add("");

        list.add(FileUtil.getJavaFormatString(Const.PRIMARY_NOTE, Const.ONE));
        for (int i = 0; i < para.getDb().length; i++){
            for (String line: DATA_SOURCE_LINE){
                list.add(FileUtil.getJavaFormatString(line.replace(Const.LEVEL_REPLACE, Const.LEVEL[i]), Const.ONE));
            }
        }

        list.add("");
        list.add("}");

        return list;
    }

    private List<String> fillJpaLineList(String path, int i){
        List<String> jpaLineList = new ArrayList<>();
        jpaLineList.add(FileUtil.getJavaFormatString("package " + path.substring(path.indexOf("com"))
                .replace("/", ".") + ";"));
        jpaLineList.add("");
        for (String line: JPA_CONFIG_LINE){
            jpaLineList.add(FileUtil.getJavaFormatString(line));
        }
        if ("primary".equals(Const.LEVEL[i])) {
            jpaLineList.add(FileUtil.getJavaFormatString("import org.springframework.context.annotation.Primary;"));
        }
        jpaLineList.add("");

        for (String line: CLASS_NOTE) {
            jpaLineList.add(FileUtil.getJavaFormatString(getReplaceLine(line, path, i)));
        }
        jpaLineList.add(FileUtil.getJavaFormatString("public class " + Const.FILE_NAME[i] + Const.JPA_CONFIG + " {"));

        jpaLineList.add(FileUtil.getJavaFormatString(Const.AUTOWIRED_NOTE, Const.ONE));
        jpaLineList.add(FileUtil.getJavaFormatString("@Qualifier(\"{level}DataSource\")".replace(Const.LEVEL_REPLACE, Const.LEVEL[i]), Const.ONE));
        jpaLineList.add(FileUtil.getJavaFormatString("private DataSource {level}DataSource;".replace(Const.LEVEL_REPLACE, Const.LEVEL[i]), Const.ONE));
        jpaLineList.add("");
        jpaLineList.add(FileUtil.getJavaFormatString(Const.AUTOWIRED_NOTE, Const.ONE));
        jpaLineList.add(FileUtil.getJavaFormatString("private JpaProperties jpaProperties;", Const.ONE));

        isNotPrimary(jpaLineList, i);
        jpaLineList.add(FileUtil.getJavaFormatString("@Bean(name = \"entityManager{fileName}\")".replace(Const.FILE_NAME_REPLACE, Const.FILE_NAME[i]), Const.ONE));
        jpaLineList.add(FileUtil.getJavaFormatString("public EntityManager entityManager(EntityManagerFactoryBuilder builder)" +
                " {return entityManagerFactory{fileName}(builder).getObject().createEntityManager();}".replace(Const.FILE_NAME_REPLACE, Const.FILE_NAME[i]), Const.ONE));

        isNotPrimary(jpaLineList, i);
        jpaLineList.add(FileUtil.getJavaFormatString("@Bean(name = \"entityManagerFactory{fileName}\")".replace(Const.FILE_NAME_REPLACE, Const.FILE_NAME[i]), Const.ONE));
        jpaLineList.add(FileUtil.getJavaFormatString(("public LocalContainerEntityManagerFactoryBean entityManagerFactory{fileName}(EntityManagerFactoryBuilder builder) {return builder.dataSource({level}DataSource).properties(jpaProperties.getHibernateProperties({level}DataSource)).packages(\"" + path.substring(path.indexOf("com"), path.lastIndexOf("/")).replace("/", ".") + ".domain." + Const.LEVEL[i] + "\").persistenceUnit(\"{level}PersistenceUnit\").build();}")
                .replace(Const.LEVEL_REPLACE, Const.LEVEL[i]).replace(Const.FILE_NAME_REPLACE, Const.FILE_NAME[i]), Const.ONE));

        isNotPrimary(jpaLineList, i);
        jpaLineList.add(FileUtil.getJavaFormatString("@Bean(name = \"transactionManager{fileName}\")".replace(Const.FILE_NAME_REPLACE, Const.FILE_NAME[i]), Const.ONE));
        jpaLineList.add(FileUtil.getJavaFormatString("public PlatformTransactionManager transactionManager{fileName}(EntityManagerFactoryBuilder builder) {return new JpaTransactionManager(entityManagerFactory{fileName}(builder).getObject());}".replace(Const.FILE_NAME_REPLACE, Const.FILE_NAME[i]), Const.ONE));

        isNotPrimary(jpaLineList, i);
        jpaLineList.add(FileUtil.getJavaFormatString("@Bean(name = \"{level}Jdbc\")".replace(Const.LEVEL_REPLACE, Const.LEVEL[i]), Const.ONE));
        jpaLineList.add(FileUtil.getJavaFormatString("public JdbcTemplate {level}Jdbc(){return new JdbcTemplate({level}DataSource);}".replace(Const.LEVEL_REPLACE, Const.LEVEL[i]), Const.ONE));
        jpaLineList.add("}");

        return jpaLineList;
    }

    private String getReplaceLine(String line, String path, int i){
        return line.replace("{entityManagerFactory}", "entityManagerFactory" + Const.FILE_NAME[i])
                .replace("{transactionManager}", "transactionManager" + Const.FILE_NAME[i])
                .replace("{repositoryPath}", path.substring(path.indexOf("com"), path.lastIndexOf("/")).replace("/", ".")
                + ".repository." + Const.LEVEL[i]);
    }

    private void isNotPrimary(List<String> jpaLineList, int i){
        if ("primary".equals(Const.LEVEL[i])) {
            jpaLineList.add(FileUtil.getJavaFormatString(Const.PRIMARY_NOTE, Const.ONE));
        }
    }
}
