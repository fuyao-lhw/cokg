package com.norway.cokgbackend.service;

import com.norway.cokgbackend.model.params.AddUserParam;
import com.norway.cokgbackend.model.params.LoginParam;
import com.norway.cokgbackend.model.Result;

/**
 * @Description: TODO
 * @Author: Norway
 * @Email: 1959415641@qq.com
 * @Date: 2025/12/25 13:06
 */
public interface UserService {

    public Result login(LoginParam loginParam);

    public Result addUser(AddUserParam addUserParam);

    public Result getUserList();

    public Result checkAuth(String code);

}
