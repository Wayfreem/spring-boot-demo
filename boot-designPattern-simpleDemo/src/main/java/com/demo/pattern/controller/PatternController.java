package com.demo.pattern.controller;

import com.demo.pattern.model.SaleOrder;
import com.demo.pattern.service.PatternService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class PatternController {

    @Autowired
    private PatternService patternService;

    @PostMapping("test")
    public Map test(@RequestBody SaleOrder saleOrder) {
        return patternService.test(saleOrder);
    }
}
