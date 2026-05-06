package com.norway.cokgbackend.controller;

import com.norway.cokgbackend.model.params.PermissionParam;
import com.norway.cokgbackend.model.params.UserParam;
import com.norway.cokgbackend.model.params.LoginParam;
import com.norway.cokgbackend.model.Result;
import com.norway.cokgbackend.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * @Description: TODO
 * @Author: Norway
 * @Email: 1959415641@qq.com
 * @Date: 2025/12/24 23:57
 */

@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/login")
    public Result login(@RequestBody LoginParam loginParam) {
        log.info("loginParam: {}", loginParam.toString());
        return userService.login(loginParam);
    }

    @PostMapping("/add")
    public Result addUser(@RequestBody UserParam.AddUserParam addUserParam) {
        log.info("addUserParam: {}", addUserParam.toString());
        return userService.addUser(addUserParam);
    }

    @GetMapping("/list")
    public Result getUserList() {
        log.info("getUserList");
        return userService.getUserList();
    }

    @PostMapping("/check_auth")
    public Result checkAuth(@RequestBody Map<String, String> token) {
        log.info("checkAuth, token: {}", token.toString());
        return userService.checkAuth(token.get("token"));
    }

    @PostMapping("/permission/distribution")
    public Result distribute(@RequestBody List<PermissionParam> permissionParam){
        log.info("permissionParam: {}", permissionParam);
        return userService.distribute(permissionParam);

    }

    @GetMapping("/permission/user_share")
    public Result getUserShareKg(){
        log.info("获取用户可见图谱");
        return userService.getUserShareKg();
    }




}
