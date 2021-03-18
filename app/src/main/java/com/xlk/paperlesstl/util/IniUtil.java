package com.xlk.paperlesstl.util;

import com.blankj.utilcode.util.LogUtils;
import com.xlk.paperlesstl.model.Constant;

import org.ini4j.Config;
import org.ini4j.Ini;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;


/**
 * @author Created by xlk on 2020/12/11.
 * @desc
 */
public class IniUtil {

    private static IniUtil instance;
    private final Ini ini = new Ini();
    private File file;
    private File iniFile = new File(Constant.ROOT_DIR + "client.ini");

    private IniUtil() {
        Config config = new Config();
        //不允许出现重复的部分和选项
        config.setMultiSection(false);
        config.setMultiOption(false);
        ini.setConfig(config);
    }

    public static IniUtil getInstance() {
        if (instance == null) {
            instance = new IniUtil();
        }
        return instance;
    }

    public boolean loadFile() {
        if (file != null) return true;
        try {
            ini.load(new FileReader(iniFile));
            this.file = iniFile;
            return true;
        } catch (IOException e) {
            LogUtils.e("IniUtil","loadFile异常："+e);
            e.printStackTrace();
        }
        return false;
    }

    public String get(String sectionName, String optionName) {
        if (file == null) {
            throw new NullPointerException("load file 还未成功！");
        }
        String s = ini.get(sectionName, optionName);
        if (s == null) {
            s = "";
        }
        return s;
    }

    public void put(String sectionName, String optionName, Object value) {
        if (file != null) {
            ini.put(sectionName, optionName, value);
        }
    }

    public void store() {
        if (file != null) {
            try {
                ini.store(file);
            } catch (IOException e) {
                LogUtils.e("IniUtil","store方法提交到ini文件中失败");
                e.printStackTrace();
            }
        }
    }
}
