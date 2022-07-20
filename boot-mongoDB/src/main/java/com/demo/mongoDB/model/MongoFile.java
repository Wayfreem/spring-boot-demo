package com.demo.mongoDB.model;

import lombok.Data;
import org.springframework.data.annotation.Id;

import java.util.Date;

/**
 * @author wuq
 * @Time 2022-7-19 15:31
 * @Description 用于包装转换 MongoDB GridFS 信息
 */
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
