package com.demo.print.ureport2.model;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author wuq
 * @Time 2022-7-29 17:09
 * @Description
 */
public interface UReportFileRepository extends JpaRepository<UReportFile, Long> {

    UReportFile findByName(String name);
}
