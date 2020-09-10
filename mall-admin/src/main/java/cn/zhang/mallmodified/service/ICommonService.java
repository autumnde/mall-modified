package cn.zhang.mallmodified.service;

import cn.zhang.mallmodified.common.api.ServerResponse;

import javax.servlet.http.HttpSession;

public interface ICommonService {
    /**
     * 判断是否是管理员（未登录直接驳回）
     * @param httpSession
     * @return
     */
    public ServerResponse AdminJudge(HttpSession httpSession);
}
