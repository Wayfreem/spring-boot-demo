package com.demo.express.sf.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.sf.csim.express.service.CallExpressServiceTools;
import com.sf.csim.express.service.HttpClientUtil;
import com.sf.csim.express.service.IServiceCodeStandard;
import com.sf.csim.express.service.code.ExpressServiceCodeEnum;
import org.apache.commons.io.IOUtils;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;


public class CallExpressApiService {

    private static final String CLIENT_CODE = "";  //此处替换为您在丰桥平台获取的顾客编码
    private static final String CHECK_WORD = "";//此处替换为您在丰桥平台获取的校验码


    //沙箱环境的地址 -PRO
    private static final String CALL_URL_BOX = "https://sfapi-sbox.sf-express.com/std/service";
    //生产环境的地址 -PRO
    private static final String CALL_URL_PROD = "https://sfapi.sf-express.com/std/service";


    /**
     * 调用参数
     * <pre>
     *     String msgData = "{" +
     *                 "    "cargoDetails":[" +
     *                 "        {          " +
     *                 "            "count":2.365," +
     *                 "             "unit":"个"," +
     *                 "             "weight":6.1," +
     *                 "             "amount":100.5111," +
     *                 "            "currency":"HKD"," +
     *                 "            "name":"护肤品1",           " +
     *                 "            "sourceArea":"CHN"          " +
     *                 "        }]," +
     *                 "    "contactInfoList":[" +
     *                 "        {" +
     *                 "            "address":"广东省深圳市南山区软件产业基地11栋"," +
     *                 "            "contact":"小曾"," +
     *                 "            "contactType":1," +
     *                 "            "country":"CN"," +
     *                 "            "postCode":"580058"," +
     *                 "            "tel":"4006789888"" +
     *                 "        }," +
     *                 "        {" +
     *                 "            "address":"广东省广州市白云区湖北大厦"," +
     *                 "            "company":"顺丰速运"," +
     *                 "            "contact":"小邱"," +
     *                 "            "contactType":2," +
     *                 "            "country":"CN"," +
     *                 "            "postCode":"580058"," +
     *                 "            "tel":"18688806057"" +
     *                 "        }]," +
     *                 "    "language":"zh_CN"," +
     *                 "    "orderId":"OrderNum20240612223"" +
     *                 "}";
     * </pre>
     * @param msgData
     * @return
     * @throws UnsupportedEncodingException
     */
    public static String createOrder(String msgData) throws UnsupportedEncodingException {
        IServiceCodeStandard standardService = ExpressServiceCodeEnum.EXP_RECE_CREATE_ORDER; //下订单

        CallExpressServiceTools tools = CallExpressServiceTools.getInstance();

        // set common header
        Map<String, String> params = new HashMap<>();

        String timeStamp = String.valueOf(System.currentTimeMillis());

        params.put("partnerID", CLIENT_CODE);  // 顾客编码 ，对应丰桥上获取的clientCode
        params.put("requestID", UUID.randomUUID().toString().replace("-", ""));
        params.put("serviceCode", standardService.getCode());// 接口服务码
        params.put("timestamp", timeStamp);
        params.put("msgData", msgData);
        params.put("msgDigest", tools.getMsgDigest(msgData, timeStamp, CHECK_WORD));


        System.out.println("====调用实际请求：" + params);
        String result = HttpClientUtil.post(CALL_URL_BOX, params);

        System.out.println("====调用丰桥的接口服务代码：" + standardService.getCode() + "====");
        System.out.println("===调用地址 ===" + CALL_URL_BOX);
        System.out.println("===顾客编码 ===" + CLIENT_CODE);
        System.out.println("===返回结果：" + result);

        return result;
    }


