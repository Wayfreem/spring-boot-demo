package com.demo.ftp.commonsNet.controller;

import com.demo.ftp.commonsNet.service.FTPClientService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * @author wuq
 * @create 2019-12-05 18:02
 */
@CrossOrigin(origins = "*", maxAge = 3600)
@Controller
@Slf4j
public class FileUploadController {

    @Autowired
    private FTPClientService ftpClientService;

    /**
     * 多文件上传
     * @param file 前端传入一个文件列表
     * @return map
     */
    @RequestMapping("upload")
    @ResponseBody
    public Map<String, Object> upload(@RequestParam("files") MultipartFile[] file) {
        Map<String, Object> returnMap = new HashMap<>();
        if (file.length == 0) {
            log.error("上传文件为空");
            returnMap.put("msg", "上传文件为空");
            returnMap.put("code", "error");
            return returnMap;
        }

        for (MultipartFile uploadFile : file  ) {
            String fileName = uploadFile.getName();
            String fileOriginalName = uploadFile.getOriginalFilename();

            try {
                InputStream inputStream = uploadFile.getInputStream();
                ftpClientService.upload(inputStream, fileOriginalName, fileName);
            } catch (IOException e) {
                e.printStackTrace();
                log.error("文件传换位输入流报错！");
            }
        }

        returnMap.put("msg", "上传文件上传成功");
        returnMap.put("code", "success");
        return returnMap;
    }

    @RequestMapping("uploadWithPath")
    public Map<String, Object> upload(@RequestParam("file") MultipartFile file,
                                      @RequestParam(value="value",required=false) String value){
        if (file.isEmpty()) {
            log.error("上传文件为空");
            return null;
        }
        String fileName = file.getName();
        String fileOriginalName = file.getOriginalFilename();
        return null;
    }

}
