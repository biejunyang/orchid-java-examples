package com.orchid.miaosha.entity;

import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.IdType;
import java.io.Serializable;

/**
 * (MiaoshaoOrder)表实体类
 *
 * @author makejava
 * @since 2020-04-14 15:51:01
 */
@SuppressWarnings("serial")
@TableName("miaoshao_order")
public class MiaoshaoOrder extends Model<MiaoshaoOrder> {

    @TableId(value="id", type = IdType.ASSIGN_ID)
    private Long id;
    
    @TableField("product_id")
    private Long productId;
    
    @TableField("user_id")
    private Long userId;
    
    @TableField("quantity")
    private Integer quantity;
    


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    /**
     * 获取主键值
     *
     * @return 主键值
     */
    @Override
    protected Serializable pkVal() {
        return this.id;
    }
    }