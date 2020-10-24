package cn.zhang.mallmodified.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * @author autum
 */
@Data
@ApiModel(value = "更新收货地址所需要的信息")
public class ShippingUpdateDto implements Serializable {
    @ApiModelProperty(value = "收货地址id",required = true)
    @NotBlank(message = "收货地址id不能为空")
    private Integer shippingId;
    @ApiModelProperty(value = "收货姓名")
    private String receiverName;

    @ApiModelProperty(value = "收货固定电话")
    private String receiverPhone;

    @ApiModelProperty(value = "收货移动电话")
    private String receiverMobile;

    @ApiModelProperty(value = "省份")
    private String receiverProvince;

    @ApiModelProperty(value = "城市")
    private String receiverCity;

    @ApiModelProperty(value = "区/县")
    private String receiverDistrict;

    @ApiModelProperty(value = "详细地址")
    private String receiverAddress;

    @ApiModelProperty(value = "邮编")
    private String receiverZip;
}
