package cn.chen.filter;

import cn.chen.utils.JwtUtil;
import io.jsonwebtoken.Claims;
import jdk.net.SocketFlow;
import org.apache.commons.lang.StringUtils;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpCookie;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.net.URI;

/**
 * @author haixin
 * @time 2019-11-08
 */

@Component
public class AuthorizeFilter  implements GlobalFilter, Ordered {

    //令牌的key
    private static final String AUTHORIZE_TOKEN = "Authorization";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        //1、获取Request、Response对象-exchange.get...
//2、获取请求的URI-request.getURI().getPath()
//3、如果是登录请求-uri.startsWith，放行-chain.filter
//4、如果是非登录请求




// 4.5 如果获取到了令牌，解析令牌-JwtUtil.parseJWT，放行-chain.filter(exchange)
        ServerHttpRequest request = exchange.getRequest();
        ServerHttpResponse response = exchange.getResponse();
        String path = request.getURI().getPath();
        if (path.startsWith("/api/user/login")) {
            chain.filter(exchange);
        }else {
            //4.1 获取前端传入的令牌-从请求头中获取-request.getHeaders().getFirst
            String token = request.getHeaders().getFirst(AUTHORIZE_TOKEN);
            if (StringUtils.isBlank(token)) {
                //4.2 如果头信息中没有，从请求参数中获取-request.getQueryParams().getFirst
                token = request.getQueryParams().getFirst(AUTHORIZE_TOKEN);
            }
            if (StringUtils.isBlank(token)) {
                MultiValueMap<String, HttpCookie> cookies = request.getCookies();
                if (cookies != null) {
                    //4.3 如果请求参数中没有，从cookie中获取-request.getCookies()-取值前先判断不为空-getFirst
                    HttpCookie cookiesFirst = cookies.getFirst(AUTHORIZE_TOKEN);
                    if (cookiesFirst != null) {
                        token = cookiesFirst.getValue();
                    }
                }
            }

            if (StringUtils.isBlank(token)) {
                //4.4 如果以上方式都取不到令牌-返回405错误-response.setStatusCode(405)-return response.setComplete
                response.setStatusCode(HttpStatus.METHOD_NOT_ALLOWED);
                return  response.setComplete();
            }else {
                //取得令牌，校验令牌
                try {
                    Claims claims = JwtUtil.parseJWT(token);
                    request.mutate().header(AUTHORIZE_TOKEN, claims.toString());
                } catch (Exception e) {
                    //校验失败处理结果
                    e.printStackTrace();
                    //无效的认证
                    response.setStatusCode(HttpStatus.UNAUTHORIZED);
                    return response.setComplete();

                }
            }
        }
        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
