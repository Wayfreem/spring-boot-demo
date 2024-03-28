package com.demo.express.sf.controller;


import com.demo.express.sf.utils.CallExpressApiService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.UnsupportedEncodingException;

@RestController
public class TestController {

    @RequestMapping("/createOrder")
    public String createOrder() throws UnsupportedEncodingException {
        // 下面的orderId  需要修改下，不然会报错
        String msgData = """
                           {
                               "cargoDetails":[
                                   {
                                       "count":2.365,
                                                   "unit":"个",
                                                   "weight":6.1,
                                                   "amount":100.5111,
                                       "currency":"HKD",
                                       "name":"护肤品1",
                                       "sourceArea":"CHN"
                                   }],
                               "contactInfoList":  [
                                   {
                                       "address":"广东省广州市白云区湖北大厦",
                                       "company":"顺丰速运",
                                       "contact":"小邱",
                                       "contactType":2,
                                       "country":"CN",
                                       "postCode":"580058",
                                       "tel":"18688806057"
                                   }
                                   {
                                       "address":"广东省深圳市南山区软件产业基地11栋",
                                       "contact":"小曾",
                                       "contactType":1,
                                       "country":"CN",
                                       "postCode":"580058",
                                       "tel":"4006789888"
                                   }
                                   ],
                               "language":"zh_CN",
                               "orderId":"OrderNum20240323223"
                           }
                """;
        CallExpressApiService.createOrder(msgData);

        return "OK";
    }

    @RequestMapping("/printWayBills")
    public String printWayBills() throws UnsupportedEncodingException {
        String msgData = """
                 {
                      "templateCode": "fm_150_standard_YJ3CB3FX",
                      "version":"2.0",
                      "fileType":"pdf",
                      "sync":true,
                      "documents": [{
                      "masterWaybillNo": "SF7444480501251"
                      }]
                   }
                """;
        CallExpressApiService.printWayBills(msgData);
        return "OK";
    }

    @RequestMapping("/getPDF")
    public String getPDF() throws UnsupportedEncodingException {
        // 这里需要拿着上面 printWayBills 的返回值
        String msgData = """
        
        """;
        CallExpressApiService.printWayBills(msgData);
        return "OK";
    }
}
