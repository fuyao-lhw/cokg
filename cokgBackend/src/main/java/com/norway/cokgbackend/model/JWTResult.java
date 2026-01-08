package com.norway.cokgbackend.model;

import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Description: TODO
 * @Author: Norway
 * @Email: 1959415641@qq.com
 * @Date: 2026/1/3 18:35
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class JWTResult {

    private String msg;
    private Boolean verify;
    private DecodedJWT data;
}
