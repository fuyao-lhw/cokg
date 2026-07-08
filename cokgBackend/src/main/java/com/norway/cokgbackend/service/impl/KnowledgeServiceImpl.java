package com.norway.cokgbackend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.norway.cokgbackend.enums.ResultCodeEnum;
import com.norway.cokgbackend.mapper.DeptMapper;
import com.norway.cokgbackend.mapper.entity.DeptEntity;
import com.norway.cokgbackend.mapper.entity.knowledge.KnowledgeNodeEntity;
import com.norway.cokgbackend.mapper.entity.knowledge.KnowledgeRelationEntity;
import com.norway.cokgbackend.mapper.knowledge.KnowledgeMetaMapper;
import com.norway.cokgbackend.mapper.UserMapper;
import com.norway.cokgbackend.mapper.entity.knowledge.KnowledgeMetaEntity;
import com.norway.cokgbackend.mapper.entity.UserEntity;
import com.norway.cokgbackend.mapper.knowledge.KnowledgeNodeMapper;
import com.norway.cokgbackend.mapper.knowledge.KnowledgeRelationMapper;
import com.norway.cokgbackend.model.Result;
import com.norway.cokgbackend.model.params.knowledge.KnowledgeNodeParam;
import com.norway.cokgbackend.model.params.knowledge.KnowledgeParam;
import com.norway.cokgbackend.model.params.knowledge.KnowledgeRelationParam;
import com.norway.cokgbackend.service.KnowledgeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.norway.cokgbackend.model.vo.knowledge.listVO;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Description: TODO
 * @Author: Norway
 * @Email: 1959415641@qq.com
 * @Date: 2026/1/8 21:34
 */

@Service
@Slf4j
public class KnowledgeServiceImpl implements KnowledgeService {

    @Autowired
    private KnowledgeMetaMapper metaMapper;
    @Autowired
    private KnowledgeNodeMapper nodeMapper;
    @Autowired
    private KnowledgeRelationMapper relationMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private DeptMapper deptMapper;


    @Override
    public Result list() {

        List<KnowledgeMetaEntity> knowledgeMetaEntityList = metaMapper.selectList(
                new QueryWrapper<KnowledgeMetaEntity>()
                        .eq("is_deleted", 0));

        List<listVO> kgList = knowledgeMetaEntityList.stream().map(
                meta -> {
                    listVO vo = new listVO();
                    BeanUtils.copyProperties(meta, vo);
                    vo.setCreateUserName(userMapper.selectOne(
                            new QueryWrapper<UserEntity>()
                                    .eq("user_id", meta.getCreateUserId())
                    ).getName());
                    vo.setDeptName(deptMapper.selectOne(
                            new QueryWrapper<DeptEntity>()
                                    .eq("dept_id", meta.getDeptId())
                    ).getName());
                    return vo;
                }
        ).toList();


        return Result.success(kgList);
    }

    @Override
    public Result batchGetKnowledgeList(List<Long> kgMetaIdList) {
        // TODO: 2026/2/9 2:34 实现批量获取知识列表
        List<KnowledgeMetaEntity> knowledgeMetaEntityList = metaMapper.selectList(new QueryWrapper<KnowledgeMetaEntity>()
                .in("kg_meta_id", kgMetaIdList)
                .eq("is_deleted", 0));
        return Result.success(knowledgeMetaEntityList);
    }

    @Override
    public Result add(KnowledgeParam.AddKnowledgeParam addKnowledgeParam) {

        if (addKnowledgeParam == null) {
            return Result.fail("参数不能为空");
        }

        KnowledgeMetaEntity knowledgeMetaEntity = new KnowledgeMetaEntity();
        knowledgeMetaEntity.setKgName(addKnowledgeParam.getKgName());
        knowledgeMetaEntity.setKgType(addKnowledgeParam.getKgType());
        knowledgeMetaEntity.setDeptId(addKnowledgeParam.getDeptId());
        Long userId = userMapper.selectOne(new QueryWrapper<UserEntity>().eq("code", addKnowledgeParam.getCreateUserCode())).getUserId();
        knowledgeMetaEntity.setCreateUserId(userId);

        metaMapper.insert(knowledgeMetaEntity);

        return Result.success(metaMapper.selectList(new QueryWrapper<KnowledgeMetaEntity>().eq("kg_meta_id", knowledgeMetaEntity.getKgMetaId())));
    }

    @Override
    public Result update(KnowledgeParam.UpdateKnowledgeParam updateKnowledgeParam) {

        if (updateKnowledgeParam == null) {
            return Result.fail("参数不能为空");
        }

        KnowledgeMetaEntity knowledgeMetaEntity = metaMapper.selectOne(new QueryWrapper<KnowledgeMetaEntity>().eq("kg_meta_id", updateKnowledgeParam.getKgMetaId()));
        if (knowledgeMetaEntity == null) {
            return Result.fail("知识图谱不存在");
        }

        knowledgeMetaEntity.setKgName(updateKnowledgeParam.getKgName());
        knowledgeMetaEntity.setKgType(updateKnowledgeParam.getKgType());
        knowledgeMetaEntity.setDeptId(updateKnowledgeParam.getDeptId());
        knowledgeMetaEntity.setUpdateTime(LocalDateTime.now());

        metaMapper.updateById(knowledgeMetaEntity);

        return Result.success(metaMapper.selectList(new QueryWrapper<KnowledgeMetaEntity>().eq("kg_meta_id", knowledgeMetaEntity.getKgMetaId())));
    }

