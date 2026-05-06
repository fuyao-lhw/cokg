package com.norway.cokgbackend.service;

import com.norway.cokgbackend.model.params.PermissionParam;
import com.norway.cokgbackend.model.params.UserParam;
import com.norway.cokgbackend.model.params.LoginParam;
import com.norway.cokgbackend.model.Result;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * @Description: TODO
 * @Author: Norway
 * @Email: 1959415641@qq.com
 * @Date: 2025/12/25 13:06
 */
public interface UserService {

    public Result login(LoginParam loginParam);

    public Result addUser(UserParam.AddUserParam addUserParam);

    public Result getUserList();

    public Result checkAuth(String code);

    public Result distribute(List<PermissionParam> permissionParam);

    public Result getUserShareKg();
}
