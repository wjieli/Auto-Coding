package com.osp.hpe.lwj.service.impl;

import com.osp.hpe.lwj.config.AppConfig;
import com.osp.hpe.lwj.constans.Const;
import com.osp.hpe.lwj.service.YmlFileService;
import com.osp.hpe.lwj.util.FileUtil;
import com.osp.hpe.lwj.vo.DbPara;
import com.osp.hpe.lwj.vo.Para;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by lwenjie on 2017/6/9.
 */
@Slf4j
@Service
public class YmlFileServiceImpl implements YmlFileService {

    @Autowired
    private AppConfig appConfig;

    @Override
    public void generateYmlFile(Para para) {
        FileUtil.createDir(para.getFilePath());
        try {
            File ymlFile = new File(FileUtil.slicePathAndFileName(para.getFilePath(), para.getArtifactId() + ".yml"));
            BufferedWriter ymlWriter = new BufferedWriter(new FileWriter(ymlFile));
            List<String> ymlList = new ArrayList<>();
            if (para.getDb() != null){
                ymlList = writeDbConfig(para.getDb());
            }

            for (String line: ymlList){
                ymlWriter.write(line);
            }

            writeConfig(para, ymlWriter);
            ymlWriter.flush();
            ymlWriter.close();
        } catch (IOException e){
            log.error("IOException: YmlFileServiceImpl.generateYmlFile[{}]", e.getMessage());
        }
    }

    private List<String> writeDbConfig(DbPara[] dbParas) throws IOException{

        List<String> list = new ArrayList<>();
        list.add(FileUtil.getFormatString("spring:"));
        list.add(FileUtil.getFormatString("datasource:", Const.ONE));

        for (int i = 0; i < dbParas.length; i++) {
            list.add(FileUtil.getFormatString(Const.DATA_SOURCE_ORDER[i] + ":", Const.TWO));
            DbPara dbPara = dbParas[i];
            Map<String, String> dataSource;

            if (Const.ORACLE.equals(dbPara.getType())){
                dataSource = appConfig.getDataSourceConfigMap().get(Const.ORACLE);
                dataSource.put(Const.URL, dataSource.get(Const.URL).replace("${share.oracle.address}", dbPara.getUrl()));

            } else {
                dataSource = appConfig.getDataSourceConfigMap().get(Const.MYSQL);
                dataSource.put(Const.URL, dataSource.get(Const.URL).replace("${share.mysql.address}", dbPara.getUrl())
                        .replace("${dataBase}", dbPara.getDbName()));
            }
            dataSource.put("username", dbPara.getUserName());
            dataSource.put("password", dbPara.getPassword());

            for (Map.Entry<String, String> entry: dataSource.entrySet()){
                list.add(FileUtil.getFormatString(entry.getKey() + ": " + entry.getValue(), Const.THREE));
            }
        }

        list.add(FileUtil.getFormatString("jpa:", Const.ONE));
        list.add(FileUtil.getFormatString("show-sql: " + appConfig.getJpa().get("showSql"),
                Const.TWO));
        list.add(FileUtil.getFormatString("hibernate:", Const.TWO));
        list.add(FileUtil.getFormatString("ddl-auto: " + appConfig.getJpa().get("hibernateDdlAuto"),
                Const.THREE));
        list.add(FileUtil.getFormatString("properties:", Const.TWO));
        list.add(FileUtil.getFormatString("hibernate:", Const.THREE));
        list.add(FileUtil.getFormatString("dialect: " + appConfig.getJpa().get("propertiesHibernateDialect"), Const.FOUR));

        return list;
    }

    private void writeConfig(Para para, BufferedWriter ymlWriter){

    }
}
