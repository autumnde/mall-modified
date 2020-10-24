package cn.zhang.mallmodified.controller.portal;

import cn.zhang.mallmodified.common.api.ServerResponse;
import cn.zhang.mallmodified.dto.UserRegisterDto;
import cn.zhang.mallmodified.dto.UserUpdateDto;
import cn.zhang.mallmodified.po.User;
import cn.zhang.mallmodified.security.AdminUserDetails;
import cn.zhang.mallmodified.service.IRedisService;
import cn.zhang.mallmodified.service.IUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.security.Principal;

/**
 * @author autum
 */
@Api(tags = "用户API")
@RestController
@Slf4j
@Validated
@RequestMapping("/user/")
public class UserController {
    @Autowired
    private IUserService userService;

    @ApiOperation("登陆")
    @PostMapping("login")
    public ServerResponse login(@ApiParam(value = "用户登陆姓名")
                                @NotBlank(message = "用户名不能为空") String username,
                                @ApiParam(value = "用户登录密码")
                                @NotBlank(message = "密码不能为空") String password){
        log.info("进入login");
        ServerResponse serverResponse = userService.login(username,password);
        return serverResponse;
    }

    @ApiOperation("注册")
    @PostMapping("register")
    public ServerResponse register(@ApiParam("注册用户详细信息")
                                   @Valid UserRegisterDto user){
        log.info("进入register方法");
        ServerResponse serverResponse = userService.register(user);
        return serverResponse;
    }

    @ApiOperation("获取用户信息")
    @PostMapping("get_user_info")
    public ServerResponse getUserInfo(Principal principal){
        if(principal == null){
            return ServerResponse.createByError();
        }
        return userService.getCurrentUser();
    }

    @ApiOperation("获得安全问题")
    @PostMapping("forget_get_question")
    public ServerResponse forgetGetQuestion(@ApiParam(value = "用户姓名",required = true)String username){
        return userService.getQuestionByUsername(username);
    }

    @ApiOperation("检验安全问题回答是否正确，正确时会向邮箱发送验证码，重置密码时可用该验证码")
    @PostMapping("forget_check_answer")
    public ServerResponse forgetCheckQuestion(@ApiParam(value = "用户姓名",required = true)@NotBlank(message = "用户名不能为空")String username,
                                              @ApiParam(value = "用户安全问题",required = true)String question,
                                              @ApiParam(value = "用户安全问题回答",required = true)String answer){
        return userService.checkAnswer(username,question,answer);
    }

    @ApiOperation("重置密码，需要输入邮箱验证码")
    @PostMapping("forget_reset_password")
    public ServerResponse forgetResetPassword(@ApiParam(value = "重置密码用户姓名",required = true)String username,
                                              @ApiParam(value = "重置密码用户新密码",required = true)String password,
                                              @ApiParam(value = "邮箱验证码",required = true)String token){
        return userService.forgetResetPassword(username,password,token);
    }

    @ApiOperation("登陆状态下重置密码，必须要填旧密码信息")
    @PostMapping("reset_password")
    public ServerResponse resetPassword(Principal principal,
                                        @ApiParam(value = "新密码",required = true)String passwordNew,
                                        @ApiParam(value = "旧密码",required = true)String passwordOld){
        if(principal == null){
            return ServerResponse.createByErrorMessage("用户未登陆");
        }
        return userService.resetPassword(principal.getName(),passwordNew,passwordOld);
    }

    @ApiOperation("更新用户信息")
    @PostMapping("update_information")
    public ServerResponse updateInformation(@ApiParam(value = "需要更改的用户信息")@Valid UserUpdateDto userUpdateDto,
                                            Principal principal){
        if(principal == null){
            return ServerResponse.createByErrorMessage("用户未登陆");
        }
       ServerResponse serverResponse = userService.updateInformation(principal,userUpdateDto);
       return serverResponse;
    }

    @ApiOperation("更新安全问题")
    @PostMapping("update_safety_question")
    public ServerResponse updateSafetyQuestion(Principal principal,
                                               @ApiParam(value = "旧安全问题",required = true)@NotBlank(message = "必须回答原先的安全问题") String oldAnswer,
                                               @ApiParam(value = "新安全问题",required = true)@NotBlank(message = "新的安全问题不能为空") String newQuestion,
                                               @ApiParam(value = "新安全问题答案",required = true)@NotBlank(message = "新的安全问题答案不能为空") String newAnswer){
        if(principal == null){
            return ServerResponse.createByErrorMessage("用户未登陆");
        }
        Authentication auth = (Authentication) principal;
        AdminUserDetails adminUserDetails = (AdminUserDetails)auth.getPrincipal();
        User user = adminUserDetails.getUser();
        return userService.updateSafetyQuestion(user,user.getQuestion(),oldAnswer,newQuestion,newAnswer);
    }
}
