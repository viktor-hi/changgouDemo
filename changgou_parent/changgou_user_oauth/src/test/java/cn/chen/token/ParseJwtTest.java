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
        String token = "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJyb2xlcyI6IlJPTEVfVklQLFJPTEVfVVNFUiIsIm5hbWUiOiJ4aWFvY2hlbiIsImlkIjoiMSJ9.J5S-tj5HtQGOGbZmUHOQ3vBSgVgTy1iUxrH6MXkmEyBKJ92YAkcw10kzwPcuKUILA3ECdW8oEM_Cd7Q_7UWmdnkH8kbmOC2W02l662KAR5qY9m-OFVFIy3yMMbjyxOUyD1SLGJs8RTmV6gj-cAsf7SEHpZ2Vp872cPISS6JExxTrAGTRD-u7C3J8dyHpxfc1_RArYMegIYeQnAhzTIkELZSrhLvkjQv3-ySa4CilZQB2b9DIhOG5OtjnMo2KSQb63ml4e6eIuzsnp71P4p5ZDVXv9hrxE8hDGeU4yfDXZjiKNdJhul4fOUwOs_Jw3f6dcHL-2wKCZsSoQS0W1Kd6sw";

        //公钥
        String publickey = "-----BEGIN PUBLIC KEY-----MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAmdzUXtUXVepduanKGlDlmZfZv2ZGfjrRI1XEEhcRmi2lRkEKZSfzuGT7VuWDdhw84Zr9HJ9osV0chN6aqmAtk9H7hDELFNb5scAk3qMfoZ7r8TvJe2XwhCt0yR5jy/Ri0Kw+5XNiaX4Ng8oUk3sEZBBtfY3IaxpC669YtmLrtdONddBeESFJANoRsfpoPpFP0FzhHepVM9tOFHj+eLpn79m2TzwYUgOCG7aLf6leNNFx3LmeCz9hCqvGFteySzGVazI/5rCC58CgPi1BISxhlZFIev7HcenUIsD2LuiUx/lPwFX4mMErm/B7+/MRMsKErVMJKRYMVENDg3bMf/uSRwIDAQAB-----END PUBLIC KEY-----";

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
