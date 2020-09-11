package cn.zhang.mallmodified.service.impl;

import cn.zhang.mallmodified.common.api.ServerResponse;
import cn.zhang.mallmodified.po.User;
import cn.zhang.mallmodified.service.ICommonService;
import cn.zhang.mallmodified.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
@Service
public class CommonServiceImpl implements ICommonService {
    @Autowired
    private IUserService userService;

    @Override
    public ServerResponse AdminJudge(HttpSession httpSession) {
        User user = (User)httpSession.getAttribute("currentUser");
        if(user == null){
            return ServerResponse.createByErrorMessage("用户未登陆");
        }
        if(!userService.checkAdminRole(user).isSuccess()){
            return ServerResponse.createByErrorMessage("无管理员权限");
        }
        return ServerResponse.createBySuccess();
    }
}
