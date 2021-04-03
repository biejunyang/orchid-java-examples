package com.orchid.miaosha.controller;


import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.api.R;
import com.orchid.miaosha.entity.MiaoshaProduct;
import com.orchid.miaosha.entity.MiaoshaoOrder;
import com.orchid.miaosha.service.MiaoshaProductService;
import com.orchid.miaosha.service.MiaoshaoOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/miaoshao")
public class MiaoshaController {

    @Autowired
    private MiaoshaProductService miaoshaProductService;

    @Autowired
    private MiaoshaoOrderService miaoshaoOrderService;

    @PostMapping
    public R domiaosha(@RequestBody MiaoshaoOrder miaoshaoOrder) {
        MiaoshaProduct product=miaoshaProductService.getById(miaoshaoOrder.getProductId());
        if(product.getQuantity()<=0){
            return R.failed("已卖完");
        }
        int count=miaoshaoOrderService.count(Wrappers.<MiaoshaoOrder>lambdaQuery()
                .eq(MiaoshaoOrder::getUserId, miaoshaoOrder.getUserId()));
        if(count>0){
            return R.failed("不能重复");
        }
        product.setQuantity(product.getQuantity()-1);
        miaoshaProductService.updateById(product);
        miaoshaoOrder.setQuantity(1);
        miaoshaoOrderService.save(miaoshaoOrder);
        return R.ok(null);
    }

}
