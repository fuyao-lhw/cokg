package com.norway.cokgbackend.interceptor;

import com.norway.cokgbackend.model.Result;
import com.norway.cokgbackend.utils.JWTUtil;
import com.norway.cokgbackend.utils.JsonUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * @Description: TODO
 * @Author: Norway
 * @Email: 1959415641@qq.com
 * @Date: 2026/1/3 18:48
 */
@Slf4j
public class AuthControlInterceptor implements HandlerInterceptor {

    @Autowired
    private JWTUtil jwtUtil;


    /**
     * 预处理方法（核心）：请求到达 Controller 之前执行
     *
     * @return true - 放行请求（继续执行后续拦截器/Controller）；false - 拦截请求（不再向下执行）
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        log.info("【拦截器-preHandle】请求到达Controller前执行，请求路径：" + request.getRequestURI());

        // 1. 业务逻辑示例：获取并验证 JWT 令牌
        String authorization = request.getHeader("Authorization");
        String token = null;
        if (authorization != null && authorization.startsWith("Bearer ")) {
            token = authorization.substring(7); // 截取 Bearer 后的令牌
        }
        if (authorization != null && !authorization.startsWith("Bearer ")) {
            token = authorization;
        }

        log.info("token: {}", token);
        log.info("request.getRequestURI(): {}", request.getRequestURI());
        log.info("request.getHeader(\"Authorization\"): {}", request.getHeader("Authorization"));
        log.info("response.getContentType(): {}", response.getContentType());


        // 2. 令牌校验：无效则拦截，返回 401 未授权
        if (token == null || !jwtUtil.verifyToken(token).getVerify()) {
            log.info("jwt校验: {}", jwtUtil.verifyToken(token));
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write(JsonUtil.bean2JsonByJackson(Result.noAuth(null)));
            return false; // 拦截请求
        }
//
//        // 3. 令牌有效：可将用户信息存入 Request 域，供 Controller 使用
//        Long userId = jwtUtil.getUserIdFromToken(token);
//        request.setAttribute("userId", userId);
//        request.setAttribute("username", jwtUtil.getUsernameFromToken(token));

        return true; // 放行请求
    }

}
