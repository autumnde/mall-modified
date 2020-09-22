package cn.zhang.mallmodified.vo;

import cn.zhang.mallmodified.common.api.Const;
import cn.zhang.mallmodified.po.Product;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author autum
 */
@Data
public class CartProductVo {

    private Integer id;
    private Integer userId;
    private Integer productId;
    private Integer quantity;
    private String productName;
    private String productSubtitle;
    private String productMainImage;
    private BigDecimal productPrice;
    private Integer productStatus;
    private BigDecimal productTotalPrice;
    private Integer productStock;
    private Integer productChecked;

    private String limitQuantity;
}
