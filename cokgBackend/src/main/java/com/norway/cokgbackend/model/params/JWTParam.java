package com.norway.cokgbackend.model.params;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Description: TODO
 * @Author: Norway
 * @Email: 1959415641@qq.com
 * @Date: 2026/1/3 17:50
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class JWTParam {

    private String name;
    private Integer code;
    private Integer role;

}
