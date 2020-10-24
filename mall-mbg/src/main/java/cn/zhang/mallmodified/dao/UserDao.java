package cn.zhang.mallmodified.dao;

import cn.zhang.mallmodified.po.User;
import org.springframework.stereotype.Repository;

@Repository
public interface UserDao {
    int deleteByPrimaryKey(Integer id);

    int insert(User record);

    int insertSelective(User record);

    User selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(User record);

    int updateByPrimaryKey(User record);

    int checkUsername(String username);

    /**
     * 查找某个邮箱名的数量
     * @param email
     * @return
     */
    int checkEmail(String email);

    User selectByUsernamePassword(String username,String password);

    /**
     * 根据用户名获取安全问题
     * @param username
     * @return
     */
    String selectQuestionByUsername(String username);

    /**
     * 查询对应用户名、安全问题、安全答案都匹配的用户的数量
     * @param username
     * @param question
     * @param answer
     * @return
     */
    int checkAnswer(String username,String question,String answer);

    /**
     * 根据用户名查找其邮箱
     * @param username
     * @return
     */
    String selectEmailByUsername(String username);

    /**
     * 根据用户姓名更新其用户密码
     * @param username
     * @param password
     * @return
     */
    int updatePassword(String username,String password);

    /**
     * 查询同时符合姓名和密码的用户个数
     * @param username
     * @param password
     * @return
     */
    int checkPassword(String username,String password);

    /**
     * 查看邮箱地址为email，用户姓名不能为username的用户数量
     * @param username
     * @param email
     * @return
     */
    int checkEmailByUsername(String username,String email);

    /**
     * 查看手机号码为phone，用户姓名不能为username的用户数量
     * @return
     */
    int checkPhoneByUserName(String username,String phone);

    /**
     * 根据用户姓名查询用户
     * @param username
     * @return
     */
    User selectUserByUsername(String username);

    /**
     * 查看某个电话号码是否已存在
     * @param phone
     * @return
     */
    int checkPhone(String phone);
}