package cn.zhang.mallmodified;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;
import springfox.documentation.swagger2.annotations.EnableSwagger2;


/**
 * @author autum
 */
@SpringBootApplication
@EnableSwagger2
public class MallModifiedApplication {

    public static void main(String[] args) {
        SpringApplication.run(MallModifiedApplication.class, args);
    }

}
