package com.norway.cokgbackend.controller;

import com.norway.cokgbackend.model.Result;
import com.norway.cokgbackend.model.params.AddDeptParam;
import com.norway.cokgbackend.service.DeptService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @Description: TODO
 * @Author: Norway
 * @Email: 1959415641@qq.com
 * @Date: 2026/1/2 0:16
 */
@RestController
@RequestMapping("/dept")
@Slf4j
public class deptController {

    @Autowired
    private DeptService deptService;

    @GetMapping("/list")
    public Result getDeptList() {
        return deptService.getDeptList();
    }

    @PostMapping("/add")
    public Result addDept(@RequestBody AddDeptParam addDeptParam) {
        log.info("addDeptParam: {}", addDeptParam.toString());
        return deptService.addDept(addDeptParam);
    }

}
