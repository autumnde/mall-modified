package cn.zhang.mallmodified.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author autum
 */
@Data
public class OrderProductVo {
    private List<OrderItemVo> orderItemVoList;
    private BigDecimal productTotalPrice;
    private String imageHost;
}
