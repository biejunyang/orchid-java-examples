package com.orchid.springboot.swagger.controller;

import io.swagger.annotations.*;
import org.springframework.web.bind.annotation.*;


/**
 * @Api：修饰类，描述修改
 * @ApiOperation：修改方法，具体的某个接口
 */
@Api(tags = "用户信息接口")
@RestController
public class UserController {



    /**
     * paramType参数类型：
     *      query：查询参数
     *      form：表单参数
     * @param name
     * @param age
     * @return
     */
    @ApiOperation(value = "用户分页列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "name", value = "用户名", dataType = "String"),
            @ApiImplicitParam(name = "age", value = "年龄", dataType = "int")
    })
    @GetMapping("/user/page")
    public String list(String name, Integer age, Page page){
        return "<h1>hello, "+name+","+age+"</h1>";
    }


    @ApiOperation(("获取用户列表"))
    @GetMapping("/user/list")
    public String list(User user){
        return user.getName()+","+user.getAge();
    }

    @ApiOperation("获取用户信息")
    @GetMapping("/user/{id}")
    public String get(@PathVariable("id") Integer id){
        return id.toString();
    }

    @ApiOperation("添加用户")
    @PostMapping("/user")
    public String add(@RequestBody User user){
        return user.getName()+","+user.getAge();
    }

    @ApiOperation(("更新用户"))
    @PutMapping("/user/{id}")
    public String update(@PathVariable("id") Integer id, @RequestBody User user){
        return user.getName()+","+user.getAge();
    }

    @ApiOperation(("删除用户"))
    @DeleteMapping("/user/{id}")
    public String delete(@PathVariable("id") Integer id){
        return "delete"+id;
    }

}
