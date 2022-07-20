package com.demo.es.controller;

import com.demo.es.model.Article;
import com.demo.es.service.ArticleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@Slf4j
public class ArticleController {

    @Autowired
    private ArticleService articleService;

    @RequestMapping("/save")
    public String createUser() {
        log.info("=================保存数据===============");
        articleService.insert();
        return "OK";
    }

    @RequestMapping("/findAll")
    public Iterable<Article> findAll(){
        return articleService.findAll();
    }

}
