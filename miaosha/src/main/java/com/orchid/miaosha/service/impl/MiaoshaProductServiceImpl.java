package com.orchid.miaosha.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.orchid.miaosha.dao.MiaoshaProductDao;
import com.orchid.miaosha.entity.MiaoshaProduct;
import com.orchid.miaosha.service.MiaoshaProductService;
import org.springframework.stereotype.Service;

/**
 * (MiaoshaProduct)表服务实现类
 *
 * @author makejava
 * @since 2020-04-14 14:07:50
 */
@Service("miaoshaProductService")
public class MiaoshaProductServiceImpl extends ServiceImpl<MiaoshaProductDao, MiaoshaProduct> implements MiaoshaProductService {

}