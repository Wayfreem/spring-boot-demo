package com.demo.emailThymeleaf.service;

import com.demo.emailThymeleaf.config.EmailUtils;
import com.demo.emailThymeleaf.config.Mail;
import com.demo.emailThymeleaf.model.StudentVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class TestService {

    @Autowired
    private EmailUtils emailUtils;

    @Autowired
    private TemplateEngine templateEngine;

    public void sendTemplateEmail(){
        Mail mail = Mail.getMail();
        mail.setSubject("SpringBoot集成JavaMail实现邮件发送");

        String[] toList = {"xxxx@163.com"};
        mail.setTo(toList);

        // 发送模板邮件
        Context context = new Context();
        context.setVariables(getMap()); // 设置模板数据
        //获取thymeleaf的html模板
        String emailContent = templateEngine.process("student", context);

        // 设置发送邮件的内容
        mail.setText(emailContent);

        emailUtils.sendMailHtml(mail);
    }


    private Map<String, Object> getMap(){
        Map<String,Object> map = new HashMap<>();
        map.put("username","All");
        map.put("year", LocalDate.now());
        map.put("datalist",getStudentInfo());
        return map;
    }

    private List<StudentVo> getStudentInfo() {
        List<StudentVo> lists = new ArrayList<StudentVo>();
        StudentVo vo1 = new StudentVo();
        vo1.setAge(14);
        vo1.setCard("20210001");
        vo1.setName("赵小明");
        vo1.setScore(686);
        vo1.setOrder(1);
        vo1.setSex("1");
        lists.add(vo1);

        StudentVo vo2 = new StudentVo();
        vo2.setAge(15);
        vo2.setCard("20210002");
        vo2.setName("王小花");
        vo2.setScore(677);
        vo2.setOrder(2);
        vo2.setSex("2");
        lists.add(vo2);


        StudentVo vo3 = new StudentVo();
        vo3.setAge(14);
        vo3.setCard("20210003");
        vo3.setName("张丽丽");
        vo3.setScore(675);
        vo3.setOrder(3);
        vo3.setSex("2");
        lists.add(vo3);
        return lists;
    }

}
