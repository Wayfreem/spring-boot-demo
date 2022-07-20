package com.demo.es.service;

import com.demo.es.model.Article;
import com.demo.es.model.ArticleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class ArticleService {

    @Autowired
    private ArticleRepository articleRepository;

    public void insert(){
        Article item1 = new Article(1L, "小米手机7", " 手机", "小米", 3499.00, "http://image.baidu.com/13123.jpg");
        articleRepository.save(item1);

        Article item2 = new Article(2L, "苹果XSMax", " 手机", "苹果", 3499.00, "http://image.baidu.com/13123.jpg");
        articleRepository.save(item2);
    }

    public Iterable<Article> findAll(){
        return articleRepository.findAll();
    }

}
