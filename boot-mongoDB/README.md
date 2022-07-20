[Spring 官方地址](https://docs.spring.io/spring-data/mongodb/docs/current/reference/html/#mongodb-getting-started)

## 简介

Spring boot 集成 MongoDB 的例子

## 集成的步骤

### 使用 MongoRepository 操作MongoDB

#### 第一步：增加依赖
增加对应的 MongoDB 依赖包
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-mongodb</artifactId>
</dependency>
```

#### 第二步：增加配置文件
```properties
spring.data.mongodb.username=admin
spring.data.mongodb.password=123456
spring.data.mongodb.host=192.168.152.129
spring.data.mongodb.port=27017
spring.data.mongodb.database=admin

# MongoDB logging MongoDB 日志输出
logging.level.org.springframework.data.mongodb.core=DEBUG
```

#### 第三步：增加对应的模型实体类以及 repository

这里我们新建一个模型实体类，使用JPA 的 repository 接口来进行 MongoDB 的访问

```java
@Data
@Document(collation = "test")   // 这里指定对应的集合名称
public class User implements Serializable {

    @Id
    private String id;

    private String userId;

    private String fileName;

    public User(String id, String userId, String fileName) {
        this.id = id;
        this.userId = userId;
        this.fileName = fileName;
    }
}
```
创建 repository 

```java
public interface UserRepository extends MongoRepository<User, String> {
}
```

#### 第四步：创建 controller 以及 service 用于测试
TestController

```java
@RestController
public class TestController {

    @Autowired
    UserService userService;

    @RequestMapping("save")
    public User save() {
        return userService.save();
    }

    @RequestMapping("findById")
    public User findById(String id) {
        return userService.findById(id);
    }
}
```

UserService
```java
@Service
public class UserService {

    @Autowired
    UserRepository userRepository;

    public User save(){
        return userRepository.save(new User("00001", "temp01", "java"));
    }

    public User findById(String id){
        return userRepository.findById(id).get();
    }
}
```

### 使用 MongoTemplate 操作 MongoDB

```java
@Service
public class UserService {
    
    @Autowired
    MongoTemplate mongoTemplate;

    public List<Document> findAll(){
        // 经过测试这里不能返回 iterable，如果直接返回 iterable， 是获取不到值的
        FindIterable<Document> iterable = mongoTemplate.getCollection("user").find();
        MongoCursor<Document> cursor =  iterable.iterator();
        List<Document> result = new ArrayList<>();
        while (cursor.hasNext()){
            Document document = cursor.next();
            result.add(document);
        }
        return result;
    }
}
```

### 使用 MongoDB 的 GridFS

GridFS存储文件是将文件分块存储，文件会按照256KB的大小分割成多个块进行存储。

GridFS使用两个集合（collection）存储文件，一个集合是chunks, 用于存储文件的二进制数据；一个集合是files，用于存储文件的元数据信息（文件名称、块大小、上传时间等信息）。

**使用 GridFsTemplate 操作 GridFs**, GridFsTemplate是Spring提供的专门操作GridFs的客户端，提供了一系列开箱即用的方法。

#### 第一步：创建一个Bean 文件，用于对 MongoDB 中 GridFsFile文件类型的的对照（转换）
```java
@Data
public class MongoFile {

    @Id  // 主键
    private String id;
    private String name; // 文件名称
    private String contentType; // 文件类型
    private long size; // 文件大小
    private Date uploadDate; // 上传时间
    private String md5; // 文件md5值
    private byte[] content; // 文件内容
    private String path; // 文件路径
    private int status = 0; //文件状态（0：临时；1：有效）

    public MongoFile(){
    }

    public MongoFile(String name, String contentType, long size, byte[] content) {
        this.name = name;
        this.contentType = contentType;
        this.size = size;
        this.uploadDate = new Date();
        this.content = content;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object == null || getClass() != object.getClass()) {
            return false;
        }
        MongoFile fileInfo = (MongoFile) object;
        return java.util.Objects.equals(size, fileInfo.size)
                && java.util.Objects.equals(name, fileInfo.name)
                && java.util.Objects.equals(contentType, fileInfo.contentType)
                && java.util.Objects.equals(uploadDate, fileInfo.uploadDate)
                && java.util.Objects.equals(md5, fileInfo.md5)
                && java.util.Objects.equals(id, fileInfo.id);
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(name, contentType, size, uploadDate, md5, id);
    }

    @Override
    public String toString() {
        return "File{"
                + "name='" + name + '\''
                + ", contentType='" + contentType + '\''
                + ", size=" + size
                + ", uploadDate=" + uploadDate
                + ", md5='" + md5 + '\''
                + ", id='" + id + '\''
                + '}';
    }
}
```

#### 第二步：创建一个 Controller，用于访问
```java
@CrossOrigin(origins = "*", maxAge = 3600)  // 允许所有域名访问
@RestController
public class GridFsController {

    @Autowired
    private GridFsService gridFsService;

    @Value("${fileserver.upload.allow:image/gif,image/jpeg,image/png,application/pdf}")
    private String uploadAllow;     // 控制文件上传的类型

