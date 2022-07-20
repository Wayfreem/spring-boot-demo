package com.demo.mongoDB.controller;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.demo.mongoDB.model.MongoFile;
import com.demo.mongoDB.service.GridFsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.FastByteArrayOutputStream;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.time.LocalDateTime;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * @author wuq
 * @Time 2022-7-19 15:14
 * @Description 用于操作 GridFs
 */
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
     * 获取文件
     *
     * @param id
     * @return
     */
    @GetMapping("/files/{id}")
    @ResponseBody
    public ResponseEntity<Object> serveFile(@PathVariable String id) throws Exception {
        MongoFile file = gridFsService.getFileById(id);
        if (file != null) {
            return ResponseEntity
                    .ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; fileName=\"" + URLEncoder.encode(file.getName(), "UTF-8") + "\"")
                    .header(HttpHeaders.CONTENT_TYPE, "application/octet-stream")
                    .header(HttpHeaders.CONTENT_LENGTH, file.getSize() + "")
                    .header("Connection", "close")
                    .body(file.getContent());
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("File was not fount !");
        }
    }

    /**
     * 在线显示文件
     *
     * @param id 对应 MongoDB 中 fs.files 中的 ID MD5 码值
     * @return ResponseEntity<Object>
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

    /**
     * 打包下载多个文件
     *
     * @param body [{"id":""}]
     * @return ResponseEntity<Object>
     */
    @PostMapping(value = "/download", produces = {"text/html;charset=utf-8"})
    @ResponseBody
    public ResponseEntity<Object> downFilesToZip(@RequestParam("body") String body) throws Exception {
        JSONArray fileMetes;
        try {
            fileMetes = JSON.parseArray(body);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("请求输入参数格式错误！".getBytes("UTF-8"));
        }

        try (FastByteArrayOutputStream out = new FastByteArrayOutputStream()) {
            filesToZip(fileMetes, out);
            return ResponseEntity
                    .ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; fileName=\"" + LocalDateTime.now() + "\"")
                    .header(HttpHeaders.CONTENT_TYPE, "application/octet-stream")
                    .header(HttpHeaders.CONTENT_LENGTH, out.size() + "")
                    .header("Connection", "close")
                    .body(out.toByteArray());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(("批量文件下载失败！" + e.getMessage()).getBytes("UTF-8"));
        }
    }

    /**
     * 多个文件压缩成zip
     *
     * @param fileMetes 文件列表
     * @param out       输出流
     */
    private void filesToZip(JSONArray fileMetes, OutputStream out) {
        try (ZipOutputStream zos = new ZipOutputStream(out)) {
//            zos.setEncoding("GBK");  17 上面没有这个方法了
            for (Object fileMete : fileMetes) {
                MongoFile file = gridFsService.getFileById(((JSONObject) fileMete).getString("id"));
                if (file == null) {
                    continue;
                }
                zos.putNextEntry(new ZipEntry(file.getName()));
                zos.write(file.getContent());
                zos.closeEntry();
            }
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }
}
