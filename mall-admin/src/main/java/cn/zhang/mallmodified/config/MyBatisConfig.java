package cn.zhang.mallmodified.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

/**
 * Mybatis配置类
 * @author autum
 */
@Configuration
@MapperScan({"cn.zhang.mallmodified.mapper","cn.zhang.mallmodified.dao"})
public class MyBatisConfig {
}
