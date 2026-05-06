package com.norway.cokgbackend.controller;

import com.norway.cokgbackend.model.Result;
import com.norway.cokgbackend.model.params.knowledge.KnowledgeNodeParam;
import com.norway.cokgbackend.model.params.knowledge.KnowledgeParam;
import com.norway.cokgbackend.model.params.knowledge.KnowledgeRelationParam;
import com.norway.cokgbackend.service.KnowledgeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @Description: TODO
 * @Author: Norway
 * @Email: 1959415641@qq.com
 * @Date: 2026/1/8 20:37
 */
@RestController
@RequestMapping("/knowledge")
@Slf4j
public class KnowledgeController {

    @Autowired
    private KnowledgeService knowledgeService;


    @GetMapping("/list")
    public Result getKnowledgeList(){
        return knowledgeService.list();
    }

    @PostMapping("/list/batch")
    public Result batchGetKnowledgeList(@RequestBody List<Long> kgMetaIdList){
        log.info("kgMetaIdList:{}",kgMetaIdList);
        return knowledgeService.batchGetKnowledgeList(kgMetaIdList);
    }


    @PostMapping("/add")
    public Result addKnowledge(@RequestBody KnowledgeParam.AddKnowledgeParam addKnowledgeParam){

        log.info("addKnowledgeParam:{}", addKnowledgeParam);

        return knowledgeService.add(addKnowledgeParam);
    }

    @PostMapping("/update")
    public Result updateKnowledge(@RequestBody KnowledgeParam.UpdateKnowledgeParam updateKnowledgeParam){
        log.info("updateKnowledgeParam:{}",updateKnowledgeParam);
        return knowledgeService.update(updateKnowledgeParam);
    }

    @PostMapping("/delete")
    public Result deleteKnowledge(@RequestBody Long kgMetaId){
        log.info("kgMetaId:{}",kgMetaId);
        return knowledgeService.deleteKnowledge(kgMetaId);
    }

    @GetMapping("/nodeList")
    public Result getKnowledgeNodeList(@RequestParam Long kgMetaId){

        log.info("kgMetaId:{}",kgMetaId);
        if (kgMetaId == -1) {
            log.info("获取全部知识图谱节点");
        }

        return knowledgeService.getKnowledgeNodeList(kgMetaId);
    }

    @PostMapping("/nodeList/batch")
    public Result batchGetKnowledgeNodeList(@RequestBody List<Long> kgNodeIdList){
        log.info("kgNodeIdList:{}",kgNodeIdList);
        return knowledgeService.batchGetKnowledgeNodeList(kgNodeIdList);
    }

    @PostMapping("/addNode")
    public Result addKnowledgeNode(@RequestBody KnowledgeNodeParam.AddKnowledgeNodeParam addKnowledgeNodeParam){
        log.info("addKnowledgeNodeParam:{}", addKnowledgeNodeParam);
        return knowledgeService.addKnowledgeNode(addKnowledgeNodeParam);
    }

    @PostMapping("/updateNode")
    public Result updateKnowledgeNode(@RequestBody KnowledgeNodeParam.UpdateKnowledgeNodeParam updateKnowledgeNodeParam){
        log.info("updateKnowledgeNodeParam:{}", updateKnowledgeNodeParam);
        return knowledgeService.updateKnowledgeNode(updateKnowledgeNodeParam);
    }

    @PostMapping("/deleteNode")
    public Result deleteKnowledgeNode(@RequestBody Long kgNodeId){
        log.info("kgNodeId:{}",kgNodeId);
        return knowledgeService.deleteKnowledgeNode(kgNodeId);
    }


    @GetMapping("/relationList")
    public Result getKnowledgeRelationList(@RequestParam Long kgMetaId){
        log.info("kgMetaId:{}",kgMetaId);
        if (kgMetaId == -1) {
            log.info("获取全部知识图谱关系");
        }
        return knowledgeService.getKnowledgeRelationList(kgMetaId);
    }

    @PostMapping("/addRelation")
    public Result addKnowledgeRelation(@RequestBody KnowledgeRelationParam.AddKnowledgeRelationParam addKnowledgeRelationParam){
        log.info("addKnowledgeRelationParam:{}", addKnowledgeRelationParam);
        return knowledgeService.addKnowledgeRelation(addKnowledgeRelationParam);
    }

    @PostMapping("/deleteRelation")
    public Result deleteKnowledgeRelation(@RequestBody Long kgRelationId){
        log.info("kgRelationId:{}",kgRelationId);
        return knowledgeService.deleteKnowledgeRelation(kgRelationId);
    }

    @PostMapping("/updateRelation")
    public Result updateKnowledgeRelation(@RequestBody KnowledgeRelationParam.UpdateKnowledgeRelationParam updateKnowledgeRelationParam){
        log.info("updateKnowledgeRelationParam:{}", updateKnowledgeRelationParam);
        return knowledgeService.updateKnowledgeRelation(updateKnowledgeRelationParam);
    }

}
