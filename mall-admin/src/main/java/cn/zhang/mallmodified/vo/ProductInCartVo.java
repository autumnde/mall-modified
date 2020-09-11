package cn.zhang.mallmodified.vo;

import cn.zhang.mallmodified.po.Product;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 购物车内显示的商品信息
 * @author autum
 */
@Data
public class ProductInCartVo {
    private String productName;
    private String subTitle;
    private String mainImage;
    private String subImages;
    private BigDecimal price;
    /**
     * 购物车中此商品的数量
     */
    private Integer quantity;
    /**
     * 标记购物车内该商品是否被勾选
     */
    private Integer isChecked;

    public ProductInCartVo(Product product,Integer isChecked,Integer quantity){
        this.setMainImage(product.getMainImage());
        this.setPrice(product.getPrice());
        this.setProductName(product.getName());
        this.setSubTitle(product.getSubtitle());
        this.setSubImages(product.getSubImages());
        this.setIsChecked(isChecked);
        this.setQuantity(quantity);
    }
}