    @Override
    public Result deleteKnowledge(Long kgMetaId) {

        if (kgMetaId == -1) {
            return Result.fail("知识图谱ID不能为空");
        }

        KnowledgeMetaEntity knowledgeMetaEntity = metaMapper.selectOne(new QueryWrapper<KnowledgeMetaEntity>().eq("kg_meta_id", kgMetaId));
        if (knowledgeMetaEntity == null) {
            return Result.fail("知识图谱不存在");
        }

        knowledgeMetaEntity.setIsDeleted(1);
        knowledgeMetaEntity.setUpdateTime(LocalDateTime.now());

        metaMapper.updateById(knowledgeMetaEntity);

        return new Result(ResultCodeEnum.SUCCESS.getCode(), "delete succeed!", new ArrayList<>());
    }

    @Override
    public Result getKnowledgeNodeList(Long kgMetaId) {

        if (kgMetaId == -1) {
            List<KnowledgeNodeEntity> knowledgeNodeEntityList = nodeMapper.selectList(
                    new QueryWrapper<KnowledgeNodeEntity>().eq("is_deleted", 0));
            return Result.success(knowledgeNodeEntityList);
        }

        List<KnowledgeNodeEntity> knowledgeNodeEntityList = nodeMapper.selectList(
                new QueryWrapper<KnowledgeNodeEntity>().eq("kg_meta_id", kgMetaId)
                        .eq("is_deleted", 0));
        return Result.success(knowledgeNodeEntityList);
    }

    @Override
    public Result batchGetKnowledgeNodeList(List<Long> kgNodeIdList) {
        // TODO: 2026/2/8 2:34 实现批量获取知识节点列表
        List<KnowledgeNodeEntity> knowledgeNodeEntityList = nodeMapper.selectList(new QueryWrapper<KnowledgeNodeEntity>()
                .in("kg_node_id", kgNodeIdList)
                .eq("is_deleted", 0));
        return Result.success(knowledgeNodeEntityList);
    }

    @Override
    public Result addKnowledgeNode(KnowledgeNodeParam.AddKnowledgeNodeParam addKnowledgeNodeParam) {


        if (addKnowledgeNodeParam == null) {
            return Result.fail("参数不能为空");
        }

        if (addKnowledgeNodeParam.getKgMetaId() == -1) {
            return Result.fail("知识图谱ID不能为空");
        }

        KnowledgeNodeEntity knowledgeNodeEntity = new KnowledgeNodeEntity();
        knowledgeNodeEntity.setKgMetaId(addKnowledgeNodeParam.getKgMetaId());
        knowledgeNodeEntity.setNodeName(addKnowledgeNodeParam.getNodeName());
        knowledgeNodeEntity.setNodeType(addKnowledgeNodeParam.getNodeType());
        knowledgeNodeEntity.setNodeAttr(addKnowledgeNodeParam.getNodeAttr());


        Long userId = userMapper.selectOne(new QueryWrapper<UserEntity>().eq("code", addKnowledgeNodeParam.getCreateUserCode())).getUserId();
        knowledgeNodeEntity.setCreateUserId(userId);

        nodeMapper.insert(knowledgeNodeEntity);

        return Result.success(nodeMapper.selectOne(new QueryWrapper<KnowledgeNodeEntity>().eq("kg_node_id", knowledgeNodeEntity.getKgNodeId())));
    }

    @Override
    public Result updateKnowledgeNode(KnowledgeNodeParam.UpdateKnowledgeNodeParam updateKnowledgeNodeParam) {
        if (updateKnowledgeNodeParam == null) {
            return Result.fail("参数不能为空");
        }

        if (updateKnowledgeNodeParam.getKgNodeId() == -1) {
            return Result.fail("知识节点ID不能为空");
        }

        KnowledgeNodeEntity knowledgeNodeEntity = nodeMapper.selectOne(new QueryWrapper<KnowledgeNodeEntity>().eq("kg_node_id", updateKnowledgeNodeParam.getKgNodeId()));
        if (knowledgeNodeEntity == null) {
            return Result.fail("知识节点不存在");
        }

        knowledgeNodeEntity.setNodeName(updateKnowledgeNodeParam.getNodeName());
        knowledgeNodeEntity.setNodeType(updateKnowledgeNodeParam.getNodeType());
        knowledgeNodeEntity.setNodeAttr(updateKnowledgeNodeParam.getNodeAttr());
        knowledgeNodeEntity.setKgMetaId(updateKnowledgeNodeParam.getKgMetaId());

        nodeMapper.updateById(knowledgeNodeEntity);

        return Result.success(nodeMapper.selectOne(new QueryWrapper<KnowledgeNodeEntity>().eq("kg_node_id", knowledgeNodeEntity.getKgNodeId())));
    }

