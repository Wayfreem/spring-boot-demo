package com.demo.qrcode.controller;

import com.demo.qrcode.utils.QrCodeUtils;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.imageio.ImageIO;
import javax.imageio.stream.ImageOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

@RestController
@RequestMapping(path = "/qrcode")
public class QrCodeController {

    // http://localhost:8080/qrcode/create?content=www.baidu.com
    @GetMapping(path = "/createQrCode")
    public void createQrCode(HttpServletResponse response, @RequestParam("content") String content) {
        try {
            // 创建二维码
            BufferedImage bufferedImage = QrCodeUtils.createImage(content, null, false);

            // 通过流的方式返回给前端
            responseImage(response, bufferedImage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 设置 可通过 postman 或者浏览器直接浏览
     *
     * @param response      response
     * @param bufferedImage bufferedImage
     * @throws Exception e
     */
    public void responseImage(HttpServletResponse response, BufferedImage bufferedImage) throws Exception {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ImageOutputStream imageOutput = ImageIO.createImageOutputStream(byteArrayOutputStream);
        ImageIO.write(bufferedImage, "jpeg", imageOutput);
        InputStream inputStream = new ByteArrayInputStream(byteArrayOutputStream.toByteArray());

        OutputStream outputStream = response.getOutputStream();
        response.setContentType("image/jpeg");
        response.setCharacterEncoding("UTF-8");
        IOUtils.copy(inputStream, outputStream);
        outputStream.flush();
    }
}
