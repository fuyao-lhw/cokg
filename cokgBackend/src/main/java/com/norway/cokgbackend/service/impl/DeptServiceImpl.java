package com.norway.cokgbackend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.norway.cokgbackend.mapper.DeptMapper;
import com.norway.cokgbackend.mapper.entity.DeptEntity;
import com.norway.cokgbackend.model.Result;
import com.norway.cokgbackend.model.params.AddDeptParam;
import com.norway.cokgbackend.service.DeptService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @Description: TODO
 * @Author: Norway
 * @Email: 1959415641@qq.com
 * @Date: 2026/1/2 0:16
 */
@Service
@Slf4j
public class DeptServiceImpl implements DeptService {

    @Autowired
    private DeptMapper deptMapper;

    @Override
    public Result getDeptList() {

        // 从数据库中查询部门列表
        List<DeptEntity> deptEntityList = deptMapper.selectList(null);

        log.info("deptEntityList: {}", deptEntityList);

        // 返回部门列表
        return Result.success(deptEntityList);


    }

    @Override
    public Result addDept(AddDeptParam addDeptParam) {


        // 从参数中获取部门名称
        String name = addDeptParam.getName();

        // 检查部门名称是否为空
        if (name == null || name.isEmpty()) {
            return Result.fail("部门名称不能为空");
        }

        // 检查部门名称是否已存在
        DeptEntity existingDept = deptMapper.selectOne(new LambdaQueryWrapper<DeptEntity>().eq(DeptEntity::getName, name));
        if (existingDept != null) {
            return Result.fail("部门名称已存在");
        }

        // 创建新的部门实体
        DeptEntity newDept = new DeptEntity();
        newDept.setName(name);

        // 保存部门到数据库
        deptMapper.insert(newDept);

        return null;
    }
}
