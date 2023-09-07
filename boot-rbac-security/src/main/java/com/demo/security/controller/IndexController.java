package com.demo.security.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
//@Secured({"ROLE_管理员","ROLE_普通用户"})
public class IndexController {

    @GetMapping("index")
    @ResponseBody
//    @PostAuthorize("hasAnyAuthority('menu:system')")
    public String index() {
        System.out.println("1111111111111");
        return "success";
    }

    @PostMapping("fail")
    @ResponseBody
    public String fail() {
        return "fail";
    }

    @GetMapping("findAll")
    @ResponseBody
    public String findAll() {
        return "findAll";
    }

    @GetMapping("find")
    @ResponseBody
    public String find() {
        return "find";
    }

}
