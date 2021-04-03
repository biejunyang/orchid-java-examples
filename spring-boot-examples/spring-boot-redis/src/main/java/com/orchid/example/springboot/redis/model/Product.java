package com.orchid.example.springboot.redis.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@NoArgsConstructor
@Accessors(chain = true)
public class Product {

    private Integer userId;

    private String productName;

    private Double price;


}
