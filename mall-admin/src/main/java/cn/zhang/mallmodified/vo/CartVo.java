package cn.zhang.mallmodified.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author autum
 */
@Data
public class CartVo {

    private List<CartProductVo> cartProductVoList;
    private BigDecimal cartTotalPrice;
    private Boolean allChecked;
    private String imageHost;

}
