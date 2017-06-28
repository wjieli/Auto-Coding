package com.osp.hpe.lwj.config;

import com.osp.hpe.lwj.domain.Dependency;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Created by lwenjie on 2017/6/9.
 */
@Data
@Component
@ConfigurationProperties(prefix = "autoCoding")
public class AppConfig {

    private String pomProjectGroupId;

    private Map<String, Dependency> pomParent;

    private Map<String, Map<String, String>> dataSourceConfigMap;

    private Map<String, String> jpa;

    private Map<String, String> columnTypeMap;

    private Map<String, Map<String, String>> columnTypeLengthMap;

//    private

}
