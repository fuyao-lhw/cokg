package com.norway.cokgbackend.mapper.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.sql.Date;
import java.time.LocalDateTime;

/**
 * @Description: TODO
 * @Author: Norway
 * @Email: 1959415641@qq.com
 * @Date: 2025/12/25 13:23
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("dept")
public class DeptEntity {
    @TableId(type = IdType.AUTO)
    private Long deptId;
    private String name;
    private LocalDateTime createTime;
    private Integer isDeleted;
}
