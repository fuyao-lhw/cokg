package com.norway.cokgbackend.service;

import com.norway.cokgbackend.model.Result;
import com.norway.cokgbackend.model.params.AddDeptParam;

/**
 * @Description: TODO
 * @Author: Norway
 * @Email: 1959415641@qq.com
 * @Date: 2026/1/2 0:16
 */
public interface DeptService {

     public Result getDeptList();

     public Result addDept(AddDeptParam addDeptParam);


}
