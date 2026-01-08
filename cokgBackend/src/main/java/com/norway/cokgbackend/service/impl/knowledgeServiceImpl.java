package com.norway.cokgbackend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.norway.cokgbackend.mapper.KnowledgeMetaMapper;
import com.norway.cokgbackend.mapper.entity.KnowledgeMetaEntity;
import com.norway.cokgbackend.model.Result;
import com.norway.cokgbackend.service.knowledgeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Description: TODO
 * @Author: Norway
 * @Email: 1959415641@qq.com
 * @Date: 2026/1/8 21:34
 */

@Service
@Slf4j
public class knowledgeServiceImpl implements knowledgeService {

    @Autowired
    private KnowledgeMetaMapper metaMapper;

    @Override
    public Result list() {

        List<KnowledgeMetaEntity> knowledgeMetaEntityList = metaMapper.selectList(new QueryWrapper<>());


        return Result.success(knowledgeMetaEntityList);
    }
}
