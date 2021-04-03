package com.orchid.miaosha.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.orchid.miaosha.dao.MiaoshaoOrderDao;
import com.orchid.miaosha.entity.MiaoshaoOrder;
import com.orchid.miaosha.service.MiaoshaoOrderService;
import org.springframework.stereotype.Service;

/**
 * (MiaoshaoOrder)表服务实现类
 *
 * @author makejava
 * @since 2020-04-14 15:51:01
 */
@Service("miaoshaoOrderService")
public class MiaoshaoOrderServiceImpl extends ServiceImpl<MiaoshaoOrderDao, MiaoshaoOrder> implements MiaoshaoOrderService {

}