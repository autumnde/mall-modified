package cn.zhang.mallmodified.service.impl;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import cn.zhang.mallmodified.common.api.Const;
import cn.zhang.mallmodified.common.api.ServerResponse;
import cn.zhang.mallmodified.dao.UserDao;
import cn.zhang.mallmodified.model.User;
import cn.zhang.mallmodified.service.IRedisService;
import cn.zhang.mallmodified.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author autum
 */
@Service
public class UserServiceImpl implements IUserService {
    @Autowired
    private UserDao userDao;
    @Autowired
    private IRedisService redisService;
    @Autowired
    private MailService mailService;

    @Override
    public ServerResponse<User> login(String username, String password) {
        int usernameCount = userDao.checkUsername(username);

        if(usernameCount == 0){
            return ServerResponse.createByErrorMessage("用户名不存在");
        }

        User user = userDao.selectByUsernamePassword(username,password);
        if(user == null){
            return ServerResponse.createByErrorMessage("用户密码错误");
        }
        return ServerResponse.createBySuccess("用户登陆成功",user);
    }

    @Override
    public ServerResponse register(User user) {
        ServerResponse validResponse = checkValid(user.getUsername(),Const.USERNAME);
        if(!validResponse.isSuccess()){
            return validResponse;
        }
        validResponse = checkValid(user.getEmail(),Const.EMAIL);
        if(!validResponse.isSuccess()){
            return validResponse;
        }

        user.setRole(Const.Role.ROLE_CUSTOMER);
        int count = userDao.insert(user);
        if(count == 0){
            return ServerResponse.createByErrorMessage("注册失败");
        }
        return ServerResponse.createBySuccessMessage("注册成功");

    }

    @Override
    public ServerResponse<String> getQuestionByUsername(String username) {
        int count = userDao.checkUsername(username);
        if(count == 0){
            return ServerResponse.createByErrorMessage("用户名不存在");
        }
        return ServerResponse.createBySuccess(userDao.selectQuestionByUsername(username));
    }

    @Override
    public ServerResponse checkAnswer(String username,String question,String answer) {
        int count = userDao.checkAnswer(username,question,answer);
        if(count == 0){
            return ServerResponse.createByErrorMessage("验证问题错误");
        }
        //验证成功，生成邮箱验证码
        String token = RandomUtil.randomString(6);
        String emaliAddress = userDao.selectEmailByUsername(username);
        redisService.set(MailService.MAIL_PREFIX+emaliAddress,token);
        mailService.sendSimpleTextMail(emaliAddress,"验证码",token);
        return ServerResponse.createBySuccessMessage("验证成功");
    }

    @Override
    public ServerResponse forgetResetPassword(String username, String passwordNew, String token) {
        String emaliAddress = userDao.selectEmailByUsername(username);
        String forgetToken = redisService.get(MailService.MAIL_PREFIX+emaliAddress);
        if(!StrUtil.equals(token,forgetToken)){
            return ServerResponse.createByErrorMessage("验证码已过期");
        }
        int count = userDao.updatePassword(username,passwordNew);
        if(count > 0){
            return ServerResponse.createBySuccessMessage("密码修改成功");
        }
        return ServerResponse.createByErrorMessage("密码修改失败");

    }

    @Override
    public ServerResponse resetPassword(String username, String passwordNew,String passwordOld) {
        int count = userDao.checkUsername(username);
        if(count == 0){
            return ServerResponse.createByErrorMessage("用户名不存在");
        }
        count = userDao.checkPassword(username,passwordOld);
        if(count == 0){
            return ServerResponse.createByErrorMessage("用户旧密码错误");
        }
        count = userDao.updatePassword(username,passwordNew);
        if(count == 0){
            return ServerResponse.createByErrorMessage("密码更新失败");
        }
        return ServerResponse.createBySuccessMessage("密码更新成功");
    }

    @Override
    public ServerResponse<User> updateInformation(User user) {
        int resultCount = userDao.checkEmailByUsername(user.getUsername(),user.getEmail());
        if(resultCount > 0){
            return ServerResponse.createByErrorMessage("email已存在,请更换email再尝试更新");
        }
        User updateUser = new User();
        updateUser.setId(user.getId());
        updateUser.setPhone(user.getPhone());
        updateUser.setEmail(user.getEmail());
        updateUser.setQuestion(user.getQuestion());
        updateUser.setAnswer(user.getAnswer());

        int count = userDao.updateByPrimaryKeySelective(updateUser);
        if(count == 0){
            return ServerResponse.createByErrorMessage("用户信息更新失败");
        }
        return ServerResponse.createBySuccess("用户信息更新成功",updateUser);
    }

    public ServerResponse<String> checkValid(String value,String type){
        if(StrUtil.equals(type, Const.USERNAME,true)){
            if(userDao.checkUsername(value) > 0){
                return ServerResponse.createByErrorMessage("用户名重复");
            }
            return ServerResponse.createBySuccessMessage("校验成功");
        }
        else if(StrUtil.equals(type, Const.EMAIL,true)){
            if(userDao.checkEmail(value) > 0){
                return ServerResponse.createByErrorMessage("邮箱已存在");
            }
            return ServerResponse.createBySuccessMessage("校验成功");
        }
        else{
            return ServerResponse.createByErrorMessage("参数类型错误");
        }
    }
}
