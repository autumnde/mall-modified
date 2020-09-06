package cn.zhang.mallmodified.service;

import cn.zhang.mallmodified.common.api.ServerResponse;

/**
 * @author autum
 */
public interface ICategoryService {
    /**
     * 添加分类
     * @param categoryName
     * @param parentId
     * @return
     */
    ServerResponse addCategory(String categoryName,Integer parentId);

    /**
     * 更新分类姓名
     * @param categoryId
     * @param categoryName
     * @return
     */
    ServerResponse updateCategoryName(Integer categoryId,String categoryName);

    /**
     * 根据分类编号获取其子分类
     * @param parentId
     * @return
     */
    ServerResponse getChildCategoryOf(Integer parentId);

    /**
     * 根据分类编号获取其所有子分类（包括子类的子类）
     * @param parentId
     * @return
     */
    ServerResponse getAllChildCategoryOf(Integer parentId);
}
