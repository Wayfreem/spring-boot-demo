package com.demo.es.model;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * @Author wuq
 * @Date 2022-2-15
 */
public interface ArticleRepository extends ElasticsearchRepository<Article, Long> {
}