    @Override
    public Result deleteKnowledgeNode(Long kgNodeId) {
        if (kgNodeId == -1) {
            return Result.fail("知识节点ID不能为空");
        }

        KnowledgeNodeEntity knowledgeNodeEntity = nodeMapper.selectOne(new QueryWrapper<KnowledgeNodeEntity>().eq("kg_node_id", kgNodeId));
        if (knowledgeNodeEntity == null) {
            return Result.fail("知识节点不存在");
        }

        knowledgeNodeEntity.setIsDeleted(1);

        nodeMapper.updateById(knowledgeNodeEntity);

        return Result.success(knowledgeNodeEntity);
    }

    @Override
    public Result getKnowledgeRelationList(Long kgMetaId) {

        if (kgMetaId == -1) {
            List<KnowledgeRelationEntity> relationEntityList = relationMapper.selectList(new QueryWrapper<>());
            return Result.success(relationEntityList);
        }

        List<KnowledgeRelationEntity> relationEntityList = relationMapper.selectList(
                new QueryWrapper<KnowledgeRelationEntity>()
                        .eq("kg_meta_id", kgMetaId)
                        .eq("is_deleted", 0));

        return Result.success(relationEntityList);
    }

    @Override
    public Result addKnowledgeRelation(KnowledgeRelationParam.AddKnowledgeRelationParam addKnowledgeRelationParam) {


        if (addKnowledgeRelationParam == null) {
            return Result.fail("参数不能为空");
        }

        if (addKnowledgeRelationParam.getKgMetaId() == -1) {
            return Result.fail("知识图谱ID不能为空");
        }

        if (addKnowledgeRelationParam.getKgNodeSrcId() == -1) {
            return Result.fail("源节点ID不能为空");
        }

        if (addKnowledgeRelationParam.getKgNodeTargetId() == -1) {
            return Result.fail("目标节点ID不能为空");
        }

        if (addKnowledgeRelationParam.getRelationType() == null) {
            return Result.fail("关系类型不能为空");
        }

        if (addKnowledgeRelationParam.getWeight() == null) {
            return Result.fail("关系权重不能为空");
        }

        KnowledgeRelationEntity knowledgeRelationEntity = new KnowledgeRelationEntity();
        knowledgeRelationEntity.setKgMetaId(addKnowledgeRelationParam.getKgMetaId());
        knowledgeRelationEntity.setKgNodeSrcId(addKnowledgeRelationParam.getKgNodeSrcId());
        knowledgeRelationEntity.setKgNodeTargetId(addKnowledgeRelationParam.getKgNodeTargetId());
        knowledgeRelationEntity.setRelationType(addKnowledgeRelationParam.getRelationType());
        knowledgeRelationEntity.setProperties(addKnowledgeRelationParam.getProperties());
        knowledgeRelationEntity.setWeight(addKnowledgeRelationParam.getWeight());

        Long userId = userMapper.selectOne(new QueryWrapper<UserEntity>().eq("code", addKnowledgeRelationParam.getCreateUserCode())).getUserId();
        knowledgeRelationEntity.setCreateUserId(userId);

        relationMapper.insert(knowledgeRelationEntity);

        return Result.success(relationMapper.selectOne(new QueryWrapper<KnowledgeRelationEntity>().eq("kg_relation_id", knowledgeRelationEntity.getKgRelationId())));
    }

    @Override
    public Result deleteKnowledgeRelation(Long kgRelationId) {
        if (kgRelationId == -1) {
            return Result.fail("知识关系ID不能为空");
        }

        KnowledgeRelationEntity knowledgeRelationEntity = relationMapper.selectOne(new QueryWrapper<KnowledgeRelationEntity>().eq("kg_relation_id", kgRelationId));
        if (knowledgeRelationEntity == null) {
            return Result.fail("知识关系不存在");
        }

        knowledgeRelationEntity.setIsDeleted(1);

        relationMapper.updateById(knowledgeRelationEntity);

        return Result.success(knowledgeRelationEntity);
    }

    @Override
    public Result updateKnowledgeRelation(KnowledgeRelationParam.UpdateKnowledgeRelationParam updateKnowledgeRelationParam) {
        if (updateKnowledgeRelationParam == null) {
            return Result.fail("参数不能为空");
        }

        if (updateKnowledgeRelationParam.getKgRelationId() == -1) {
            return Result.fail("知识关系ID不能为空");
        }

        KnowledgeRelationEntity knowledgeRelationEntity = relationMapper.selectOne(new QueryWrapper<KnowledgeRelationEntity>().eq("kg_relation_id", updateKnowledgeRelationParam.getKgRelationId()));
        if (knowledgeRelationEntity == null) {
            return Result.fail("知识关系不存在");
        }

        knowledgeRelationEntity.setRelationType(updateKnowledgeRelationParam.getRelationType());
        knowledgeRelationEntity.setProperties(updateKnowledgeRelationParam.getProperties());
        knowledgeRelationEntity.setWeight(updateKnowledgeRelationParam.getWeight());

        relationMapper.updateById(knowledgeRelationEntity);

        return Result.success(knowledgeRelationEntity);
    }
}
