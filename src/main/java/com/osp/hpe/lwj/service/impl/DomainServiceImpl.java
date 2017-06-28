package com.osp.hpe.lwj.service.impl;

import com.osp.hpe.lwj.config.AppConfig;
import com.osp.hpe.lwj.constans.Const;
import com.osp.hpe.lwj.service.DomainService;
import com.osp.hpe.lwj.util.ConnectDBUtil;
import com.osp.hpe.lwj.util.FileUtil;
import com.osp.hpe.lwj.vo.DbPara;
import com.osp.hpe.lwj.vo.Para;
import com.osp.hpe.lwj.vo.TableProperty;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by lwenjie on 2017/6/26.
 */
@Slf4j
@Service
public class DomainServiceImpl implements DomainService {

    @Autowired
    private AppConfig appConfig;

    @Override
    public void generateEntityFile(Para para) {
        String[] projectPaths = para.getArtifactId().split("-");
        String childPath = Const.COMMENT_PATH;
        for (String pj: projectPaths) {
            childPath = FileUtil.slicePathAndFileName(childPath, pj);
        }
        if (para.getDb() != null) {
            for (int i = 0; i < para.getDb().length; i++) {
                String lastPath = childPath + "/domain/" + Const.DATA_SOURCE_ORDER[i];
                String path = FileUtil.slicePathAndFileName(para.getFilePath(), lastPath);
                FileUtil.createDir(path);
                getTableInfo(para.getDb()[i], path);
            }
        }
    }

    private void getTableInfo(DbPara dbPara, String path){
        String url = appConfig.getDataSourceConfigMap().get(dbPara.getType()).get("url")
                .replace("${share.oracle.address}", dbPara.getUrl()).replace("${share.mysql.address}", dbPara.getUrl())
                .replace("${dataBase}", dbPara.getDbName());
        Connection con = ConnectDBUtil.getConnection(appConfig.getDataSourceConfigMap().get(dbPara.getType())
                .get("driverClassName"), url, dbPara.getUserName(), dbPara.getPassword());
        List<TableProperty> tableProperties = new ArrayList<>();
        for (String tableName: dbPara.getTableName()){
            ConnectDBUtil.getTableInfo(tableName, con, tableProperties, appConfig);
            writeFile(path, tableProperties, tableName);
        }

    }

    private void writeFile(String path, List<TableProperty> tableProperties, String tableName){
        tableName = analyzeTableName(tableName);
        String fileName = FileUtil.slicePathAndFileName(path, tableName + ".java");
        File entityFile = new File(fileName);
        List<String> lineList = fillLine(tableProperties, tableName, path);
        BufferedWriter writer = null;
        try{
            writer = new BufferedWriter(new FileWriter(entityFile));
            for (String line: lineList){
                writer.write(line);
            }
        } catch (IOException e){
            log.error("IOException: Write File [{}] is error!", fileName);
        } finally {
            try {
                writer.close();
            } catch (IOException e){
                log.error("IOException: Close File [{}] is error!", fileName);
            } catch (NullPointerException e){
                log.error(e.getMessage());
            }
        }
    }

    private String analyzeTableName(String tableName){
        String[] childNames = tableName.toLowerCase().split("_");
        String newTableName = "";
        for (int i = 0; i < childNames.length; i++){
            char[] columnNameArray = childNames[i].toCharArray();
            newTableName = newTableName + String.valueOf(columnNameArray[0]).toUpperCase() + childNames[i].substring(1);
        }
        return newTableName;
    }

    private List<String> fillLine(List<TableProperty> tableProperties, String tableName, String path){
        List<String> lineList = new ArrayList<>();
        String pack = path.substring(path.indexOf("com")).replace("/", ".") + ";";

        lineList.add(FileUtil.getJavaFormatString("package " + pack));
        lineList.add(Const.NEWLINE);
        lineList.add(FileUtil.getJavaFormatString("import lombok.Data;"));
        lineList.add(FileUtil.getJavaFormatString("import javax.persistence.Entity;"));
        lineList.add(FileUtil.getJavaFormatString("import javax.persistence.Id;"));
        lineList.add(Const.NEWLINE);
        lineList.add(FileUtil.getJavaFormatString("@Data"));
        lineList.add(FileUtil.getJavaFormatString("@Entity"));
        lineList.add(FileUtil.getJavaFormatString("public class " + tableName + " {"));
        lineList.add(Const.NEWLINE);
        for (TableProperty property: tableProperties){
            lineList.add(sliceLine(property));
        }
        lineList.add("}");
        return lineList;
    }

    private String sliceLine(TableProperty property){
        String line;
        if (property.isPrimary()){
            line = FileUtil.getJavaFormatString("@Id private " + property.getType() + " " + property.getColumnName() +
                    ";", Const.ONE);
        } else {
            line = FileUtil.getJavaFormatString("private " + property.getType() + " " + property.getColumnName() +
                    ";", Const.ONE);
        }
        return line;
    }
}
