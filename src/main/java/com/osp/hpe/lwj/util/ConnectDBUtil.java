package com.osp.hpe.lwj.util;

import com.osp.hpe.lwj.config.AppConfig;
import com.osp.hpe.lwj.vo.TableProperty;
import lombok.extern.slf4j.Slf4j;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by lwenjie on 2017/6/20.
 */
@Slf4j
public class ConnectDBUtil {

    public static Connection getConnection(String driverClass, String url, String user, String password){
        Connection con = null;
        try{
            Class.forName(driverClass);
            con = DriverManager.getConnection(url, user, password);
            return con;
        } catch (ClassNotFoundException e){
            log.error("[{}] not found", driverClass);
        } catch (SQLException e){
            log.error("Connect DB exception! [{}]", url);
        }
        return con;
    }

    public static void getTableInfo(String tableName, Connection con, List<TableProperty> tableProperties, AppConfig appConfig){
        String sql = "select * from " + tableName;
        List<TableProperty> tablePropertyList = new ArrayList<>();
        try {
            PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            ResultSetMetaData metaData = rs.getMetaData();
            DatabaseMetaData data = con.getMetaData();
            ResultSet primaryKeys = data.getPrimaryKeys(null, null, tableName);
            for (int i = 1; i < metaData.getColumnCount() ; i++) {
                TableProperty tableProperty = new TableProperty();
                tableProperty.setColumnName(TableUtil.analyzeColumnName(metaData.getColumnName(i)));
                tableProperty.setType(TableUtil.analyzeType(metaData.getColumnTypeName(i), metaData.getPrecision(i), appConfig));
                tablePropertyList.add(tableProperty);
            }
            while( primaryKeys.next() ) {
                for (TableProperty property: tablePropertyList){
                    if (property.getColumnName().equals(primaryKeys.getObject(4))){
                        property.setPrimary(true);
                    }
                    tableProperties.add(property);
                }
            }

        } catch (SQLException e){
            log.error("select * from [{}] is error", tableName);
        }
    }
}
