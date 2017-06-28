package com.osp.hpe.lwj.constans;

/**
 * Created by lwenjie on 2017/6/9.
 */
public class Const {

    public static final String AUTOWIRED_NOTE = "@Autowired";
    public static final String PRIMARY_NOTE = "@Primary";
    public static final String CONFIGURATION_NOTE = "@Configuration";
    public static final String NEWLINE = "\n";
    public static final String COMMENT_PATH = "src/main/java/com/";
    //pomFile
    public static final String CLOUD_PARENT_LAST_LINE = "\t\t<relativePath/>";


    //ymlFile
    public static final String LINE_RETRACT = "  ";
    public static final int ONE = 1;
    public static final int TWO = 2;
    public static final int THREE = 3;
    public static final int FOUR = 4;
    public static final String[] DATA_SOURCE_ORDER = {"primary", "secondary"};
    public static final String ORACLE = "oracle";
    public static final String URL = "url";
    public static final String MYSQL = "mySql";

    //configFile
    public static final String[] LEVEL = {"primary", "secondary"};
    public static final String JAVA_FILE = ".java";
    public static final String JPA_CONFIG = "JpaConfig";
    public static final String[] FILE_NAME = {"Primary", "Secondary"};
    public static final String FILE_NAME_REPLACE = "{fileName}";
    public static final String LEVEL_REPLACE = "{level}";
}
