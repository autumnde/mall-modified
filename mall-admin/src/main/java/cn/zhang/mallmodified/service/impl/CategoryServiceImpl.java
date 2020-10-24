package cn.zhang.mallmodified.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import cn.zhang.mallmodified.common.api.ServerResponse;
import cn.zhang.mallmodified.dao.CategoryDao;
import cn.zhang.mallmodified.po.Category;
import cn.zhang.mallmodified.service.ICategoryService;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;


/**
 * @author autum
 */
@Service
public class CategoryServiceImpl implements ICategoryService {
    private Logger logger = LoggerFactory.getLogger(CategoryServiceImpl.class);
    @Autowired
    private CategoryDao categoryDao;

    @Override
    public ServerResponse addCategory(String categoryName, Integer parentId) {
        if(StrUtil.isBlank(categoryName) || parentId == null){
            return ServerResponse.createByErrorMessage("传入参数错误");
        }
        Category category = new Category();
        category.setName(categoryName);
        category.setParentId(parentId);
        category.setStatus(true);

        int influence = categoryDao.insert(category);
        if(influence == 0){
            return ServerResponse.createByErrorMessage("添加品类错误");
        }
        return ServerResponse.createBySuccessMessage("添加品类成功");
    }

    @Override
    public ServerResponse updateCategoryName(Integer categoryId, String categoryName) {
        if(categoryId == null || StrUtil.isBlank(categoryName)){
            return ServerResponse.createByErrorMessage("参数错误");
        }
        Category category = new Category();
        category.setId(categoryId);
        category.setName(categoryName);
        int influence = categoryDao.updateByPrimaryKeySelective(category);
        if(influence == 0){
            return ServerResponse.createByErrorMessage("分类名更新错误");
        }
        return ServerResponse.createBySuccessMessage("分类名更新成功");
    }

    @Override
    public ServerResponse<List<Category>> getChildCategoryOf(Integer parentId) {
        if(parentId == null){
            return ServerResponse.createByErrorMessage("参数错误");
        }
        List<Category> categoryList = categoryDao.selectChildrenCategoryByParentId(parentId);
        if(CollectionUtil.isEmpty(categoryList)){
            logger.info("分类下无子分类");
        }
        return ServerResponse.createBySuccess(categoryList);
    }

    @Override
    public ServerResponse<List<Category>> getAllChildCategoryOf(Integer parentId) {
        if(parentId == null){
            return ServerResponse.createByErrorMessage("参数错误");
        }
        Set<Category> categorySet = CollectionUtil.newHashSet();
        getAllChildCategoryHelper(parentId,categorySet);
        List<Category> categoryList = new ArrayList<>(categorySet);
        return ServerResponse.createBySuccess(categoryList);
    }

    /**
     * 递归查询本节点的id及孩子节点的id
     * @param categoryId
     * @return
     */
    @Override
    public ServerResponse<List<Integer>> selectCategoryAndChildrenById(Integer categoryId){
        Set<Category> categorySet = Sets.newHashSet();
        getAllChildCategoryHelper(categoryId,categorySet);


        List<Integer> categoryIdList = Lists.newArrayList();
        if(categoryId != null){
            for(Category categoryItem : categorySet){
                categoryIdList.add(categoryItem.getId());
            }
        }
        return ServerResponse.createBySuccess(categoryIdList);
    }

    /**
     * 辅助方法，递归查询所有的子节点
     * @param id
     * @param categorySet
     * @return
     */
    private Set<Category> getAllChildCategoryHelper(Integer id,Set<Category> categorySet){
        Category category = categoryDao.selectByPrimaryKey(id);
        if(category != null){
            categorySet.add(category);
        }
        List<Category> categoryList = getChildCategoryOf(id).getData();
        for(Category category1:categoryList){
            getAllChildCategoryHelper(category1.getId(),categorySet);
        }
        return categorySet;
    }
}
