
[文档地址](https://blog.csdn.net/qq_18948359/article/details/103635325?spm=1001.2014.3001.5501)

## 简介

Spring Email 抽象的核心是 MailSender 接口，MailSender 的实现能够把 Email 发送给邮件服务器，由邮件服务器实现邮件发送的功能。

Spring 自带了一个 MailSender 的实现 JavaMailSenderImpl，它会使用 JavaMail API 来发送 Email。Spring 或 SpringBoot 应用在发送 Email 之前，我们必须要 JavaMailSenderImpl 装配为 Spring应用上下文的一个 bean。

## 集成的步骤

### 第一步：增加依赖

**pom文件**

```xml
<dependency>
     <groupId>org.springframework.boot</groupId>
     <artifactId>spring-boot-starter-mail</artifactId>
</dependency>
 
<!-- 我使用了lombok  -->
<dependency>
    <groupId>org.projectlombok</groupId>
    <artifactId>lombok</artifactId>
    <scope>provided</scope>
</dependency>
```

### 第二步：配置文件

```properties
# QQ 邮箱 的host 为 smtp.qq.com, 网易的为 smtp.163.com
spring.mail.host=smtp.qq.com
spring.mail.username=532548957@qq.com
spring.mail.password= 这里输入自己的编码
spring.mail.default-encoding=utf-8
```

### 第三步：创建核心的类

新建一个用于邮件发送的 Mail Bean

```java
import lombok.Data;
import org.springframework.core.io.FileSystemResource;
import java.util.List;
@Data
public class Mail {
 
    private String[] to;    /*收件人列表*/
    private String subject; /*邮件主题*/
    private String text;    /*邮件内容*/
   private List<FileSystemResource> file;    /*附件*/
 
    /**
     * 内容ID，用于发送静态资源邮件时使用
     */
    private String contentId;
 
    public static Mail getMail() {
        return new Mail();
    }
 
}
```

创建一个工具类 EmailUtils

```java
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
 
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
 
/**
 * 发送邮件工具
 *
 * @author wuq
 * @create 2019-04-06 15:24
 */
@Component
public class EmailUtils {
 
    @Autowired
    private JavaMailSender mailSender; // 自动注入的Bean
 
    @Value("${spring.mail.username}")
    private String sender; // 读取配置文件中的参数
 
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
```

## 测试

具体看下 controller 中的代码