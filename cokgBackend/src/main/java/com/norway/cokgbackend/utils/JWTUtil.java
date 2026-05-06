package com.norway.cokgbackend.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.norway.cokgbackend.model.JWTResult;
import com.norway.cokgbackend.model.Result;
import com.norway.cokgbackend.model.params.JWTParam;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;


/**
 * @Description: TODO
 * @Author: Norway
 * @Email: 1959415641@qq.com
 * @Date: 2026/1/3 17:41
 */
@Component
public class JWTUtil {

    // 2天
    public static final long EXPIRATION_TIME = 1000 * 60 * 60 * 48; // 2天

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.issuer}")
    private String issuer;


    /**
     * 生成token
     *
     * @Author hongwei.luo
     * @Date 2026/1/3 18:39
     * @Param [jwtParam]
     * @Return java.lang.String
     */
    public String generateToken(JWTParam jwtParam) {

        Algorithm algorithm = Algorithm.HMAC256(secret);
        return JWT.create()
                .withIssuer(issuer)
                .withSubject(jwtParam.getName())
                .withClaim("code", jwtParam.getCode())
                .withClaim("role", jwtParam.getRole())
                .withExpiresAt(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .sign(algorithm);

    }

    /**
     * 验证token
     *
     * @Author hongwei.luo
     * @Date 2026/1/3 18:39
     * @Param [token]
     * @Return JWTResult
     */

    public JWTResult verifyToken(String token) {

        try {
            // 指定算法
            Algorithm algorithm = Algorithm.HMAC256(secret);

            // 构建JWT验证器
            DecodedJWT jwt = JWT.require(algorithm)
                    .withIssuer(issuer)
                    .build()
                    .verify(token);

            return new JWTResult("token验证成功", true, jwt);
        } catch (JWTVerificationException e) {
            return new JWTResult("token验证失败：" + e.getMessage(), false, null);
        } catch (IllegalArgumentException e) {
            return new JWTResult("token为空或密钥无效：" + e.getMessage(), false, null);
        }


    }

    /**
     * 获取JWT信息
     * */
//    public JWTResult getJWTInfo(String token) {
//        return verifyToken(token);
//    }

}




