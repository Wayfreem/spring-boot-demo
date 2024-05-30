package com.demo.qrcode;

import com.google.zxing.*;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.HybridBinarizer;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.Map;

public class BarCodeUtilsTest {

    /**
     * generateCode 根据code生成相应的一维码
     *
     * @param file   一维码目标文件
     * @param code   一维码内容
     * @param width  图片宽度
     * @param height 图片高度
     */
    public static void generateCode(File file, String code, int width, int height) {
        //定义位图矩阵BitMatrix
        BitMatrix matrix = null;
        try {
            // 使用code_128格式进行编码生成100*25的条形码
            MultiFormatWriter writer = new MultiFormatWriter();

            Map<EncodeHintType, Object> hints = new HashMap<>();
            hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");

            //定义编码参数 这里可以设置条形码的格式
            matrix = writer.encode(code, BarcodeFormat.CODE_128, width, height, hints);
            //matrix = writer.encode(code,BarcodeFormat.EAN_13, width, height, hints);
        } catch (WriterException e) {
            e.printStackTrace();
        }

        //将位图矩阵BitMatrix保存为图片
        try (FileOutputStream outStream = new FileOutputStream(file)) {
            assert matrix != null;
            ImageIO.write(MatrixToImageWriter.toBufferedImage(matrix), "png", outStream);
            outStream.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * readCode 读取一张一维码图片
     *
     * @param file 一维码图片名字或者是文件路径
     */
    public static void readCode(File file) {
        try {
            BufferedImage image = ImageIO.read(file);
            if (image == null) {
                return;
            }
            LuminanceSource source = new BufferedImageLuminanceSource(image);
            BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));

            Map<DecodeHintType, Object> hints = new HashMap<>();
            hints.put(DecodeHintType.CHARACTER_SET, "UTF-8");
            hints.put(DecodeHintType.PURE_BARCODE, Boolean.TRUE);
            hints.put(DecodeHintType.TRY_HARDER, Boolean.TRUE);

            Result result = new MultiFormatReader().decode(bitmap, hints);
            System.out.println("条形码内容: " + result.getText());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws Exception {
        generateCode(new File("D:\\barcode.png"), "123456789012", 500, 250);
        readCode(new File("D:\\barcode.png"));
    }

}
