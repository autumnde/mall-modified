package cn.zhang.malladmin.service;



public interface IUmsMemberService {
    /**
     * 生成验证码
     */
    String generateAuthCode(String telephone);

    /**
     * 判断验证码和手机号码是否匹配
     */
    String verifyAuthCode(String telephone, String authCode);
}
