package com.osp.hpe.lwj.vo;

import lombok.Data;

/**
 * Created by lwenjie on 2017/6/9.
 */
@Data
public class Para {
    private DbPara[] db;
    private String spring;
    private String artifactId;
    private String version;
    private String filePath;
}
