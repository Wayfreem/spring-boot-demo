package com.demo.print.ureport2.model;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author wuq
 * @Time 2022-7-29 17:09
 * @Description 用于将模板保存到数据库中
 */
@Service
public class UReportFileService {

    @Autowired
    private UReportFileRepository uReportFileRepository;


    public UReportFile save(){

        return null;
    }

    public void delete(){

    }


}
