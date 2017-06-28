package com.osp.hpe.lwj.service.impl;

import com.osp.hpe.lwj.config.AppConfig;
import com.osp.hpe.lwj.constans.Const;
import com.osp.hpe.lwj.domain.Dependency;
import com.osp.hpe.lwj.service.*;
import com.osp.hpe.lwj.util.FileUtil;
import com.osp.hpe.lwj.vo.Para;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by lwenjie on 2017/6/9.
 */
@Service
public class AutoCodingServiceImpl implements AutoCodingService {

    @Autowired
    private PomXmlService pomXmlService;

    @Autowired
    private YmlFileService ymlFileService;

    @Autowired
    private ConfigFileService configFileService;

    @Autowired
    private DomainService domainService;

    @Autowired
    private AppConfig appConfig;



    @Override
    public void autoCoding(Para para) {

//        Dependency dependency = new Dependency();
        para.setFilePath(FileUtil.slicePathAndFileName(para.getFilePath(), para.getArtifactId()));
        if (para.getSpring() == null || para.getSpring().equals("cloud")) {
            pomXmlService.geneFilePomXml(appConfig.getPomParent().get("cloud"), para);
        }

        if (para.getDb() != null){
            ymlFileService.generateYmlFile(para);
            configFileService.generateConfigFile(para);
            domainService.generateEntityFile(para);
        }
    }
}