    /**
     * 调用参数
     * <pre>
     *  {
     *     "templateCode": "fm_150_standard_YJ3CB3FX",
     *     "version":"2.0",
     *     "fileType":"pdf",
     *     "sync":true,
     *     "documents": [{
     *     "masterWaybillNo": "SF7444480501251"
     *     }]
     *  }
     *</pre>
     *  返回结果
     *
     *  <pre>
     *   {"apiErrorMsg":"","apiResponseID":"00018E79F0AAEE3FE755F4D40823D73F","apiResultCode":"A1000","apiResultData":"{\"obj\":{\"clientCode\":\"YJ3CB3FX\",\"fileType\":\"pdf\",\"files\":[{\"areaNo\":1,\"documentSize\":0,\"pageCount\":0,\"pageNo\":1,\"seqNo\":1,\"token\":\"AUTH_tkv12_f146d1855480549d262b5c46ab0ab597ff20a97d9d0db45c16bedeb4fabd112b012deadd477ee524b1d690ce01baa3cdffbb125a6ccf69b73778dba2eb5157eb73eb03e946a2c01352db378fe2bdea7c95c535a186cf195dc290be8fb7d1e7064e80fa12c5e7757aff35d31ff59b7f55832b73ef3f6a4397c071ef11cba0f8623abd7a376adcd85a3c8e3e8c9b64f903a7d5c55353003625d76f23480fd915464d767f73ba97048cd4aef655f4d970ba\",\"url\":\"https://eos-scp-core-shenzhen-futian1-oss.sf-express.com:443/v1.2/AUTH_EOS-SCP-CORE/print-file-sbox/AAABjnnwq4I40xFIfoVMAJQtjYTrUht8_SF7444480501251_fm_150_standard_YJ3CB3FX_1_1.pdf\",\"waybillNo\":\"SF7444480501251\"}],\"templateCode\":\"fm_150_standard_YJ3CB3FX\"},\"requestId\":\"9c772eb846124800a15edcaf9e0cfea4\",\"success\":true}"}
     *  </pre>
     * @param msgData
     */
    public static void printWayBills(String msgData) throws UnsupportedEncodingException {
        IServiceCodeStandard standardService = ExpressServiceCodeEnum.COM_RECE_CLOUD_PRINT_WAYBILLS; //云打印面单打印2.0接口

        CallExpressServiceTools tools = CallExpressServiceTools.getInstance();

        // set common header
        Map<String, String> params = new HashMap<>();

        String timeStamp = String.valueOf(System.currentTimeMillis());

        params.put("partnerID", CLIENT_CODE);  // 顾客编码 ，对应丰桥上获取的clientCode
        params.put("requestID", UUID.randomUUID().toString().replace("-", ""));
        params.put("serviceCode", standardService.getCode());// 接口服务码
        params.put("timestamp", timeStamp);
        params.put("msgData", msgData);
        params.put("msgDigest", tools.getMsgDigest(msgData, timeStamp, CHECK_WORD));


        System.out.println("====调用实际请求：" + params);
        String result = HttpClientUtil.post(CALL_URL_BOX, params);

        System.out.println("====调用丰桥的接口服务代码：" + standardService.getCode() + "====");
        System.out.println("===调用地址 ===" + CALL_URL_BOX);
        System.out.println("===顾客编码 ===" + CLIENT_CODE);
        System.out.println("===返回结果：" + result);
    }


    public static void getPDF(String result){
        JSONObject resultJson = JSON.parseObject(result).getJSONObject("apiResultData").getJSONObject("obj");
        JSONObject fileInfo = resultJson.getJSONArray("files").getJSONObject(0);

        try {
            URL noodleUrl = new URL(fileInfo.getString("url"));
            URLConnection connection = noodleUrl.openConnection(); //创建连接
            //设置请求头（下载文件时需要的token,设置在请求头的 X-Auth-token 字段，有效期 24h）
            connection.setRequestProperty("X-Auth-token", fileInfo.getString("token"));

            try ( InputStream in = connection.getInputStream()){
                File outputFile = new File("D:\\test\\"+resultJson.getString("templateCode")+".pdf");
                try (FileOutputStream outputStream = new FileOutputStream(outputFile)) {
                    IOUtils.copy(in, outputStream);
                    System.out.println("内容已成功写入到文件中。");
                }
            }

        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public static void main(String[] args) throws UnsupportedEncodingException {
        String msgData = "{\"apiErrorMsg\":\"\",\"apiResponseID\":\"00018E79F0AAEE3FE755F4D40823D73F\",\"apiResultCode\":\"A1000\",\"apiResultData\":\"{\\\"obj\\\":{\\\"clientCode\\\":\\\"YJ3CB3FX\\\",\\\"fileType\\\":\\\"pdf\\\",\\\"files\\\":[{\\\"areaNo\\\":1,\\\"documentSize\\\":0,\\\"pageCount\\\":0,\\\"pageNo\\\":1,\\\"seqNo\\\":1,\\\"token\\\":\\\"AUTH_tkv12_f146d1855480549d262b5c46ab0ab597ff20a97d9d0db45c16bedeb4fabd112b012deadd477ee524b1d690ce01baa3cdffbb125a6ccf69b73778dba2eb5157eb73eb03e946a2c01352db378fe2bdea7c95c535a186cf195dc290be8fb7d1e7064e80fa12c5e7757aff35d31ff59b7f55832b73ef3f6a4397c071ef11cba0f8623abd7a376adcd85a3c8e3e8c9b64f903a7d5c55353003625d76f23480fd915464d767f73ba97048cd4aef655f4d970ba\\\",\\\"url\\\":\\\"https://eos-scp-core-shenzhen-futian1-oss.sf-express.com:443/v1.2/AUTH_EOS-SCP-CORE/print-file-sbox/AAABjnnwq4I40xFIfoVMAJQtjYTrUht8_SF7444480501251_fm_150_standard_YJ3CB3FX_1_1.pdf\\\",\\\"waybillNo\\\":\\\"SF7444480501251\\\"}],\\\"templateCode\\\":\\\"fm_150_standard_YJ3CB3FX\\\"},\\\"requestId\\\":\\\"9c772eb846124800a15edcaf9e0cfea4\\\",\\\"success\\\":true}\"}";
        getPDF(msgData);
    }
}
