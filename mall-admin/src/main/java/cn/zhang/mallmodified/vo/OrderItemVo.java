package cn.zhang.mallmodified.vo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * Created by geely
 */
@Data
public class OrderItemVo {

    private Long orderNo;

    private Integer productId;

    private String productName;
    private String productImage;

    private BigDecimal currentUnitPrice;

    private Integer quantity;

    private BigDecimal totalPrice;

    private String createTime;

}
