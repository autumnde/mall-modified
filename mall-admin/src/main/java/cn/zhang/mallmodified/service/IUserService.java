package cn.zhang.mallmodified.service;

import cn.zhang.mallmodified.common.api.ServerResponse;
import cn.zhang.mallmodified.po.User;
import cn.zhang.mallmodified.vo.OrderVo;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * @author autum
 */
public interface IUserService {
    /**
     * 用户登陆
     * @param username 用户名
     * @param password 用户密码
     * @return 登陆结果
     */
    ServerResponse<User> login(String username, String password);

    /**
     * 用户注册
     * @param user 注册用户信息
     * @return 注册结果
     */
    ServerResponse<String> register(User user);

    /**
     * 根据邮箱获取安全问题
     * @param username 用户名
     * @return 安全问题
     */
    ServerResponse<String> getQuestionByUsername(String username);

    /**
     * 检查验证问题是否回答正确，回答正确则会发送邮箱验证码
     * @param username
     * @param question
     * @param answer
     * @return
     */
    ServerResponse checkAnswer(String username,String question,String answer);

    /**
     * 忘记密码的情况下，重置密码
     * @param username
     * @param passwordNew
     * @param token
     * @return
     */
    ServerResponse forgetResetPassword(String username,String passwordNew,String token);

    /**
     * 登陆状态下更新用户密码
     * @param username
     * @param passwordNew
     * @param passwordOld
     * @return
     */
    ServerResponse resetPassword(String username,String passwordNew,String passwordOld);

    /**
     * 在线状态下更新用户信息
     * @param user
     * @return
     */
    ServerResponse updateInformation(User user);

    /**
     * 检查该角色是否为管理员
     * @param user
     * @return
     */
    ServerResponse checkAdminRole(User user);

    /**
     * 根据用户名获取用户
     * @param username
     * @return
     */
    ServerResponse getUserByUsername(String username);

    ServerResponse getCurrentUser();

    ServerResponse<OrderVo> manageDetail(Long orderNo);
}
