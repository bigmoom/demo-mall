package com.cwh.mall.mallmbg;

import org.mybatis.generator.api.MyBatisGenerator;
import org.mybatis.generator.config.Configuration;
import org.mybatis.generator.config.xml.ConfigurationParser;
import org.mybatis.generator.internal.DefaultShellCallback;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * @author cwh
 * @date 2021/7/5 14:25
 */
public class Generator {
    public static void main(String[] args) throws Exception {
        //MBG 执行过程中生成的警告信息
        List<String> warnings = new ArrayList<>();
        //生成代码重复时是否覆盖源代码
        boolean overwrite = true;

        InputStream is = Generator.class.getResourceAsStream("/generatorConfig.xml");
        ConfigurationParser cp = new ConfigurationParser(warnings);
        Configuration config = cp.parseConfiguration(is);
        is.close();

        DefaultShellCallback callback = new DefaultShellCallback(overwrite);
        //生成MBG
        MyBatisGenerator myBatisGenerator = new MyBatisGenerator(config, callback, warnings);
        //执行生成
        myBatisGenerator.generate(null);
        //输出警告信息
        for(String warning : warnings){
            System.out.println(warning);
        }
    }
}
