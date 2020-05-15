package com.orchid.supply.controller;



import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.api.ApiController;
import com.baomidou.mybatisplus.extension.api.R;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.orchid.supply.entity.Supplier;
import com.orchid.supply.service.SupplierService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.io.Serializable;
import java.util.List;

/**
 * (Supplier)表控制层
 *
 * @author makejava
 * @since 2020-04-10 17:06:07
 */
@RestController
@RequestMapping("supplier")
public class SupplierController extends ApiController {
    /**
     * 服务对象
     */
    @Resource
    private SupplierService supplierService;

    /**
     * 分页查询所有数据
     *
     * @param page 分页对象
     * @param supplier 查询实体
     * @return 所有数据
     */
    @GetMapping
    public R selectAll(Page<Supplier> page, Supplier supplier) {
        return success(this.supplierService.page(page, new QueryWrapper<>(supplier)));
    }

    /**
     * 通过主键查询单条数据
     *
     * @param id 主键
     * @return 单条数据
     */
    @GetMapping("{id}")
    public R selectOne(@PathVariable Serializable id) {
        return success(this.supplierService.getById(id));
    }

    /**
     * 新增数据
     *
     * @param supplier 实体对象
     * @return 新增结果
     */
    @PostMapping
    public R insert(@RequestBody Supplier supplier) {
        return success(this.supplierService.save(supplier));
    }

    /**
     * 修改数据
     *
     * @param supplier 实体对象
     * @return 修改结果
     */
    @PutMapping
    public R update(@RequestBody Supplier supplier) {
        return success(this.supplierService.updateById(supplier));
    }

    /**
     * 删除数据
     *
     * @param idList 主键结合
     * @return 删除结果
     */
    @DeleteMapping
    public R delete(@RequestParam("idList") List<Long> idList) {
        return success(this.supplierService.removeByIds(idList));
    }
}