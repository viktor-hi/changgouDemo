package cn.chen.token;

import org.junit.Test;
import org.springframework.security.jwt.Jwt;
import org.springframework.security.jwt.JwtHelper;
import org.springframework.security.jwt.crypto.sign.RsaVerifier;

/**
 * @author haixin
 * @time 2019-11-09
 */
public class ParseJwtTest {

    /***
     * 校验令牌
     */
    @Test
    public void testParseToken(){
        //令牌
        String token = "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJyb2xlcyI6IlJPTEVfVklQLFJPTEVfVVNFUiIsIm5hbWUiOiJ4aWFvY2hlbiIsImlkIjoiMSJ9.jqfg20Wfq-kp8TdRaFjFbogyHKb6PoFZ_Aa9va63TbVeX_b0FcKq6G_8EYz1BsU2ejMPj6qn6bA-i5HB_rZy09sZsRt4rqQdEeHa5_v7Sv2vZe5aXcmOtqBP1bBYEtEujaTooXvUeVjNOKzDNwow8F0bOE4BE-D5gYVP-6PPvwIAyV9zJg5psIUxAgMy6dfeUi8VU0_zJPG3UChW9OIzvwOfeEXt3-raRGoAO8yebGFSdP-lO8vVxXHdNYvcRyJB3BB8IMyPgKcs4_hNHbsuLBxqOqbiUW6reB8B8sPoX4m7WzC_pbDllylB5myjARpGyOVCBg6ZAEYEAuVFuk8wzA";

        //公钥
        String publickey = "-----BEGIN PUBLIC KEY-----MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAvFsEiaLvij9C1Mz+oyAmt47whAaRkRu/8kePM+X8760UGU0RMwGti6Z9y3LQ0RvK6I0brXmbGB/RsN38PVnhcP8ZfxGUH26kX0RK+tlrxcrG+HkPYOH4XPAL8Q1lu1n9x3tLcIPxq8ZZtuIyKYEmoLKyMsvTviG5flTpDprT25unWgE4md1kthRWXOnfWHATVY7Y/r4obiOL1mS5bEa/iNKotQNnvIAKtjBM4RlIDWMa6dmz+lHtLtqDD2LF1qwoiSIHI75LQZ/CNYaHCfZSxtOydpNKq8eb1/PGiLNolD4La2zf0/1dlcr5mkesV570NxRmU1tFm8Zd3MZlZmyv9QIDAQAB-----END PUBLIC KEY-----";

        //校验Jwt
        Jwt jwt = JwtHelper.decodeAndVerify(token, new RsaVerifier(publickey));

        //获取Jwt原始内容
        String claims = jwt.getClaims();
        System.out.println(claims);
        //jwt令牌
        String encoded = jwt.getEncoded();
        System.out.println(encoded);
    }
}
