package cn.zhang.malladmin.service.impl;

import cn.zhang.malladmin.service.IRedisService;
import cn.zhang.malladmin.service.IUmsMemberService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

import java.util.Random;

/**
 * @author autum
 */
@Service
@PropertySource(value = "classpath:application.yml",ignoreResourceNotFound = true)
public class UmsMemberServiceImpl implements IUmsMemberService {
    @Autowired
    private IRedisService redisService;

    @Value("${redis.key.prefix.authCode}")
    private String REDIS_KEY_PREFXI_AUTH_CODE;

    private int AUTH_CODE_EXPIRE_SECONDS = 120;


    @Override
    public String generateAuthCode(String telephone) {
        StringBuilder sb = new StringBuilder();
        Random random = new Random();
        for(int i=0;i<6;i++){
            sb.append(random.nextInt(10));
        }
        redisService.set(REDIS_KEY_PREFXI_AUTH_CODE + telephone,sb.toString());
        redisService.expire(REDIS_KEY_PREFXI_AUTH_CODE + telephone,AUTH_CODE_EXPIRE_SECONDS);
        return sb.toString();
    }

    @Override
    public String verifyAuthCode(String telephone, String authCode) {
        return null;
    }
}
