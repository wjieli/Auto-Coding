package com.osp.hpe.lwj.vo;

import lombok.Data;

/**
 * Created by lwenjie on 2017/6/20.
 */
@Data
public class TableProperty {

    private String columnName;
    private String type;
    private boolean isPrimary = false;
}
