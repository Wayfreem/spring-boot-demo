package com.demo.swagger.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@Api(tags = "SwaggerController")
@RestController
public class SwaggerController {

    @ApiOperation(value="保存用户", notes="根据传入的值，保存")
//    @ApiImplicitParams({@ApiImplicitParam(name = "User", value = "员工信息")})
//    @ApiImplicitParam(name = "User", value = "用户详细实体user", required = true, dataTypeClass = User.class)
    @RequestMapping("/save")
    public User save(User user) {
        return user;
    }

    @ApiOperation(value = "查询单个接口", notes = "根据url的name来获取用户详细信息", response = User.class)
//    @ApiImplicitParams({@ApiImplicitParam(name = "name", value = "员工名称")})
    @ApiImplicitParam(name = "name", value = "用户名称", required = true, dataType = "String", paramType = "path", dataTypeClass = String.class)
    @RequestMapping("/findByName")
    public User findByName(String name) {
        System.out.println(name);
        return new User();
    }

    @ApiOperation(value="查询所有接口", notes="根据url的name来获取用户详细信息")
//    @ApiImplicitParams({@ApiImplicitParam(name = "name", value = "员工名称")})
    @ApiImplicitParam(name = "name", value = "用户名称", required = true, dataType = "String", paramType = "path", dataTypeClass = String.class)
    @RequestMapping("/findAll")
    public List<User> findAll(String name) {
        return new ArrayList<>();
    }
}
