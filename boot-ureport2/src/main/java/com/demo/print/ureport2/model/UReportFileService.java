package com.demo.print.ureport2.model;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * @author wuq
 * @Time 2022-7-29 17:09
 * @Description 用于将模板保存到数据库中
 */
@Service
public class UReportFileService {

    @Autowired
    private UReportFileRepository uReportFileRepository;

    public UReportFile findByName(String fileName) {
        return uReportFileRepository.findByName(fileName);
    }

    public List<UReportFile> findAll() {
        return uReportFileRepository.findAll();
    }

    public UReportFile save(String fileName,String content){
        UReportFile uReportFile = new UReportFile();
        uReportFile.setName(fileName);
        uReportFile.setContent(content);
        uReportFile.setCreateTime(new Date());
        return uReportFileRepository.saveAndFlush(uReportFile);
    }

    public void deleteByName(String fileName){
        UReportFile uReportFile = uReportFileRepository.findByName(fileName);
        if (uReportFile != null) {
            uReportFileRepository.delete(uReportFile);
        }
    }

}
