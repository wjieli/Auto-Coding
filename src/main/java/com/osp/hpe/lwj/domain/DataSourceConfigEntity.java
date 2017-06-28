package com.osp.hpe.lwj.domain;

import lombok.Data;

/**
 * Created by lwenjie on 2017/6/11.
 */
@Data
public class DataSourceConfigEntity {

    private String type;
    private String url;
    private String userName;
    private String password;
    private String driverClassName;
    private String jpaShowSql;
    private String jpaHibernateDdlAuto;
    private String jpaPropertiesHibernateDialect;
}
