package com.demo.emailThymeleaf.config;

import lombok.Data;
import org.springframework.core.io.FileSystemResource;

import java.util.List;

/**
 * mail
 *
 * @author wuq
 * @create 2019-04-06 16:20
 */
@Data
public class Mail {

    private String[] to;    /*收件人列表*/
    private String subject; /*邮件主题*/
    private String text;    /*邮件内容*/
    private List<FileSystemResource> file;    /*附件*/

    // 内容ID，用于发送静态资源邮件时使用，例如：在邮件中嵌入图片
    private String contentId;

    public static Mail getMail() {
        return new Mail();
    }

}
