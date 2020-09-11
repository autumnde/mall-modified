package cn.zhang.mallmodified.vo;

import lombok.Data;

import java.util.List;

/**
 * 传送给前端的关于购物车的信息
 * @author autum
 */
@Data
public class CartVo {
    private Integer userId;
    private List<ProductInCartVo> productVoList;
    /**
     * 一键全选！清空购物车！
     */
    private boolean allSelected;
    private String imageHost;
    private double totalPrice;

}
