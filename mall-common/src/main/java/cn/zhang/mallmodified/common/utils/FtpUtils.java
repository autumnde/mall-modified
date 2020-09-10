package cn.zhang.mallmodified.common.utils;

import cn.zhang.mallmodified.common.api.ServerResponse;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

/**
 * @author autum
 */
@Slf4j
@ConfigurationProperties(prefix = "ftp.server")
@Component
@Data
public class FtpUtils {
    private  String host;
    private  String username;
    private  String password;
    private  int port;
    private  String dir;
    private  FTPClient ftpClient;

    /**
     * 建立ftp客户端连接ftp服务器
     * 不提供接口调用
     * 供其他方法使用
     * @return 与ftp服务器建立连接的客户端
     * @throws IOException
     */
    private  void loinFTP(){
        try {
            ftpClient = new FTPClient();
            ftpClient.connect(host, port);
            ftpClient.login(username, password);
            ftpClient.setControlEncoding("UTF-8");
            ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
        }catch (IOException e){
            log.info("创建客户端出错");
            return;
        }

        if (!FTPReply.isPositiveCompletion(ftpClient.getReplyCode())) {
            try {
                ftpClient.disconnect();
            }catch (IOException e){
                log.info("FTP客户端disconnect失败");
            }
            log.info("连接FTP失败，用户名或密码错误");
        } else {
            log.info("FTP连接成功!");
        }
    }

    public  ServerResponse upload(MultipartFile file){
            //创建ftp用户
            loinFTP();
        try {
            ftpClient.changeWorkingDirectory(dir);
            String fileName = file.getOriginalFilename();
            ftpClient.storeFile(fileName,file.getInputStream());
            //登出ftp用户
            ftpClient.logout();
            if(ftpClient.isConnected()){
                ftpClient.disconnect();
            }
            return ServerResponse.createBySuccess();
        }catch (IOException e){
            log.info("上传文件出错");
            return ServerResponse.createByError();
        }
    }
}
