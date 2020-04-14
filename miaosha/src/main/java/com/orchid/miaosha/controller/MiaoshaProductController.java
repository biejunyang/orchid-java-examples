package com.orchid.miaosha.controller;



import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.api.ApiController;
import com.baomidou.mybatisplus.extension.api.R;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.orchid.miaosha.entity.MiaoshaProduct;
import com.orchid.miaosha.service.MiaoshaProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.io.Serializable;
import java.util.List;

/**
 * (MiaoshaProduct)表控制层
 *
 * @author makejava
 * @since 2020-04-14 14:07:50
 */
@RestController
@RequestMapping("miaoshaProduct")
public class MiaoshaProductController extends ApiController {
    /**
     * 服务对象
     */
    @Autowired
    private MiaoshaProductService miaoshaProductService;

    /**
     * 分页查询所有数据
     *
     * @param page 分页对象
     * @param miaoshaProduct 查询实体
     * @return 所有数据
     */
    @GetMapping
    public R selectAll(Page<MiaoshaProduct> page, MiaoshaProduct miaoshaProduct) {
        return success(this.miaoshaProductService.page(page, new QueryWrapper<>(miaoshaProduct)));
    }

    /**
     * 通过主键查询单条数据
     *
     * @param id 主键
     * @return 单条数据
     */
    @GetMapping("{id}")
    public R selectOne(@PathVariable Serializable id) {
        return success(this.miaoshaProductService.getById(id));
    }

    /**
     * 新增数据
     *
     * @param miaoshaProduct 实体对象
     * @return 新增结果
     */
    @PostMapping
    public R insert(@RequestBody MiaoshaProduct miaoshaProduct) {
        return success(this.miaoshaProductService.save(miaoshaProduct));
    }

    /**
     * 修改数据
     *
     * @param miaoshaProduct 实体对象
     * @return 修改结果
     */
    @PutMapping
    public R update(@RequestBody MiaoshaProduct miaoshaProduct) {
        return success(this.miaoshaProductService.updateById(miaoshaProduct));
    }

    /**
     * 删除数据
     *
     * @param idList 主键结合
     * @return 删除结果
     */
    @DeleteMapping
    public R delete(@RequestParam("idList") List<Long> idList) {
        return success(this.miaoshaProductService.removeByIds(idList));
    }
}
