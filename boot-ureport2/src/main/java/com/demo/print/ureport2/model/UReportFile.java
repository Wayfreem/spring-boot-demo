package com.demo.print.ureport2.model;

import lombok.Data;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.SelectBeforeUpdate;

import javax.persistence.*;
import java.util.Date;

/**
 * @author wuq
 * @Time 2022-7-29 16:24
 * @Description 打印模板表
 */
@Data
@Entity
@SelectBeforeUpdate
@DynamicInsert
@DynamicUpdate
@Table(name = "U_Report_File")
public class UReportFile {

    @Id
    @Column(name = "id", nullable = false, length = 16)
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "name", length = 64)
    private String name;    // 模板名称

    @Lob
    @Basic(fetch = FetchType.LAZY)
    @Column(name = "content")
    private String content; // 模板内容，由于在 ReportProvider 中保存方法传递的是 String，这里就用String

    private Date createTime;
}
