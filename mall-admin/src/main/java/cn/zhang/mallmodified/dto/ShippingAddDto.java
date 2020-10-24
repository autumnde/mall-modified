package cn.zhang.mallmodified.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.util.Date;

/**
 * @author autum
 */
@ApiModel(value = "添加收货地址所需要的信息")
@Data
public class ShippingAddDto implements Serializable {
    @ApiModelProperty(value = "收货人姓名",required = true)
    @NotBlank(message = "收货人姓名不能为空")
    private String receiverName;

    @ApiModelProperty(value = "收货人固定电话")
    private String receiverPhone;

    @ApiModelProperty(value = "收货人移动电话",required = true)
    @NotBlank(message = "收货人移动电话不能为空")
    private String receiverMobile;

    @ApiModelProperty(value = "收货省份",required = true)
    @NotBlank(message = "收货省份不能为空")
    private String receiverProvince;

    @ApiModelProperty(value = "收货城市",required = true)
    @NotBlank(message = "收货城市不能为空")
    private String receiverCity;

    @ApiModelProperty(value = "收货区/县",required = true)
    @NotBlank(message = "收货区/县不能为空")
    private String receiverDistrict;

    @ApiModelProperty(value = "详细地址",required = true)
    @NotBlank(message = "详细地址不能为空")
    private String receiverAddress;

    @ApiModelProperty(value = "邮编",required = true)
    @NotBlank(message = "邮编不能为空")
    private String receiverZip;

    private static final long serialVersionUID = 1L;
}
