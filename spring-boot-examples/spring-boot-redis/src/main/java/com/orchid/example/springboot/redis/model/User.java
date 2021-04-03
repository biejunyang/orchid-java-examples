package com.orchid.example.springboot.redis.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class User {
    private Integer id;

    private String name;

    private Integer age;

    private Double weight;

    private Double funds;

    private Date birthday;

    private Date createTime;

    private List<Product> inventorys=new ArrayList<Product>();

}