    @RequestMapping("upload")
    @ResponseBody
    public ResponseEntity<String> upload(@RequestParam("files") MultipartFile[] files) {
        try {
            List result = new ArrayList();
            for (MultipartFile file : files) {
                if (!uploadAllow.contains(Objects.requireNonNull(file.getContentType()))) {
                    throw new RuntimeException("系统禁止：" + file.getContentType() + " 类型的文件上传！");
                }
            }

            for (MultipartFile file : files) {
                MongoFile f = new MongoFile(file.getOriginalFilename(), file.getContentType(), file.getSize(), file.getBytes());

                MongoFile returnFile = gridFsService.saveFile(f);      //  核心的保存方法
                result.add(Map.of("id", returnFile.getId(),
                        "type", returnFile.getContentType(),
                        "size", returnFile.getSize(),
                        "name", returnFile.getName(),
                        "fileName", returnFile.getName()));
            }
            return ResponseEntity.status(HttpStatus.OK).body(result.toString());
        } catch (IOException ex) {
            ex.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
        }
    }

    @GetMapping("/delete/{id}")
    @ResponseBody
    public ResponseEntity<String> deleteFile(@PathVariable String id) {
        try {
            gridFsService.delete(id);
            return ResponseEntity.status(HttpStatus.OK).body("Delete Success!");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    /**
     * 在线显示文件
     *
     * @param id
     * @return
     */
    @GetMapping("/view/{id}")
    @ResponseBody
    public ResponseEntity<Object> serveFileOnline(@PathVariable String id) throws Exception {
        MongoFile file = gridFsService.getFileById(id);
        if (file != null) {
            return ResponseEntity
                    .ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "fileName=\"" + URLEncoder.encode(file.getName(), "UTF-8") + "\"")
                    .header(HttpHeaders.CONTENT_TYPE, file.getContentType())
                    .header(HttpHeaders.CONTENT_LENGTH, file.getSize() + "")
                    .header("Connection", "close")
                    .body(file.getContent());
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("File was not fount !");
        }
    }
}
```

#### 第四步：对应的 service

```java
@Service
public class GridFsService {

    @Autowired
    private GridFsTemplate gridFsTemplate;

    public MongoFile saveFile(MongoFile file) {
        Document document = new Document("status", file.getStatus());
        // 数据保存到 MongoDB 中
        ObjectId id = gridFsTemplate.store(new ByteArrayInputStream(file.getContent()), file.getName(), file.getContentType(), document);
        return findOne(id.toHexString());
    }

    public MongoFile findOne(String id) {
        GridFSFile file = findById(id);
        return toFile(file);
    }

    public MongoFile getFileById(String id) {
        GridFSFile file = findById(id);
        return toFile(file);
    }

    /**
     * 根据 ID 查询对应的文件
     *
     * @param id fileId
     * @return GridFSFile
     */
    private GridFSFile findById(String id) {
        Query query;
        try {
            query = Query.query(Criteria.where("_id").is(new ObjectId(id)));
        } catch (Exception e) {
            throw new RuntimeException("文件：" + id + " 在服务器中不存在！");
        }
        return gridFsTemplate.findOne(query);
    }
    
    public void delete(String id) {
        Query query = Query.query(Criteria.where("_id").is(new ObjectId(id)));
        gridFsTemplate.delete(query);
    }

    /**
     * 将 MongoBD 中的 GridFSFile 转为 MongoFile
     *
     * @param gridFile GridFSFile
     * @return MongoFile
     */
    private MongoFile toFile(GridFSFile gridFile) {
        try {
            if (gridFile == null) return null;
            GridFsResource resource = gridFsTemplate.getResource(gridFile);
            ByteArrayOutputStream os = writeTo(resource.getInputStream());
            MongoFile file = new MongoFile(resource.getFilename(), resource.getContentType(), resource.contentLength(), os.toByteArray());

            file.setId((gridFile.getObjectId()).toHexString());
            file.setUploadDate(gridFile.getUploadDate());
            Document document = gridFile.getMetadata() != null ? gridFile.getMetadata() : null;
            if (document != null) {
                file.setStatus((Integer) document.get("status"));
            }
            return file;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private ByteArrayOutputStream writeTo(InputStream in) throws Exception {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        int ch;
        while ((ch = in.read()) != -1) {
            os.write(ch);
        }
        return os;
    }

}
```

#### 第五步：创建一个页面，用于测试文件提交
```html
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>文件上传</title>
</head>
<body>

<form action="http://127.0.0.1:8080/upload" method="post" enctype="multipart/form-data">

    <div>
        <label for="value">文件名路径：</label>
        <input id="value" name="value" type="text" placeholder="请输入文件路径"/>
    </div>

    <div style="margin-top: 20px">
        <img src="http://127.0.0.1:8080/view/62d67b971c1a322e613fb7ba">
    </div>

    <div style="margin-top: 20px">
        <input type="file" name="files" />
    </div>
    <div style="margin-top: 20px">
        <input type="submit" value="提交"/>
    </div>
</form>

</body>
</html>
```