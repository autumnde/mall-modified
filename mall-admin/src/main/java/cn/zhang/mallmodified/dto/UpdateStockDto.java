package cn.zhang.mallmodified.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

/**
 * 方便消息队里传递信息用
 * @author autum
 */
@Data
@AllArgsConstructor
public class UpdateStockDto implements Serializable {
    Integer productId;
    int Stock;
}
