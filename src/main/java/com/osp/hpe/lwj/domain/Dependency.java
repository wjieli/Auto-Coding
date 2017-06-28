package com.osp.hpe.lwj.domain;

import lombok.Data;

/**
 * Created by lwenjie on 2017/6/9.
 */
@Data
public class Dependency {
    private String groupId;
    private String artifactId;
    private String version;
}
