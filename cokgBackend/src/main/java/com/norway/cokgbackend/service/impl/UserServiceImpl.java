package com.norway.cokgbackend.service.impl;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.norway.cokgbackend.enums.ResultCodeEnum;
import com.norway.cokgbackend.mapper.DeptMapper;
import com.norway.cokgbackend.mapper.UserMapper;
import com.norway.cokgbackend.mapper.entity.DeptEntity;
import com.norway.cokgbackend.mapper.entity.UserEntity;
import com.norway.cokgbackend.mapper.entity.knowledge.KnowledgeShareEntity;
import com.norway.cokgbackend.mapper.knowledge.KnowledgeShareMapper;
import com.norway.cokgbackend.model.JWTResult;
import com.norway.cokgbackend.model.params.PermissionParam;
import com.norway.cokgbackend.model.params.UserParam;
import com.norway.cokgbackend.model.params.JWTParam;
import com.norway.cokgbackend.model.params.LoginParam;
import com.norway.cokgbackend.model.Result;
import com.norway.cokgbackend.service.UserService;
import com.norway.cokgbackend.utils.JWTUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Description: TODO
 * @Author: Norway
 * @Email: 1959415641@qq.com
 * @Date: 2025/12/25 13:07
 */
@Service
@Slf4j
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private DeptMapper deptMapper;

    @Autowired
    private JWTUtil jwtUtil;

    @Autowired
    private KnowledgeShareMapper knowledgeShareMapper;


    @Override
    public Result login(LoginParam loginParam) {
        // 校验参数
        if (loginParam.getCode() == null || loginParam.getPassword() == null) {
            return Result.fail("用户名（工号）或密码不能为空");
        }

        // 校验用户名是否存在
        UserEntity userEntity = userMapper.selectOne(new LambdaQueryWrapper<UserEntity>().eq(UserEntity::getCode, loginParam.getCode()));
        if (userEntity == null || userEntity.getIsDeleted() == 1) {
            return Result.fail("用户不存在");
        }

        // 校验密码是否正确
        if (!userEntity.getPassword().equals(loginParam.getPassword())) {
            return Result.fail("密码错误");
        }

        log.info("userEntity: {}", userEntity.toString());

        // 校验成功，生成JWT
        String token = jwtUtil.generateToken(new JWTParam(userEntity.getName(), userEntity.getUserId().intValue(), userEntity.getRole()));

        Map<String, Object> data = new HashMap<>();
        data.put("token", token);
        data.put("name", userEntity.getName());
        data.put("code", userEntity.getCode());

        return new Result(ResultCodeEnum.SUCCESS.getCode(), "登录成功", data);
    }

    @Override
    public Result addUser(UserParam.AddUserParam addUserParam) {
        // 校验参数
        // 用户是否已经在数据库中
        // 校验用户名是否重复
        UserEntity userEntity = userMapper.selectOne(new LambdaQueryWrapper<UserEntity>().eq(UserEntity::getName, addUserParam.getName()));
        if (userEntity != null) {
            return Result.fail("用户已存在");
        }

        // 校验部门是否存在
        DeptEntity deptEntity = deptMapper.selectOne(new LambdaQueryWrapper<DeptEntity>().eq(DeptEntity::getDeptId, addUserParam.getDeptId()));
        if (deptEntity == null) {
            return Result.fail("部门不存在");
        }

        // 构建用户实体
        userEntity = new UserEntity();


        Long userLength = userMapper.selectCount(new QueryWrapper<>());
        userEntity.setCode(deptEntity.getDeptId() + String.format("%04d", userLength + 1));

        userEntity.setName(addUserParam.getName());
        userEntity.setPassword("123456");
        userEntity.setDeptId(addUserParam.getDeptId());
        userEntity.setRole(addUserParam.getRole());
        userEntity.setIsDeleted(0);

        // 插入数据库
        userMapper.insert(userEntity);

        return Result.success(userEntity);
    }

    @Override
    public Result getUserList() {
        List<UserEntity> userList = userMapper.selectList(new QueryWrapper<>());
        // 删除密码字段
        userList.forEach(user -> user.setPassword(null));

        log.info("userList: {}", userList);

//        userList = new ArrayList<>();
//        userList.add(new UserEntity(1L, "Norway", "123456", 1, "100001", 1, null, 0));
//        userList.add(new UserEntity(2L, "张三", "123456", 2, "100002", 0, null, 0));
//        userList.add(new UserEntity(3L, "李四", "123456", 3, "100003", 0, null, 0));

        return Result.success(userList);
    }

    @Override
    public Result checkAuth(String token) {

        // 校验参数
        if (token == null) {
            return Result.fail("token不能为空");
        }

        // 校验token是否存在
        JWTResult jwtResult = jwtUtil.verifyToken(token);
        if (jwtResult == null || !jwtResult.getVerify()) {
            return new Result(ResultCodeEnum.TOKEN_FAILED.getCode(), "token失效", null);
        }

        // 校验token是否过期
        if (jwtResult.getData().getExpiresAt().before(new Date())) {
            return new Result(ResultCodeEnum.TOKEN_FAILED.getCode(), "token过期", null);
        }

        // 获取JWT解码数据
        DecodedJWT decodedJWT = jwtResult.getData();
        log.info("decodedJWT: {}", decodedJWT);
        log.info("decodedJWT.getClaim(\"role\").asInt(): {}", decodedJWT.getClaim("role").asInt());
        log.info("decodedJWT.getClaim(\"code\").asString(): {}", decodedJWT.getClaim("code").asString());
        // 从JWT中获取用户角色
        Integer role = decodedJWT.getClaim("role").asInt();
        // 校验用户权限
        if (role == null || role == 1) {
            return Result.fail("普通用户");
        }

        return new Result(ResultCodeEnum.SUCCESS.getCode(), "权限校验成功,用户角色为: 管理员", null);

    }

    @Override
    public Result distribute(List<PermissionParam> permissionParam) {
        for (PermissionParam item : permissionParam) {
            for (Long kgMetaId : item.getKgMetaIds()) {
                KnowledgeShareEntity knowledgeShareEntity = knowledgeShareMapper.selectOne(
                        new QueryWrapper<KnowledgeShareEntity>()
                                .eq("kg_share_id", item.getUserId())
                                .eq("kg_id", kgMetaId)
                );
                if (knowledgeShareEntity != null) {
                    continue;
                }
                KnowledgeShareEntity existShare = knowledgeShareMapper.selectOne(
                        new QueryWrapper<KnowledgeShareEntity>()
                                .eq("kg_id", kgMetaId)
                                .eq("share_user_id", item.getUserId())
                );
                if (existShare == null) {
                    log.info("图谱：{}，可见用户：{} -- 已经加入过分享表", kgMetaId, item.getUserId());
                    continue;
                }
                KnowledgeShareEntity addKnowledgeShareEntity = new KnowledgeShareEntity();
                addKnowledgeShareEntity.setShareUserId(item.getUserId());
                addKnowledgeShareEntity.setKgId(kgMetaId);
                addKnowledgeShareEntity.setPermission(0);
                addKnowledgeShareEntity.setCreateTime(LocalDateTime.now());
                knowledgeShareMapper.insert(addKnowledgeShareEntity);
            }
        }

        return Result.success();
    }

    @Override
    public Result getUserShareKg() {

        List<UserEntity> userEntity = userMapper.selectList(
                new QueryWrapper<UserEntity>()
                        .select("user_id")
        );

        List<Map<String, Object>> userShareKgList = new ArrayList<>();

        userEntity.forEach(item -> {
            Long userId = item.getUserId();
            UserEntity user = userMapper.selectOne(
                    new QueryWrapper<UserEntity>()
                            .eq("user_id", userId)
            );
            Map<String, Object> userShareMap = new HashMap<>();
            userShareMap.put("userId", userId);
            userShareMap.put("userCode", user.getCode());
            userShareMap.put("userName", user.getName());
            userShareMap.put("originalKgIds", knowledgeShareMapper.selectList(
                    new QueryWrapper<KnowledgeShareEntity>()
                            .eq("share_user_id", userId)
            ).stream().map(KnowledgeShareEntity::getKgId).toList());
            userShareKgList.add(userShareMap);
        });


        return Result.success(userShareKgList);
    }
}
