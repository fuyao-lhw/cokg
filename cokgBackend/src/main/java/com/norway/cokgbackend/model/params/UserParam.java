package com.norway.cokgbackend.model.params;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Description: TODO
 * @Author: Norway
 * @Email: 1959415641@qq.com
 * @Date: 2026/1/2 0:39
 */

public class UserParam {

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class AddUserParam{
        private String name;
        private Long deptId;
        private Integer role;
    }


    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class UpdateUserParam extends AddUserParam{
        private Long userId;

    }



}
