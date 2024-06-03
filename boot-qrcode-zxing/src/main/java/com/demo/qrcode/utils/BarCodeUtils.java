package com.demo.qrcode.utils;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Objects;

@Component
public class BarCodeUtils {

    /**
     * 条形码宽度
     */
    private static final int WIDTH = 200;

    /**
     * 条形码高度
     */
    private static final int HEIGHT = 50;


    /**
     * 生成条形码，并加文字，以流的方式返回
     *
     * @param content     内容
     * @param barCodeWord 二维码的文字
     * @return ByteArrayOutputStream
     */
    public ByteArrayOutputStream barcodeGenerator(String content, String barCodeWord) {
        // 设置条形码参数
        HashMap<EncodeHintType, Object> hints = new HashMap<>();
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.L); // 设置纠错级别为L（低）
        hints.put(EncodeHintType.CHARACTER_SET, "UTF-8"); // 设置字符编码为UTF-8

        try {
            // 生成条形码的矩阵
            BitMatrix matrix = new MultiFormatWriter().encode(content, BarcodeFormat.CODE_128, WIDTH, HEIGHT, hints);
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            BufferedImage bufferedImage = MatrixToImageWriter.toBufferedImage(matrix);
            //底部加单号
            BufferedImage image = this.insertWords(bufferedImage, barCodeWord);
            if (Objects.isNull(image)) {
                throw new RuntimeException("条形码加文字失败");
            }
            ImageIO.write(image, "png", outputStream);
            return outputStream;
        } catch (WriterException | IOException e) {
            throw new RuntimeException("条形码生成失败", e);
        }
    }

    private BufferedImage insertWords(BufferedImage image, String words) {
        // 新的图片，把带logo的二维码下面加上文字
        if (StringUtils.hasLength(words)) {
            BufferedImage outImage = new BufferedImage(WIDTH, HEIGHT + 20, BufferedImage.TYPE_INT_RGB);
            Graphics2D g2d = outImage.createGraphics();
            // 抗锯齿
            this.setGraphics2D(g2d);
            // 设置白色
            this.setColorWhite(g2d);
            // 画条形码到新的面板
            g2d.drawImage(image, 0, 0, image.getWidth(), image.getHeight(), null);
            // 画文字到新的面板
            Color color = new Color(0, 0, 0);
            g2d.setColor(color);
            // 字体、字型、字号
            g2d.setFont(new Font("微软雅黑", Font.PLAIN, 16));
            //文字长度
            int strWidth = g2d.getFontMetrics().stringWidth(words);
            //总长度减去文字长度的一半  （居中显示）
            int wordStartX = (WIDTH - strWidth) / 2;
            //height + (outImage.getHeight() - height) / 2 + 12
            int wordStartY = HEIGHT + 20;
            // time 文字长度

            // 画文字
            g2d.drawString(words, wordStartX, wordStartY);
            g2d.dispose();
            outImage.flush();
            return outImage;
        }
        return null;
    }

    /**
     * 设置 Graphics2D 属性  （抗锯齿）
     *
     * @param g2d Graphics2D提供对几何形状、坐标转换、颜色管理和文本布局更为复杂的控制
     */
    private void setGraphics2D(Graphics2D g2d) {
        // 消除画图锯齿
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        // 消除文字锯齿
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_DEFAULT);
        Stroke s = new BasicStroke(1, BasicStroke.CAP_ROUND, BasicStroke.JOIN_MITER);
        g2d.setStroke(s);
    }

    private void setColorWhite(Graphics2D g2d) {
        g2d.setColor(Color.WHITE);
        //填充整个屏幕
        g2d.fillRect(0, 0, WIDTH, HEIGHT + 20);
        //设置笔刷
        g2d.setColor(Color.BLACK);
    }

}
