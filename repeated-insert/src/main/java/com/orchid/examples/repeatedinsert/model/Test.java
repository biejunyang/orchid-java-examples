package com.orchid.examples.repeatedinsert.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Test {

    @TableId(value = "id", type= IdType.AUTO)
    private Integer id;

    private String name;

    private Integer age;
}
