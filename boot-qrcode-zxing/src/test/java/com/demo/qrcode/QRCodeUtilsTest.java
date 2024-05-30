package com.demo.qrcode;

import com.google.zxing.*;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class QRCodeUtilsTest {

    //生成二维码
    public static void generateQRCode(String content, int width, int height, String filePath) throws Exception {
        String format = "png";
        Map<EncodeHintType, Object> hints = new HashMap<>();
        hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");   //设置编码
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H); //设置容错等级
        hints.put(EncodeHintType.MARGIN, 1);    // 设置边距

        BitMatrix bitMatrix = new MultiFormatWriter().encode(content, BarcodeFormat.QR_CODE, width, height, hints);
        QRCodeUtilsTest.outputQRCode(bitMatrix, format, filePath);
    }

    private static void outputQRCode(BitMatrix matrix, String format, String filePath) throws Exception {
        MatrixToImageWriter.writeToPath(matrix, format, java.nio.file.Paths.get(filePath));
    }


    //读取二维码
    public static void readQrCode(File file) {
        MultiFormatReader reader = new MultiFormatReader();
        try {
            BufferedImage image = ImageIO.read(file);
            BinaryBitmap binaryBitmap = new BinaryBitmap(new HybridBinarizer(new BufferedImageLuminanceSource(image)));
            Map<DecodeHintType, Object> hints = new HashMap<>();
            hints.put(DecodeHintType.CHARACTER_SET, "utf-8");//设置编码
            Result result = reader.decode(binaryBitmap, hints);
            System.out.println("解析结果:" + result.toString());
            System.out.println("二维码格式:" + result.getBarcodeFormat());
            System.out.println("二维码文本内容:" + result.getText());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws Exception {
        String filePath = "D:\\qrcode.png";
        generateQRCode("https://www.baidu.com", 300, 300, filePath);
        readQrCode(new File(filePath));
    }

}
