package com.orchid.supply.controller;



import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.api.ApiController;
import com.baomidou.mybatisplus.extension.api.R;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.orchid.supply.entity.SupplyOrder;
import com.orchid.supply.service.SupplyOrderService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.io.Serializable;
import java.util.List;

/**
 * (SupplyOrder)表控制层
 *
 * @author makejava
 * @since 2020-04-10 17:06:07
 */
@RestController
@RequestMapping("supplyOrder")
public class SupplyOrderController extends ApiController {
    /**
     * 服务对象
     */
    @Resource
    private SupplyOrderService supplyOrderService;

    /**
     * 分页查询所有数据
     *
     * @param page 分页对象
     * @param supplyOrder 查询实体
     * @return 所有数据
     */
    @GetMapping
    public R selectAll(Page<SupplyOrder> page, SupplyOrder supplyOrder) {
        return success(this.supplyOrderService.page(page, new QueryWrapper<>(supplyOrder)));
    }

    /**
     * 通过主键查询单条数据
     *
     * @param id 主键
     * @return 单条数据
     */
    @GetMapping("{id}")
    public R selectOne(@PathVariable Serializable id) {
        return success(this.supplyOrderService.getById(id));
    }

    /**
     * 新增数据
     *
     * @param supplyOrder 实体对象
     * @return 新增结果
     */
    @PostMapping
    public R insert(@RequestBody SupplyOrder supplyOrder) {
        return success(this.supplyOrderService.save(supplyOrder));
    }

    /**
     * 修改数据
     *
     * @param supplyOrder 实体对象
     * @return 修改结果
     */
    @PutMapping
    public R update(@RequestBody SupplyOrder supplyOrder) {
        return success(this.supplyOrderService.updateById(supplyOrder));
    }

    /**
     * 删除数据
     *
     * @param idList 主键结合
     * @return 删除结果
     */
    @DeleteMapping
    public R delete(@RequestParam("idList") List<Long> idList) {
        return success(this.supplyOrderService.removeByIds(idList));
    }
}