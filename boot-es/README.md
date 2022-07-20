资源收集

[使用 docker 安装 ELK](https://blog.csdn.net/qq_18948359/article/details/123252037?spm=1001.2014.3001.5501)

## 简介

通过 spring boot 集成 ElasticSearch，将数据保存到搜索引擎中去，在公司的项目上面是将 日志保存到了 ES 上面。方便与排查日志

## 集成的步骤

### 第一步：增加依赖

Pom 文件

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-elasticsearch</artifactId>
</dependency>
```

### 第二步：创建文档模型
```java
@Data
@Document(indexName = "article")
public class Article {

    @Id
    private Long id;

    @Field(type = FieldType.Text, analyzer = "ik_max_word")
    private String title; //标题

    @Field(type = FieldType.Keyword)
    private String category;// 分类

    @Field(type = FieldType.Keyword)
    private String brand; // 品牌

    @Field(type = FieldType.Double)
    private Double price; // 价格

    @Field(index = false, type = FieldType.Keyword)
    private String images; // 图片地址l;

    public Article() {
    }

    public Article(Long id, String title, String category, String brand, Double price, String images) {
        this.id = id;
        this.title = title;
        this.category = category;
        this.brand = brand;
        this.price = price;
        this.images = images;
    }

    @Override
    public String toString() {
        return "Item [id=" + id + ", title=" + title + ", category=" + category + ", brand=" + brand + ", price="
                + price + ", images=" + images + "]";
    }
}
```

#### 第三步：创建 repository
```java
public interface ArticleRepository extends ElasticsearchRepository<Article, Long> {
}
```

#### 第四步：创建 controller 以及 service

ArticleController

```java
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
```

ArticleService

```java
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
```

