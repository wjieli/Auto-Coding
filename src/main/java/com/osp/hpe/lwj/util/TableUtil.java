package com.osp.hpe.lwj.util;

import com.osp.hpe.lwj.config.AppConfig;
import com.osp.hpe.lwj.vo.TableProperty;

/**
 * Created by lwenjie on 2017/6/20.
 */
public class TableUtil {

    public static String analyzeColumnName(String columnName){

        String[] childNames = columnName.toLowerCase().split("_");
        String newColumnName = childNames[0];
        for (int i = 1; i < childNames.length; i++){
            char[] columnNameArray = childNames[i].toCharArray();
            newColumnName = newColumnName + String.valueOf(columnNameArray[0]).toUpperCase() + childNames[i].substring(1);
        }
        return newColumnName;
    }

    public static String analyzeType(String type, int length, AppConfig appConfig){
        String newType;
        newType = appConfig.getColumnTypeMap().get(type);
        if (newType == null && appConfig.getColumnTypeLengthMap().get(type) != null){
            newType = appConfig.getColumnTypeLengthMap().get(type).get(length + "");
        }
        if (newType == null){
            newType = "String";
        }
        return newType;
    }
}
