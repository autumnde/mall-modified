package cn.zhang.mallmodified.common.api;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import org.apache.ibatis.annotations.Param;


import java.io.Serializable;

/**
 * @author autum
 */
@Getter
public class ServerResponse<T> implements Serializable {
    //保证序列化json的时候,如果是null的对象,key也会消失

    @JsonInclude(value = JsonInclude.Include.NON_NULL)
    private long status;
    @JsonInclude(value = JsonInclude.Include.NON_NULL)
    private String msg;
    @JsonInclude(value = JsonInclude.Include.NON_NULL)
    private T data;
    @JsonInclude(value = JsonInclude.Include.NON_NULL)
    private String token;

    private ServerResponse(int status){
        this.status = status;
    }
    private ServerResponse(int status, T data){
        this.status = status;
        this.data = data;
    }
    private ServerResponse(int status, String msg, T data){
        this.status = status;
        this.msg = msg;
        this.data = data;
    }
    private ServerResponse(int status, String msg){
        this.status = status;
        this.msg = msg;
    }
    private ServerResponse(int status, String token, T data,String msg){
        this.token = token;
        this.status = status;
        this.data = data;
        this.msg = msg;
    }

    @JsonIgnore
    public boolean isSuccess(){
        return this.status == ResponseCode.SUCCESS.getCode();
    }


    public static <T> ServerResponse<T> createBySuccess(){
        return new ServerResponse<T>(ResponseCode.SUCCESS.getCode());
    }

    public static <T> ServerResponse<T> createBySuccessMessage(String msg){
        return new ServerResponse<T>(ResponseCode.SUCCESS.getCode(),msg);
    }

    public static <T> ServerResponse<T> createBySuccess(T data){
        return new ServerResponse<T>(ResponseCode.SUCCESS.getCode(),data);
    }

    public static <T> ServerResponse<T> createBySuccess(String msg, T data){
        return new ServerResponse<T>(ResponseCode.SUCCESS.getCode(),msg,data);
    }


    public static <T> ServerResponse<T> createByError(){
        return new ServerResponse<T>(ResponseCode.ERROR.getCode(),ResponseCode.ERROR.getMessage());
    }

    public static <T> ServerResponse<T> createByErrorMessage(String errorMessage){
        return new ServerResponse<T>(ResponseCode.ERROR.getCode(),errorMessage);
    }

    public static <T> ServerResponse<T> createByErrorCodeMessage(int errorCode, String errorMessage){
        return new ServerResponse<T>(errorCode,errorMessage);
    }

    public static <T> ServerResponse<T> createByTokenMessage(String token,T data,String msg){
        return new ServerResponse<T>(ResponseCode.SUCCESS.getCode(),token,data,msg);
    }













}
