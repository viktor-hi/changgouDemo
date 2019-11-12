package cn.chen.oauth.service;

import cn.chen.oauth.util.AuthToken;

/**
 * @author haixin
 * @time 2019-11-10
 */
public interface LoginService {
    /**
     * 授权认证方法
     * @param username 用户名
     * @param password 密码
     * @param clientId 客户端id(密钥的别名)
     * @param clientSecret 客户端密钥(访问密码)
     * @return
     */
    AuthToken login(String username, String password, String clientId, String clientSecret);
}
