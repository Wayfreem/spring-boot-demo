package com.demo.print.ureport2.extend;

import com.bstek.ureport.exception.ReportException;
import com.bstek.ureport.provider.report.ReportFile;
import com.bstek.ureport.provider.report.ReportProvider;
import com.demo.print.ureport2.model.UReportFile;
import com.demo.print.ureport2.model.UReportFileService;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class SqlReportProvider implements ReportProvider {

    @Autowired
    private UReportFileService reportService;

    // 增加头部信息
    private String prefix = "report:";

    @Override
    public String getName() {
        // 返回存储器的名称
        return "报表模板保存到数据库";
    }

    @Override
    public boolean disabled() {
        // 返回是否禁用, 默认为非禁用, 不需要的打印模板就删除掉
        return false;
    }

    @Override
    public String getPrefix() {
        return prefix;
    }

    /**
     * 根据报表名加载报表文件
     * @param fileName 报表名称
     * @return
     */
    @Override
    public InputStream loadReport(String fileName) {
        try {
            UReportFile uReportFile= reportService.findByName(removePrefix(fileName));
            if (uReportFile==null) return null;
            return IOUtils.toInputStream(uReportFile.getContent(),"utf-8");
        } catch (Exception e) {
            throw new ReportException(e);
        }
    }

    @Override
    public void deleteReport(String fileName) {
        reportService.deleteByName(fileName);
    }

    @Override
    public List<ReportFile> getReportFiles() {
        List<UReportFile> uReportFiles = reportService.findAll();
        return uReportFiles.stream()
                .map( uReportFile -> new ReportFile(getPrefix()+uReportFile.getContent(), uReportFile.getCreateTime()))
                .collect(Collectors.toList());
    }

    /**
     * 保存打印模板
     * @param fileName 报表名称
     * @param content 报表的XML内容
     */
    @Override
    public void saveReport(String fileName, String content) {
        reportService.save(fileName, content);
    }

    private String removePrefix(String file){
        return file.replace("report:","");
    }
}
