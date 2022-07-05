package com.demo.email.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

@Component
public class EmailUtils {

    @Autowired
    private JavaMailSender mailSender;  // 自动注入bean

    @Value("${spring.mail.username}")
    private String sender;  // 读取配置文件中的参数


    /**
     * 发送简易邮件
     *
     * @param mail
     */
    public void sendMail(Mail mail) {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage);

        try {
            helper.setFrom(sender);
            helper.setTo(mail.getTo());
            helper.setSubject(mail.getSubject());
            helper.setText(mail.getText());

            mailSender.send(mimeMessage);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    /**
     * 发送邮件-邮件正文是HTML
     *
     * @param mail
     */
    public void sendMailHtml(Mail mail) {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage);

        try {
            helper.setFrom(sender);
            helper.setTo(mail.getTo());
            helper.setSubject(mail.getSubject());
            helper.setText(mail.getText(), true);

            mailSender.send(mimeMessage);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    /**
     * 发送邮件-附件邮件
     *
     * @param mail
     */
    public void sendMailAttachment(Mail mail) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
            helper.setFrom(sender);
            helper.setTo(mail.getTo());
            helper.setSubject(mail.getSubject());
            helper.setText(mail.getText(), true);

            // 增加附件名称和附件
            for (FileSystemResource file : mail.getFile()) {
                helper.addAttachment(file.getFilename(), file);
            }
            mailSender.send(mimeMessage);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    /**
     * 内联资源（静态资源）邮件发送
     * 由于邮件服务商不同，可能有些邮件并不支持内联资源的展示
     * 在测试过程中，新浪邮件不支持，QQ邮件支持
     * 不支持不意味着邮件发送不成功，而且内联资源在邮箱内无法正确加载
     * 发送的内容格式还是 html 模式
     *
     * @param mail
     */
    public void sendMailInline(Mail mail) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
            helper.setFrom(sender);
            helper.setTo(mail.getTo());
            helper.setSubject(mail.getSubject());

            /*
             * 内联资源邮件需要确保先设置邮件正文，再设置内联资源相关信息
             */

            // todo 只是测试发送一个图片
            helper.setText(mail.getText(), true);
            helper.addInline(mail.getContentId(), mail.getFile().get(0));

            mailSender.send(mimeMessage);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    /**
     * 模板邮件发送
     *
     * @param mailBean
     */
    public void sendMailTemplate(Mail mailBean) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
            helper.setFrom(sender);
            helper.setTo(mailBean.getTo());
            helper.setSubject(mailBean.getSubject());
            helper.setText(mailBean.getText(), true);
            mailSender.send(mimeMessage);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

}
