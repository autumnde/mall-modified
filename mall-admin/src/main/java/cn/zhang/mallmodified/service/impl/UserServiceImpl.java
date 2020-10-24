package cn.zhang.mallmodified.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.lang.Validator;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.mail.MailUtil;
import cn.zhang.mallmodified.common.api.Const;
import cn.zhang.mallmodified.common.api.ServerResponse;
import cn.zhang.mallmodified.common.utils.JwtTokenUtil;
import cn.zhang.mallmodified.dao.OrderDao;
import cn.zhang.mallmodified.dao.OrderItemDao;
import cn.zhang.mallmodified.dao.ShippingDao;
import cn.zhang.mallmodified.dao.UserDao;
import cn.zhang.mallmodified.dto.UserRegisterDto;
import cn.zhang.mallmodified.dto.UserUpdateDto;
import cn.zhang.mallmodified.po.Order;
import cn.zhang.mallmodified.po.OrderItem;
import cn.zhang.mallmodified.po.Shipping;
import cn.zhang.mallmodified.po.User;
import cn.zhang.mallmodified.security.AdminUserDetails;
import cn.zhang.mallmodified.service.ICommonService;
import cn.zhang.mallmodified.service.IRedisService;
import cn.zhang.mallmodified.service.IUserService;
import cn.zhang.mallmodified.vo.OrderItemVo;
import cn.zhang.mallmodified.vo.OrderVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.List;

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
    @Autowired
    private UserDetailsService userDetailsService;
    @Autowired
    private JwtTokenUtil jwtTokenUtil;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private OrderDao orderDao;
    @Autowired
    private OrderItemDao orderItemDao;
    @Autowired
    private ICommonService commonService;

    @Override
    public ServerResponse login(String username, String password) {
        String token = null;
        int usernameCount = userDao.checkUsername(username);

        if(usernameCount == 0){
            return ServerResponse.createByErrorMessage("用户名不存在");
        }
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        if (!passwordEncoder.matches(password, userDetails.getPassword())) {
            return ServerResponse.createByErrorMessage("用户密码错误");
        }

        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(userDetails,null,userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        token = jwtTokenUtil.generateToken(userDetails);

        SecurityContext context = SecurityContextHolder.getContext();
        Authentication authentication = context.getAuthentication();
        AdminUserDetails adminUserDetails = (AdminUserDetails) authentication.getPrincipal();

        return ServerResponse.createByTokenMessage(token,adminUserDetails.getUser(),"登录成功");
    }

    @Override
    public ServerResponse register(UserRegisterDto userRegisterDto) {
        //检查部分关键参数是否为空及其格式
        ServerResponse serverResponse = checkUserRegisterDto(userRegisterDto);
        if(!serverResponse.isSuccess()){
            return serverResponse;
        }
        //格式转化
        User user = commonService.assembleUser(userRegisterDto);
        //密码加密
        String password = passwordEncoder.encode(user.getPassword());
        user.setPassword(password);

        int count = userDao.insertSelective(user);
        if(count == 0){
            return ServerResponse.createByErrorMessage("注册失败");
        }
        return ServerResponse.createBySuccessMessage("注册成功");

    }

    @Override
    public ServerResponse getQuestionByUsername(String username) {
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
        if(StrUtil.length(passwordOld) > 32){
            return ServerResponse.createByErrorMessage("密码过长");
        }
        else if(StrUtil.length(passwordOld) < 16){
            return ServerResponse.createByErrorMessage("密码过短");
        }
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
    public ServerResponse updateInformation(Principal principal, UserUpdateDto userUpdateDto) {
        Authentication auth = (Authentication) principal;
        AdminUserDetails adminUserDetails = (AdminUserDetails)auth.getPrincipal();
        User userCurrent = adminUserDetails.getUser();
        //判断参数是否合理
        ServerResponse serverResponse = checkUserUpdateDto(userUpdateDto,userCurrent.getId());
        if(!serverResponse.isSuccess()){
            return serverResponse;
        }
        User user = new User();
        user.setId(userCurrent.getId());

        if(userUpdateDto.getEmail() != null){
            user.setEmail(userUpdateDto.getEmail());
        }
        if(userUpdateDto.getUsername() != null){
            user.setUsername(userUpdateDto.getUsername());
        }
        if(userUpdateDto.getPhone() != null){
            user.setPhone(userUpdateDto.getPhone());
        }
        user.setUpdateTime(DateUtil.date());

        if(userDao.insertSelective(user) ==0){
            return ServerResponse.createByErrorMessage("用户信息更新失败");
        }
        return ServerResponse.createBySuccess("用户信息更新成功");
    }

    @Override
    public ServerResponse checkAdminRole(User user) {
        if(user != null && user.getRole() == Const.Role.ROLE_ADMIN){
            return ServerResponse.createBySuccess();
        }
        return ServerResponse.createByError();
    }

    @Override
    public ServerResponse getUserByUsername(String username) {
        User user = userDao.selectUserByUsername(username);
        if(user == null){
            return ServerResponse.createByError();
        }
        return ServerResponse.createBySuccess(user);
    }

    @Override
    public ServerResponse getCurrentUser() {
        SecurityContext context = SecurityContextHolder.getContext();
        Authentication authentication = context.getAuthentication();
        AdminUserDetails adminUserDetails = (AdminUserDetails) authentication.getPrincipal();
        return ServerResponse.createBySuccess(adminUserDetails.getUser());
    }

    @Override
    public ServerResponse manageDetail(Long orderNo){
        Order order = orderDao.selectByOrderNo(orderNo);
        if(order != null){
            List<OrderItem> orderItemList = orderItemDao.selectByOrderNo(orderNo);
            OrderVo orderVo = commonService.assembleOrderVo(order,orderItemList);
            return ServerResponse.createBySuccess(orderVo);
        }
        return ServerResponse.createByErrorMessage("订单不存在");
    }

    @Override
    public ServerResponse updateSafetyQuestion(User user,String oldQuestion,String oldAnswer, String newQuestion, String newAnswer) {
        if(userDao.checkAnswer(user.getUsername(),oldQuestion,oldAnswer) == 0){
            return ServerResponse.createByErrorMessage("安全问题回答错误");
        }
        if(newAnswer == null || newQuestion == null){
            return ServerResponse.createByErrorMessage("安全问题信息不完善");
        }
        User updateUser = new User();
        updateUser.setUpdateTime(DateUtil.date());
        updateUser.setId(user.getId());
        user.setQuestion(newQuestion);
        user.setAnswer(newAnswer);

        if(userDao.insertSelective(user) == 0){
            return ServerResponse.createByErrorMessage("安全问题更新错误");
        }
        return ServerResponse.createBySuccessMessage("安全问题更新成功");

    }

    private ServerResponse checkUserRegisterDto(UserRegisterDto userRegisterDto){
        StringBuilder msg = new StringBuilder();
        //用户名是否重复
        if(userDao.checkUsername(userRegisterDto.getUsername()) > 0){
                msg.append("用户名重复\\r\\n");
        }

        //安全问题和安全问题答案格式判断
        if((userRegisterDto.getAnswer() != null)^(userRegisterDto.getQuestion() != null)){
            msg.append("安全问题和安全问题答案不可只填一个\\r\\n");
        }
        //邮箱是否重复
        if(userRegisterDto.getEmail() != null){
            String email = userRegisterDto.getEmail();
            if(userDao.checkEmail(email) > 0){
                msg.append("邮箱已存在\\r\\n");
            }
        }
        //电话格式验证
        if(userRegisterDto.getPhone() != null){
            String phone = userRegisterDto.getPhone();
            if(!Validator.isMobile(phone)){
                msg.append("电话号码格式错误\\r\\n");
            }
            else if(userDao.checkPhone(phone) > 0){
                return ServerResponse.createByErrorMessage("电话号码已存在\\r\\n");
            }
        }


        if(msg.length() > 0){
            return ServerResponse.createByErrorMessage(msg.toString());
        }
        else {
            return ServerResponse.createBySuccess();
        }
    }

    private ServerResponse checkUserUpdateDto(UserUpdateDto userUpdateDto,Integer updateUserId) {
        User user = userDao.selectByPrimaryKey(updateUserId);
        StringBuilder errorMsg = new StringBuilder();
        //如果更新了用户名，判断是否重复
        if (user.getUsername() != null && user.getUsername() != userUpdateDto.getUsername()) {
            if (userDao.checkUsername(userUpdateDto.getUsername()) > 0) {
                errorMsg.append("用户名重复\\r\\n");
            }
        }
        //检验邮箱是否重复
        if (userUpdateDto.getEmail() != null) {
            if (userDao.checkEmailByUsername(user.getUsername(), userUpdateDto.getEmail()) > 0) {
                errorMsg.append("email已存在,请更换email再尝试更新\\r\\n");
            }

        }
        //检查电话号码
        if (userUpdateDto.getPhone() != null) {
            if(!Validator.isMobile(userUpdateDto.getPhone())){
                errorMsg.append("phone格式错误\\r\\n");
            }
            else if(userDao.checkPhoneByUserName(user.getUsername(), userUpdateDto.getPhone()) > 0) {
                errorMsg.append("phone已存在，请更换phone再尝试更新\\r\\n");
            }
        }


        if (errorMsg.length() > 0) {
            return ServerResponse.createByErrorMessage(errorMsg.toString());
        } else {
            return ServerResponse.createBySuccess();
        }
    }
}
