package com.osp.hpe.lwj.util;

import com.osp.hpe.lwj.constans.Const;
import lombok.extern.slf4j.Slf4j;

import java.io.File;

/**
 * Created by lwenjie on 2017/6/9.
 */
@Slf4j
public class FileUtil {

    public static void createDir(String path){
        File dir = new File(path);
        if (!dir.exists()){
            if (dir.mkdirs()){
                log.info("Path create success! [{}]", path);
            } else {
                log.error("Path create failed! [{}]", path);
            }
        }
    }

    public static String slicePathAndFileName(String path, String fileName){
        String last = path.substring(path.length() - 1, path.length());
        if ("/".equals(last)){
            return path + fileName;
        }
        return path + "/" + fileName;
    }

    public static String getFormatString(String line, int retract){
        String newLine = line;
        for (int i = 1; i <= retract; i++){
            newLine = Const.LINE_RETRACT + newLine;
        }
        return newLine + "\n";
    }

    public static String getFormatString(String line){
        return getFormatString(line, 0);
    }

    public static String getJavaFormatString(String line, int retract){
        String newLine = line;
        for (int i = 1; i <= retract; i++){
            newLine = "\t" + newLine;
        }
        return newLine + "\n";
    }

    public static String getJavaFormatString(String line){
        return getJavaFormatString(line, 0);
    }
}
