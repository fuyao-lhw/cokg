package com.norway.cokgbackend.enums;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

/**
 * @Description: TODO
 * @Author: Norway
 * @Email: 1959415641@qq.com
 * @Date: 2026/1/3 20:25
 */
@AllArgsConstructor
public enum ResultCodeEnum {

    SUCCESS(200, "操作成功"),

    FAIL(100, "操作失败"),
    TOKEN_FAILED(101, ""), // token出现问题



    NO_AUTH(401, "操作没有权限");

    @Getter
    private Integer code;
    @Getter
    @Setter
    private String msg;

}
