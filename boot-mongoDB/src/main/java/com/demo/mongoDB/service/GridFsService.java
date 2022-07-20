package com.demo.mongoDB.service;

import com.demo.mongoDB.model.MongoFile;
import com.mongodb.client.gridfs.model.GridFSFile;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

/**
 * @author wuq
 * @Time 2022-7-19 15:28
 * @Description 核心使用 GridFsTemplate 操作 GridFS
 */
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
