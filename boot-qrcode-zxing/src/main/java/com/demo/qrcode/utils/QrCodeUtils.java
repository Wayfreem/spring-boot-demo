package com.demo.qrcode.utils;

import com.google.zxing.*;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Hashtable;


/**
 * 生成二维码工具类
 */
@Component
public class QrCodeUtils {

    private static final String CHARSET = "UTF-8";
    private static final String FORMAT_NAME = "JPG";

    /**
     * 二维码尺寸
     */
    private static final int QRCODE_SIZE = 300;

    /**
     * LOGO宽度
     */
    private static final int WIDTH = 60;

    /**
     * LOGO高度
     */
    private static final int HEIGHT = 60;

    /**
     * 创建二维码图片
     *
     * @param content    内容
     * @param logoPath   logo
     * @param isCompress 是否压缩Logo
     * @return 返回二维码图片
     * @throws WriterException e
     * @throws IOException     BufferedImage
     */
    public static BufferedImage createImage(String content, String logoPath, boolean isCompress) throws WriterException, IOException {
        Hashtable<EncodeHintType, Object> hints = new Hashtable<>();
        // 设置二维码的错误纠正级别 高
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
        // 设置字符集
        hints.put(EncodeHintType.CHARACTER_SET, CHARSET);
        // 设置边距
        hints.put(EncodeHintType.MARGIN, 1);
        // 生成二维码
        BitMatrix bitMatrix = new MultiFormatWriter().encode(content, BarcodeFormat.QR_CODE, QRCODE_SIZE, QRCODE_SIZE, hints);
        int width = bitMatrix.getWidth();
        int height = bitMatrix.getHeight();
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                image.setRGB(x, y, bitMatrix.get(x, y) ? 0xFF000000 : 0xFFFFFFFF);
            }
        }
        if (logoPath == null || "".equals(logoPath)) {
            return image;
        }

        // 在二维码中增加 logo
        QrCodeUtils.insertImage(image, logoPath, isCompress);
        return image;
    }

    /**
     * 添加Logo
     *
     * @param source     二维码图片
     * @param logoPath   Logo
     * @param isCompress 是否压缩Logo
     * @throws IOException void
     */
    private static void insertImage(BufferedImage source, String logoPath, boolean isCompress) throws IOException {
        File file = new File(logoPath);
        if (!file.exists()) {
            return;
        }

        Image src = ImageIO.read(new File(logoPath));
        int width = src.getWidth(null);
        int height = src.getHeight(null);
        // 压缩LOGO
        if (isCompress) {
            if (width > WIDTH) {
                width = WIDTH;
            }

            if (height > HEIGHT) {
                height = HEIGHT;
            }

            Image image = src.getScaledInstance(width, height, Image.SCALE_SMOOTH);
            BufferedImage tag = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            Graphics g = tag.getGraphics();
            // 绘制缩小后的图
            g.drawImage(image, 0, 0, null);
            g.dispose();
            src = image;
        }

        // 插入LOGO
        Graphics2D graph = source.createGraphics();
        int x = (QRCODE_SIZE - width) / 2;
        int y = (QRCODE_SIZE - height) / 2;
        graph.drawImage(src, x, y, width, height, null);
        Shape shape = new RoundRectangle2D.Float(x, y, width, width, 6, 6);
        graph.setStroke(new BasicStroke(3f));
        graph.draw(shape);
        graph.dispose();
    }

    /**
     * 生成带Logo的二维码
     *
     * @param content    二维码内容
     * @param logoPath   Logo
     * @param destPath   二维码输出路径
     * @param isCompress 是否压缩Logo
     * @throws Exception void
     */
    public static void create(String content, String logoPath, String destPath, boolean isCompress) throws Exception {
        BufferedImage image = QrCodeUtils.createImage(content, logoPath, isCompress);
        mkdirs(destPath);
        ImageIO.write(image, FORMAT_NAME, new File(destPath));
    }

    /**
     * 生成不带Logo的二维码
     *
     * @param content  二维码内容
     * @param destPath 二维码输出路径
     */
    public static void create(String content, String destPath) throws Exception {
        QrCodeUtils.create(content, null, destPath, false);
    }

    /**
     * 生成带Logo的二维码，并输出到指定的输出流
     *
     * @param content    二维码内容
     * @param logoPath   Logo
     * @param output     输出流
     * @param isCompress 是否压缩Logo
     */
    public static void create(String content, String logoPath, OutputStream output, boolean isCompress) throws Exception {
        BufferedImage image = QrCodeUtils.createImage(content, logoPath, isCompress);
        ImageIO.write(image, FORMAT_NAME, output);
    }

    /**
     * 生成不带Logo的二维码，并输出到指定的输出流
     *
     * @param content 二维码内容
     * @param output  输出流
     * @throws Exception void
     */
    public static void create(String content, OutputStream output) throws Exception {
        QrCodeUtils.create(content, null, output, false);
    }

    /**
     * 二维码解析
     *
     * @param file 二维码
     * @return 返回解析得到的二维码内容
     * @throws Exception String
     */
    public static String parse(File file) throws Exception {
        BufferedImage image;
        image = ImageIO.read(file);
        if (image == null) {
            return null;
        }
        BufferedImageLuminanceSource source = new BufferedImageLuminanceSource(image);
        BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
        Result result;
        Hashtable<DecodeHintType, Object> hints = new Hashtable<DecodeHintType, Object>();
        hints.put(DecodeHintType.CHARACTER_SET, CHARSET);
        result = new MultiFormatReader().decode(bitmap, hints);
        return result.getText();
    }

    /**
     * 二维码解析
     *
     * @param path 二维码存储位置
     * @return 返回解析得到的二维码内容
     * @throws Exception String
     */
    public static String parse(String path) throws Exception {
        return QrCodeUtils.parse(new File(path));
    }

    /**
     * 判断路径是否存在，如果不存在则创建
     *
     * @param dir 目录
     */
    public static void mkdirs(String dir) {
        if (dir != null && !"".equals(dir)) {
            File file = new File(dir);
            if (!file.isDirectory()) {
                file.mkdirs();
            }
        }
    }
}
