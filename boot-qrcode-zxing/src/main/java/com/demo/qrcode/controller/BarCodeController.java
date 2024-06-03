package com.demo.qrcode.controller;

import com.demo.qrcode.utils.BarCodeUtils;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

@RestController
@RequestMapping(path = "/barcode")
public class BarCodeController {

    @Autowired
    BarCodeUtils barCodeUtils;

    // http://localhost:8080/barcode/createCode?content=987654132&barCodeWord=123456789
    @GetMapping(path = "/createCode")
    public void createQrCode(HttpServletResponse response, @RequestParam("content") String content, @RequestParam("content") String barCodeWord) {
        try {
            // 创建二维码
            ByteArrayOutputStream byteArrayOutputStream = barCodeUtils.barcodeGenerator(content, barCodeWord);

            // 通过流的方式返回给前端
            InputStream inputStream = new ByteArrayInputStream(byteArrayOutputStream.toByteArray());

            OutputStream outputStream = response.getOutputStream();
            response.setContentType("image/jpeg");
            response.setCharacterEncoding("UTF-8");
            IOUtils.copy(inputStream, outputStream);
            outputStream.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
