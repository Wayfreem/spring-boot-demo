package com.demo.email.controller;

import com.demo.email.config.EmailUtils;
import com.demo.email.config.Mail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @author wuq
 * @Time 2022-7-5 19:27
 * @Description
 */
@RestController
public class EmailController {

    @Autowired
    EmailUtils emailUtils;

    /**
     * 发送简易邮件
     * 这里只发送一个主体和具体的内容
     */
    @RequestMapping("sendText")
    public void contextLoads() {
        Mail mail = Mail.getMail();
        mail.setSubject("SpringBoot集成JavaMail实现邮件发送");
        mail.setText("SpringBoot集成JavaMail实现简易邮件发送功能");

        String[] toList = {"wayfreem@163.com"};     // 接收方列表
        mail.setTo(toList);
        emailUtils.sendMail(mail);
    }

    /**
     * 发送的内容为 HTML
     */
    @RequestMapping("sendHTML")
    public void sendHTML(){
        Mail mail = Mail.getMail();
        mail.setSubject("SpringBoot集成JavaMail实现邮件发送");

        String[] toList = {"wayfreem@163.com"};
        mail.setTo(toList);

        String content = "<!DOCTYPE html>\n" +
                "<html>\n" +
                "<body>\n" +
                "\t<h3>这是由 spring boot 集成 mail 发送的 html 邮件</h3>\n" +
                "</body>\n" +
                "</html>";
        mail.setText(content);

        emailUtils.sendMailHtml(mail);
    }

    /**
     * 增加附件发送
     */
    public void sendAttarchment(){
        Mail mail = Mail.getMail();
        mail.setSubject("SpringBoot集成JavaMail实现邮件发送");
        mail.setText("SpringBoot集成JavaMail实现简易邮件发送功能");

        String[] toList = {"wayfreem@163.com"};
        mail.setTo(toList);

        List<FileSystemResource> list = new ArrayList<>();
        list.add(new FileSystemResource(new File("E:\\test\\img\\150A01.png")));
        list.add(new FileSystemResource(new File("E:\\test\\img\\150A01.png")));
        mail.setFile(list);

        emailUtils.sendMailAttachment(mail);
    }

    /**
     * 发送图片
     */
    public void sendImg(){
        Mail mail = Mail.getMail();
        mail.setSubject("SpringBoot集成JavaMail实现邮件发送");
        String[] toList = {"wayfreem@163.com"};
        mail.setTo(toList);

        // 设置图片的地址名称
        String contentId = "0001";

        String content = "<!DOCTYPE html>\n" +
                "<html>\n" +
                "<body>\n" +
                "\t<h3>这是由 spring boot 集成 mail 发送的 html 邮件</h3>\n" +
                " <div> <img src=\'cid:"+contentId+"\' /> </div> " +
                "</body>\n" +
                "</html>";
        mail.setText(content);
        mail.setContentId(contentId);

        // 添加附件
        List<FileSystemResource> list = new ArrayList();
        list.add(new FileSystemResource(new File("E:\\test\\img\\150A01.png")));
        mail.setFile(list);

        emailUtils.sendMailInline(mail);
    }
    
}
