package cn.zhang.mallmodified.controller.portal;

import cn.zhang.mallmodified.common.api.ServerResponse;
import cn.zhang.mallmodified.po.User;
import cn.zhang.mallmodified.service.IRedisService;
import cn.zhang.mallmodified.service.IUserService;
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

    @PostMapping("login.do")
    public ServerResponse login(String username, String password){
        ServerResponse serverResponse = userService.login(username,password);
        return serverResponse;
    }

    @PostMapping("register.do")
    public ServerResponse register(User user){
        return userService.register(user);
    }

    @PostMapping("get_user_info.do")
    public ServerResponse getUserInfo(Principal principal){
        if(principal == null){
            return ServerResponse.createByError();
        }
        return userService.getCurrentUser();
    }

    @PostMapping("forget_get_question.do")
    public ServerResponse forgetGetQuestion(String username){
        return userService.getQuestionByUsername(username);
    }

    @PostMapping("forget_check_answer.do")
    public ServerResponse forgetCheckQuestion(String username,String question,String answer){
        return userService.checkAnswer(username,question,answer);
    }

    @PostMapping("forget_reset_password.do")
    public ServerResponse forgetResetPassword(String username,String password,String token){
        return userService.forgetResetPassword(username,password,token);
    }

    @PostMapping("reset_password.do")
    public ServerResponse resetPassword(Principal principal,String passwordNew,String passwordOld){
        if(principal == null){
            return ServerResponse.createByErrorMessage("用户未登陆");
        }
        return userService.resetPassword(principal.getName(),passwordNew,passwordOld);
    }

    @PostMapping("update_information.do")
    public ServerResponse updateInformation(User user,Principal principal){
        if(principal == null){
            return ServerResponse.createByErrorMessage("用户未登陆");
        }
       ServerResponse serverResponse = userService.updateInformation(user);
       return serverResponse;
    }
}
