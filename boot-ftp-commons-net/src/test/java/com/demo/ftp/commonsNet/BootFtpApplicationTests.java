package com.demo.ftp.commonsNet;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

@SpringBootTest
public class BootFtpApplicationTests {

    @Autowired
    private FTPClientService ftpClientService;


    @Test
    public void test1() {
        try {
            InputStream inputStream = new FileInputStream(new File("F:\\test\\img\\1.jpg"));
            ftpClientService.upload(inputStream, "004.jpg", "2023");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
