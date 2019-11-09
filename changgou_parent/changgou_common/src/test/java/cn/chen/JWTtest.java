package cn.chen;

import io.jsonwebtoken.*;
import org.junit.Test;

import java.util.Date;

/**
 * @author haixin
 * @time 2019-11-08
 */
public class JWTtest {
    /**
     * 创建令牌
     */
    @Test
    public void testCreateJwt(){
        //1、创建Jwt构建器-jwtBuilder = Jwts.builder()
        JwtBuilder jwtBuilder = Jwts.builder();
        //2、设置唯一编号-setId
        jwtBuilder.setId("007");
        //3、设置主题，可以是JSON数据-setSubject()
        jwtBuilder.setSubject("测试主题");
        //4、设置签发日期-setIssuedAt
        jwtBuilder.setIssuedAt(new Date());
        //5、设置签发人-setIssuer
        jwtBuilder.setIssuer("www.itheima.com");
        //6、设置签证
        jwtBuilder.signWith(SignatureAlgorithm.HS256, "itheima.steven");
        //7、生成令牌-compact()
        String token = jwtBuilder.compact();
        //8、输出结果
        System.out.println(token);
    }
    /**
     * 解析令牌
     */
    @Test
    public void testParseJwt(){
        String token = "eyJhbGciOiJIUzI1NiJ9.eyJqdGkiOiIwMDciLCJzdWIiOiLmtYvor5XkuLvpopgiLCJpYXQiOjE1NjgxNzAwMjgsImlzcyI6Ind3dy5pdGhlaW1hLmNvbSJ9.yIO3b9WjmBXcvmQYRHCTgpknuI-QX8Y5bhXD_vAmss4";
        //1、创建Jwt解析器-jwtParser = Jwts.parser();
        JwtParser jwtParser = Jwts.parser();
        //2、设置签名-密钥
        jwtParser.setSigningKey("itheima.steven");
        //3、设置要解析的密文，并读取结果
        Claims claims = jwtParser.parseClaimsJws(token).getBody();
        System.out.println(jwtParser.parseClaimsJws(token).getSignature());
        //4、输出结果
        System.out.println(claims);
    }
}