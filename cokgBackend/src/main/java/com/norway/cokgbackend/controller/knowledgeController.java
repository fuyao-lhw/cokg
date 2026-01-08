package com.norway.cokgbackend.controller;

import com.norway.cokgbackend.model.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Description: TODO
 * @Author: Norway
 * @Email: 1959415641@qq.com
 * @Date: 2026/1/8 20:37
 */
@RestController
@RequestMapping("/knowledge")
@Slf4j
public class knowledgeController {

    @GetMapping("/list")
    public Result getKnowledgeList(){
        return Result.success(null);
    }


}
