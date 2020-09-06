package cn.zhang.mallmodified.controller.portal;

import cn.zhang.mallmodified.common.api.ServerResponse;
import cn.zhang.mallmodified.model.User;
import cn.zhang.mallmodified.service.IRedisService;
import cn.zhang.mallmodified.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;

/**
 * @author autum
 */
@RestController
@RequestMapping("/user/")
public class UserController {
    @Autowired
    private IUserService userService;
    @Autowired
    private IRedisService redisService;

    @PostMapping("login.do")
    public ServerResponse<User> login(String username, String password, HttpSession httpSession){
        ServerResponse response = userService.login(username,password);
        if(response.isSuccess()){
            httpSession.setAttribute("currentUser", response.getData());
        }
        return response;
    }

    @RequestMapping("logout.do")
    public ServerResponse<String> logout(HttpSession httpSession){
        httpSession.removeAttribute("currentUser");
        return ServerResponse.createBySuccess();
    }

    @PostMapping("register.do")
    public ServerResponse register(User user){
        return userService.register(user);
    }

    @RequestMapping("getUserInfo.do")
    public ServerResponse getUserInfo(HttpSession httpSession){
        User user = (User)httpSession.getAttribute("currentUser");
        if(user != null){
            return ServerResponse.createBySuccess(user);
        }
        return ServerResponse.createByErrorMessage("用户未登陆");
    }

    @RequestMapping("forgetGetQuestion.do")
    public ServerResponse forgetGetQuestion(String username){
        return userService.getQuestionByUsername(username);
    }

    @RequestMapping("forgetCheckQuestion.do")
    public ServerResponse forgetCheckQuestion(String username,String question,String answer){
        return userService.checkAnswer(username,question,answer);
    }

    @PostMapping("forgetResetPassword.do")
    public ServerResponse forgetResetPassword(String username,String password,String token){
        return userService.forgetResetPassword(username,password,token);
    }

    @PostMapping("resetPassword.do")
    public ServerResponse resetPassword(HttpSession httpSession,String passwordNew,String passwordOld){
        User user = (User)httpSession.getAttribute("currentUser");
        if(user == null){
            return ServerResponse.createByErrorMessage("用户未登陆");
        }
        return userService.resetPassword(user.getUsername(),passwordNew,passwordOld);
    }

    @PostMapping("updateInformation.do")
    public ServerResponse updateInformation(User user,HttpSession httpSession){
        User userCur = (User)httpSession.getAttribute("currentUser");
        if(userCur == null){
            return ServerResponse.createByErrorMessage("用户未登陆");
        }
        user.setUsername(userCur.getUsername());
        user.setId(userCur.getId());
       ServerResponse serverResponse = userService.updateInformation(user);
       if(serverResponse.isSuccess()){
           httpSession.setAttribute("currentUser",serverResponse.getData());
       }
       return serverResponse;
    }
}
