package com.osp.hpe.lwj.service.impl;

import com.osp.hpe.lwj.config.AppConfig;
import com.osp.hpe.lwj.constans.Const;
import com.osp.hpe.lwj.domain.Dependency;
import com.osp.hpe.lwj.service.PomXmlService;
import com.osp.hpe.lwj.util.FileUtil;
import com.osp.hpe.lwj.vo.Para;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.*;

/**
 * Created by lwenjie on 2017/6/9.
 */
@Slf4j
@Service
public class PomXmlServiceImpl implements PomXmlService {

    @Autowired
    private AppConfig appConfig;

    @Setter
    @Getter
    private Para para;

    @Override
    public void geneFilePomXml(Dependency dependency, Para para) {
        this.setPara(para);
        String sourceFileName = "tmp/pom.xml";

        FileUtil.createDir(para.getFilePath());
        String targetFileName = FileUtil.slicePathAndFileName(para.getFilePath(), "pom.xml");
        copyPomXml(sourceFileName, targetFileName, dependency);
    }

    private void copyPomXml(String sourceFileName, String targetFileName, Dependency dependency){
        File sourceFile = new File(sourceFileName);
        File targetFile = new File(targetFileName);
        try {
            BufferedReader reader = new BufferedReader(new FileReader(sourceFile));
            BufferedWriter writer = new BufferedWriter(new FileWriter(targetFile));
            String line = reader.readLine();
            boolean parent = false;
            while (line != null) {

                String newLine;

                if (line.contains("</parent>")){
                    parent = false;
                    writer.write(Const.CLOUD_PARENT_LAST_LINE);
                    writer.newLine();
                }

                newLine = processLine(line, parent, dependency);

                if (line.contains("osp.com.hpe.lwj")){
                    newLine = parentData(line, appConfig.getPomProjectGroupId());
                }

                if (line.contains("auto-coding")){
                    newLine = parentData(line, this.getPara().getArtifactId());
                }

                if (line.contains("1.0-SNAPSHOT")){
                    newLine = parentData(line, this.getPara().getVersion());
                }

                if (line.contains("<parent>")){
                    parent = true;
                }

                writer.write(newLine);
                writer.newLine();
                line = reader.readLine();
            }

            writer.flush();
            reader.close();
            writer.close();
        } catch (FileNotFoundException e){
            log.error("FileNotFoundException:PomXmlServiceImpl.copyPomXml() {}",e.getMessage());
        } catch (IOException e){
            log.error("IOException:PomXmlServiceImpl.copyPomXml() {}", e.getMessage());
        }
    }

    private String processLine(String line, boolean parent, Dependency dependency){
        String newLine = "";
        if (this.para.getSpring().equals("cloud")) {
            if (parent) {
                if (line.contains("groupId")) {
                    newLine = parentData(line, dependency.getGroupId());
                }
                if (line.contains("artifactId")) {
                    newLine = parentData(line, dependency.getArtifactId());
                }
                if (line.contains("version")) {
                    newLine = parentData(line, dependency.getVersion());
                }
            } else {
                newLine = line;
            }
        } else {
            newLine = line;
        }
        return newLine;
    }

    private String parentData(String line, String ele){
        String newline;
        if (ele == null){
            newline = line;
        } else {
            newline = line.substring(0, line.indexOf(">") + 1) + ele + line.substring(line.lastIndexOf("<"));
        }
        return newline;
    }
}
