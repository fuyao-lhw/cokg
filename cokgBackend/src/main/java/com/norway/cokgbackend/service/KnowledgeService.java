package com.norway.cokgbackend.service;

import com.norway.cokgbackend.model.Result;
import com.norway.cokgbackend.model.params.knowledge.KnowledgeNodeParam;
import com.norway.cokgbackend.model.params.knowledge.KnowledgeParam;
import com.norway.cokgbackend.model.params.knowledge.KnowledgeRelationParam;

import java.util.List;

/**
 * @Description: TODO
 * @Author: Norway
 * @Email: 1959415641@qq.com
 * @Date: 2026/1/8 21:33
 */
public interface KnowledgeService {

    public Result list();

    public Result batchGetKnowledgeList(List<Long> kgMetaIdList);

    public Result add(KnowledgeParam.AddKnowledgeParam addKnowledgeParam);

    public Result update(KnowledgeParam.UpdateKnowledgeParam updateKnowledgeParam);

    public Result deleteKnowledge(Long kgMetaId);

    public Result getKnowledgeNodeList(Long kgMetaId);

    public Result batchGetKnowledgeNodeList(List<Long> kgNodeIdList);

    public Result addKnowledgeNode(KnowledgeNodeParam.AddKnowledgeNodeParam addKnowledgeNodeParam);

    public Result updateKnowledgeNode(KnowledgeNodeParam.UpdateKnowledgeNodeParam updateKnowledgeNodeParam);

    public Result deleteKnowledgeNode(Long kgNodeId);

    public Result getKnowledgeRelationList(Long kgMetaId);

    public Result addKnowledgeRelation(KnowledgeRelationParam.AddKnowledgeRelationParam addKnowledgeRelationParam);
    public Result deleteKnowledgeRelation(Long kgRelationId);
    public Result updateKnowledgeRelation(KnowledgeRelationParam.UpdateKnowledgeRelationParam updateKnowledgeRelationParam);
}
