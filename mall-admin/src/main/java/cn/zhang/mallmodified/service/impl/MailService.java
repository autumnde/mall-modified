package cn.zhang.mallmodified.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

/**
 * @author autum
 */
@Service
public class MailService{
    private Logger logger = LoggerFactory.getLogger(MailService.class);
    public static String MAIL_PREFIX = "mail_";
    @Value("${spring.mail.username}")
    private String from;
    @Autowired
    private JavaMailSender mailSender;

    public void sendSimpleTextMail(String to,String subject,String content){
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(from);
        message.setTo(to);
        message.setSubject(subject);
        message.setText(content);
        mailSender.send(message);
        logger.info("验证码邮件已经发送");
    }
}
