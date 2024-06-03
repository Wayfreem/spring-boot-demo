## 使用 zxing 生成二维码与条形码

### 引入依赖

```xml
<dependency>
    <groupId>com.google.zxing</groupId>
    <artifactId>core</artifactId>
    <version>3.4.1</version>
</dependency>

<dependency>
    <groupId>com.google.zxing</groupId>
    <artifactId>javase</artifactId>
    <version>3.4.1</version>
</dependency>
```

### 生成二维码

```java
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

public class QRCodeUtils {

    //生成二维码
    public static void generateQRCode(String content, int width, int height, String filePath) throws Exception {
        String format = "png";
        Map<EncodeHintType, Object> hints = new HashMap<>();
        hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");   //设置编码
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H); //设置容错等级
        hints.put(EncodeHintType.MARGIN, 1);    // 设置边距

        BitMatrix bitMatrix = new MultiFormatWriter().encode(content, BarcodeFormat.QR_CODE, width, height, hints);
        QRCodeUtils.outputQRCode(bitMatrix, format, filePath);
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
```

### 生成条形码

```java
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

public class BarCodeUtils {

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
```

## 返回给前端展示

由于我们现在都是使用的是 web 项目，生成完的条形码以及二维码都需要返回给前端展示，这里我们看下怎么做：

### 生成二维码
对应的 `controller` 
```java
@RestController
@RequestMapping(path = "/qrcode")
public class QrCodeController {

    // http://localhost:8080/qrcode/create?content=www.baidu.com
    @GetMapping(path = "/createQrCode")
    public void createQrCode(HttpServletResponse response, @RequestParam("content") String content) {
        try {
            // 创建二维码
            BufferedImage bufferedImage = QrCodeUtils.createImage(content, null, false);

            // 通过流的方式返回给前端
            responseImage(response, bufferedImage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 设置 可通过 postman 或者浏览器直接浏览
     *
     * @param response      response
     * @param bufferedImage bufferedImage
     * @throws Exception e
     */
    public void responseImage(HttpServletResponse response, BufferedImage bufferedImage) throws Exception {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ImageOutputStream imageOutput = ImageIO.createImageOutputStream(byteArrayOutputStream);
        ImageIO.write(bufferedImage, "jpeg", imageOutput);
        InputStream inputStream = new ByteArrayInputStream(byteArrayOutputStream.toByteArray());

        OutputStream outputStream = response.getOutputStream();
        response.setContentType("image/jpeg");
        response.setCharacterEncoding("UTF-8");
        IOUtils.copy(inputStream, outputStream);
        outputStream.flush();
    }
}
```


对应的 工具类 `QrCodeUtils`
```java
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
```



### 生成条形码
对应的 `controller`
```java
@RestController
@RequestMapping(path = "/barcode")
public class BarCodeController {

    @Autowired
    BarCodeUtils barCodeUtils;

    // http://localhost:8080/barcode/createCode?content=987654132&barCodeWord=123456789
    @GetMapping(path = "/createCode")
    public void createQrCode(HttpServletResponse response, @RequestParam("content") String content, @RequestParam("content") String barCodeWord) {
        try {
            // 创建二维码
            ByteArrayOutputStream byteArrayOutputStream = barCodeUtils.barcodeGenerator(content, barCodeWord);

            // 通过流的方式返回给前端
            InputStream inputStream = new ByteArrayInputStream(byteArrayOutputStream.toByteArray());

            OutputStream outputStream = response.getOutputStream();
            response.setContentType("image/jpeg");
            response.setCharacterEncoding("UTF-8");
            IOUtils.copy(inputStream, outputStream);
            outputStream.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
```


对应的 工具类 `BarCodeUtils`
```java
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
```
