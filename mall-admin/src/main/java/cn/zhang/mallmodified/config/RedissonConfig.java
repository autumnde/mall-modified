package cn.zhang.mallmodified.config;

import io.lettuce.core.RedisClient;
import org.redisson.Redisson;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author autum
 */
@Configuration
public class RedissonConfig {
    @Autowired
    Config config;

    @Bean
    public Config getRedissonConfig(){
        Config config = new Config();
        config.useSingleServer().setAddress("redis://140.143.149.101:6379")
                                .setPassword("5PsTDdu3gpMclcDVxFuQ");
        return config;
    }

    @Bean
    public RedissonClient getRedissonClient(){
        return Redisson.create(config);
    }

    @Bean
    public RLock getRLock(){
        return getRedissonClient().getLock("lock1");
    }
}
