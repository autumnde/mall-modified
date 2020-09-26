package cn.zhang.mallmodified.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Email;
import java.io.Serializable;

/**
 * @author autum
 */
@Data
@ApiModel(value = "用户更新信息对象")
public class UserUpdateDto implements Serializable {
    @ApiModelProperty(value = "用户姓名")
    private String username;
    @Email(message = "邮箱格式不正确")
    @ApiModelProperty(value = "邮箱")
    private String email;
    @ApiModelProperty(value = "电话号码")
    private String phone;
    private static final long serialVersionUID = 1L;
}
