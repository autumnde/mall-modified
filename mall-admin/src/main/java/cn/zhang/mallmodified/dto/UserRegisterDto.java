package cn.zhang.mallmodified.dto;

import com.sun.istack.NotNull;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * @author autum
 */
@Data
@ApiModel(value = "用户注册对象")
public class UserRegisterDto implements Serializable {
    @ApiModelProperty(value = "用户姓名",required = true)
    @NotBlank(message = "用户名不能为空")
    @Length(min = 1,message = "最短长度为1")
    private String username;
    @ApiModelProperty(value = "用户密码",required = true)
    @NotBlank(message = "密码不能为空")
    @Length(min = 16,max = 32,message = "密码长度必须位于16到32")
    private String password;
    @ApiModelProperty(value = "邮箱")
    @Email(message = "不符合邮箱格式")
    private String email;
    @ApiModelProperty(value = "电话号码")
    private String phone;
    @ApiModelProperty(value = "安全问题")
    private String question;
    @ApiModelProperty(value = "安全问题答案")
    private String answer;

    private static final long serialVersionUID = 1L;
}
