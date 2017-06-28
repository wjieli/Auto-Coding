package com.osp.hpe.lwj.service;

import com.osp.hpe.lwj.domain.Dependency;
import com.osp.hpe.lwj.vo.Para;

/**
 * Created by lwenjie on 2017/6/9.
 */
public interface PomXmlService {

    public void geneFilePomXml(Dependency dependency, Para para);
}
