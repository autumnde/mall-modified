package cn.zhang.mallmodified.controller.portal;

import cn.zhang.mallmodified.common.api.ServerResponse;
import cn.zhang.mallmodified.po.User;
import cn.zhang.mallmodified.service.IRedisService;
import cn.zhang.mallmodified.service.IUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.security.Principal;

/**
 * @author autum
 */
@Api(tags = "用户API")
@RestController
@RequestMapping("/user/")
@Slf4j
public class UserController {
    @Autowired
    private IUserService userService;
    @Autowired
    private IRedisService redisService;
    @Value("${jwt.tokenHeader}")
    private String jwtHeader;

    @ApiOperation("登陆")
    @PostMapping("login.do")
    public ServerResponse login(@ApiParam("用户登陆信息")String username, @ApiParam("用户登录密码")String password){
        ServerResponse serverResponse = userService.login(username,password);
        return serverResponse;
    }

    @ApiOperation("注册")
    @PostMapping("register.do")
    public ServerResponse register(@ApiParam("注册用户详细信息")User user){
        return userService.register(user);
    }

    @ApiOperation("获取用户信息")
    @PostMapping("get_user_info.do")
    public ServerResponse getUserInfo(Principal principal){
        if(principal == null){
            return ServerResponse.createByError();
        }
        return userService.getCurrentUser();
    }

    @ApiOperation("获得安全问题")
    @PostMapping("forget_get_question.do")
    public ServerResponse forgetGetQuestion(@ApiParam("用户姓名")String username){
        return userService.getQuestionByUsername(username);
    }

    @ApiOperation("检验安全问题回答是否正确，正确时会向邮箱发送验证码，重置密码时可用该验证码")
    @PostMapping("forget_check_answer.do")
    public ServerResponse forgetCheckQuestion(@ApiParam("用户姓名")String username,
                                              @ApiParam("用户安全问题")String question,
                                              @ApiParam("用户安全问题回答")String answer){
        return userService.checkAnswer(username,question,answer);
    }

    @ApiOperation("重置密码，需要输入邮箱验证码")
    @PostMapping("forget_reset_password.do")
    public ServerResponse forgetResetPassword(@ApiParam("重置密码用户姓名")String username,
                                              @ApiParam("重置密码用户新密码")String password,
                                              @ApiParam("邮箱验证码")String token){
        return userService.forgetResetPassword(username,password,token);
    }

    @ApiOperation("登陆状态下重置密码，必须要填旧密码信息")
    @PostMapping("reset_password.do")
    public ServerResponse resetPassword(Principal principal,
                                        @ApiParam("新密码")String passwordNew,
                                        @ApiParam("旧密码")String passwordOld){
        if(principal == null){
            return ServerResponse.createByErrorMessage("用户未登陆");
        }
        return userService.resetPassword(principal.getName(),passwordNew,passwordOld);
    }

    @ApiOperation("更新用户信息")
    @PostMapping("update_information.do")
    public ServerResponse updateInformation(@ApiParam("需要更改的用户信息")User user,Principal principal){
        if(principal == null){
            return ServerResponse.createByErrorMessage("用户未登陆");
        }
       ServerResponse serverResponse = userService.updateInformation(user);
       return serverResponse;
    }
}
