package com.norway.cokgbackend.mapper.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

/**
 * @Description: TODO
 * @Author: Norway
 * @Email: 1959415641@qq.com
 * @Date: 2025/12/25 13:17
 */
@TableName("user")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserEntity {
    @TableId(type = IdType.AUTO)
    private Long userId;
    private String name;
    private String password;
    private Long deptId;
    private String code;
    private Integer role;
    private DateTimeFormat createTime;
    private Integer isDeleted;
}
