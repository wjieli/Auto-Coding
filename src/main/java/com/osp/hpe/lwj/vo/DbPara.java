package com.osp.hpe.lwj.vo;

import lombok.Data;

/**
 * Created by lwenjie on 2017/6/11.
 */
@Data
public class DbPara {
    private String type;
    private String url;
    private String userName;
    private String password;
    private String dbName;
    private String[] tableName;
}
