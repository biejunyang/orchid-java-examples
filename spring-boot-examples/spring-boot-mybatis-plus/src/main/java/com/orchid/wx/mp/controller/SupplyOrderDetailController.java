package com.orchid.wx.mp.controller;



import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.api.ApiController;
import com.baomidou.mybatisplus.extension.api.R;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.orchid.supply.entity.SupplyOrderDetail;
import com.orchid.supply.service.SupplyOrderDetailService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.io.Serializable;
import java.util.List;

/**
 * (SupplyOrderDetail)表控制层
 *
 * @author makejava
 * @since 2020-04-10 17:06:07
 */
@RestController
@RequestMapping("supplyOrderDetail")
public class SupplyOrderDetailController extends ApiController {
    /**
     * 服务对象
     */
    @Resource
    private SupplyOrderDetailService supplyOrderDetailService;

    /**
     * 分页查询所有数据
     *
     * @param page 分页对象
     * @param supplyOrderDetail 查询实体
     * @return 所有数据
     */
    @GetMapping
    public R selectAll(Page<SupplyOrderDetail> page, SupplyOrderDetail supplyOrderDetail) {
        return success(this.supplyOrderDetailService.page(page, new QueryWrapper<>(supplyOrderDetail)));
    }

    /**
     * 通过主键查询单条数据
     *
     * @param id 主键
     * @return 单条数据
     */
    @GetMapping("{id}")
    public R selectOne(@PathVariable Serializable id) {
        return success(this.supplyOrderDetailService.getById(id));
    }

    /**
     * 新增数据
     *
     * @param supplyOrderDetail 实体对象
     * @return 新增结果
     */
    @PostMapping
    public R insert(@RequestBody SupplyOrderDetail supplyOrderDetail) {
        return success(this.supplyOrderDetailService.save(supplyOrderDetail));
    }

    /**
     * 修改数据
     *
     * @param supplyOrderDetail 实体对象
     * @return 修改结果
     */
    @PutMapping
    public R update(@RequestBody SupplyOrderDetail supplyOrderDetail) {
        return success(this.supplyOrderDetailService.updateById(supplyOrderDetail));
    }

    /**
     * 删除数据
     *
     * @param idList 主键结合
     * @return 删除结果
     */
    @DeleteMapping
    public R delete(@RequestParam("idList") List<Long> idList) {
        return success(this.supplyOrderDetailService.removeByIds(idList));
    }
}
