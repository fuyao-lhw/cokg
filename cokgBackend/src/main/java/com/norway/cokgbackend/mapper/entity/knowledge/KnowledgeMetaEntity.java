package com.norway.cokgbackend.mapper.entity.knowledge;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * @Description: TODO
 * @Author: Norway
 * @Email: 1959415641@qq.com
 * @Date: 2026/1/8 21:35
 */
@TableName("kg_meta")
@AllArgsConstructor
@Data
@NoArgsConstructor
public class KnowledgeMetaEntity {

    @TableId(type = IdType.AUTO)
    private Long kgMetaId;
    private String kgName;
    private Integer kgType;
    private Long deptId;
    private Long createUserId;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private Integer isDeleted;


}
