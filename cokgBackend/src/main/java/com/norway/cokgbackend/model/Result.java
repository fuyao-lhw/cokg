package com.norway.cokgbackend.model;

import com.norway.cokgbackend.enums.ResultCodeEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Array;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @Description: TODO
 * @Author: Norway
 * @Email: 1959415641@qq.com
 * @Date: 2025/12/25 13:08
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Result {

    private Integer code;
    private String msg;
    private Object data;


    public static Result success(Object data) {
        return new Result(ResultCodeEnum.SUCCESS.getCode(), ResultCodeEnum.SUCCESS.getMsg(), data);
    }

    public static Result success(){
        return new Result(ResultCodeEnum.SUCCESS.getCode(), ResultCodeEnum.SUCCESS.getMsg(), new ArrayList<>());
    }

    public static Result fail(String msg){
        String Msg = msg == null ? ResultCodeEnum.FAIL.getMsg() : msg;
        return new Result(ResultCodeEnum.FAIL.getCode(), Msg, null);
    }

     public static Result noAuth(String msg){
        String Msg = msg == null ? ResultCodeEnum.NO_AUTH.getMsg() : msg;
        return new Result(ResultCodeEnum.NO_AUTH.getCode(), Msg, null);
    }



}
