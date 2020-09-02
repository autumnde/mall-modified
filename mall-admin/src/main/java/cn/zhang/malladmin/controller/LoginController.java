package cn.zhang.malladmin.controller;

import cn.zhang.malladmin.service.IUmsMemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/login")
public class LoginController {
    @Autowired
    private IUmsMemberService umsMemberService;

    @GetMapping("/getAuthCode")
    public String loginByPhone(String phone){
        return umsMemberService.generateAuthCode(phone);
    }
}
