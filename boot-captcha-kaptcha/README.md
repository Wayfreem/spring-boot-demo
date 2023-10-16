## 说明

kaptcha 是谷歌开源的简单实用的验证码生成工具。

## 项目搭建

### 项目路径

具体的项目路径就直接看下源码结构吧

### 第一步: 引入依赖包

在查看其他人写的博客的时候发现有好多人并不是用的这个 `pro.fessional` groupId 下面的包。这个可以去 [maven repository 官网上面查看下](https://mvnrepository.com/)

```xml
<dependency>
    <groupId>pro.fessional</groupId>
    <artifactId>kaptcha</artifactId>
    <version>2.3.3</version>
</dependency>
```

### 第二步：增加配置文件

这里增加配置文件是为了方便于切换验证码格式

```properties
captcha.type=char
```

### 第三步：增加配置类

Kaptcha 是支持两种方式的验证码一种是计算类的，一种就是常规的方式。

注意在 `captchaProducerMath` 中验证码文本生成器的源码路径是需要修改下的，这里需要换成自己的工程路径

```java
@Configuration
public class KaptchaConfig {

    @Bean(name = "captchaProducer")
    public DefaultKaptcha getKaptchaBean() {
        DefaultKaptcha defaultKaptcha = new DefaultKaptcha();
        Properties properties = new Properties();
        // 是否有边框 默认为true 我们可以自己设置yes，no
        properties.setProperty(KAPTCHA_BORDER, "yes");
        // 验证码文本字符颜色 默认为Color.BLACK
        properties.setProperty(KAPTCHA_TEXTPRODUCER_FONT_COLOR, "black");
        // 验证码图片宽度 默认为200
        properties.setProperty(KAPTCHA_IMAGE_WIDTH, "160");
        // 验证码图片高度 默认为50
        properties.setProperty(KAPTCHA_IMAGE_HEIGHT, "60");
        // 验证码文本字符大小 默认为40
        properties.setProperty(KAPTCHA_TEXTPRODUCER_FONT_SIZE, "38");
        // KAPTCHA_SESSION_KEY
        properties.setProperty(KAPTCHA_SESSION_CONFIG_KEY, "kaptchaCode");
        // 验证码文本字符长度 默认为5
        properties.setProperty(KAPTCHA_TEXTPRODUCER_CHAR_LENGTH, "4");
        // 验证码文本字体样式 默认为new Font("Arial", 1, fontSize), new Font("Courier", 1, fontSize)
        properties.setProperty(KAPTCHA_TEXTPRODUCER_FONT_NAMES, "Arial,Courier");
        // 图片样式 水纹com.google.code.kaptcha.impl.WaterRipple 鱼眼com.google.code.kaptcha.impl.FishEyeGimpy 阴影com.google.code.kaptcha.impl.ShadowGimpy
        properties.setProperty(KAPTCHA_OBSCURIFICATOR_IMPL, "com.google.code.kaptcha.impl.ShadowGimpy");
        Config config = new Config(properties);
        defaultKaptcha.setConfig(config);
        return defaultKaptcha;
    }

    @Bean(name = "captchaProducerMath")
    public DefaultKaptcha getKaptchaBeanMath() {
        DefaultKaptcha defaultKaptcha = new DefaultKaptcha();
        Properties properties = new Properties();
        // 是否有边框 默认为true 我们可以自己设置yes，no
        properties.setProperty(KAPTCHA_BORDER, "yes");
        // 边框颜色 默认为Color.BLACK
        properties.setProperty(KAPTCHA_BORDER_COLOR, "105,179,90");
        // 验证码文本字符颜色 默认为Color.BLACK
        properties.setProperty(KAPTCHA_TEXTPRODUCER_FONT_COLOR, "blue");
        // 验证码图片宽度 默认为200
        properties.setProperty(KAPTCHA_IMAGE_WIDTH, "160");
        // 验证码图片高度 默认为50
        properties.setProperty(KAPTCHA_IMAGE_HEIGHT, "60");
        // 验证码文本字符大小 默认为40
        properties.setProperty(KAPTCHA_TEXTPRODUCER_FONT_SIZE, "35");
        // KAPTCHA_SESSION_KEY
        properties.setProperty(KAPTCHA_SESSION_CONFIG_KEY, "kaptchaCodeMath");
        // 验证码文本生成器
        properties.setProperty(KAPTCHA_TEXTPRODUCER_IMPL, "com.demo.kaptcha.config.KaptchaTextCreator");
        // 验证码文本字符间距 默认为2
        properties.setProperty(KAPTCHA_TEXTPRODUCER_CHAR_SPACE, "3");
        // 验证码文本字符长度 默认为5
        properties.setProperty(KAPTCHA_TEXTPRODUCER_CHAR_LENGTH, "6");
        // 验证码文本字体样式 默认为new Font("Arial", 1, fontSize), new Font("Courier", 1, fontSize)
        properties.setProperty(KAPTCHA_TEXTPRODUCER_FONT_NAMES, "Arial,Courier");
        // 验证码噪点颜色 默认为Color.BLACK
        properties.setProperty(KAPTCHA_NOISE_COLOR, "white");
        // 干扰实现类
        properties.setProperty(KAPTCHA_NOISE_IMPL, "com.google.code.kaptcha.impl.NoNoise");
        // 图片样式 水纹 com.google.code.kaptcha.impl.WaterRipple 鱼眼 com.google.code.kaptcha.impl.FishEyeGimpy 阴影 com.google.code.kaptcha.impl.ShadowGimpy
        properties.setProperty(KAPTCHA_OBSCURIFICATOR_IMPL, "com.google.code.kaptcha.impl.ShadowGimpy");
        Config config = new Config(properties);
        defaultKaptcha.setConfig(config);
        return defaultKaptcha;
    }
}
```

### 第四步：增加验证码文本生成器

```java
public class KaptchaTextCreator extends DefaultTextCreator {
    private static final String[] CNUMBERS = "0,1,2,3,4,5,6,7,8,9,10".split(",");

    @Override
    public String getText() {
        Integer result = 0;
        Random random = new Random();
        int x = random.nextInt(10);
        int y = random.nextInt(10);
        StringBuilder suChinese = new StringBuilder();
        int randomOperands = random.nextInt(3);
        if (randomOperands == 0) {
            result = x * y;
            suChinese.append(CNUMBERS[x]);
            suChinese.append("*");
            suChinese.append(CNUMBERS[y]);
        } else if (randomOperands == 1) {
            if ((x != 0) && y % x == 0) {
                result = y / x;
                suChinese.append(CNUMBERS[y]);
                suChinese.append("/");
                suChinese.append(CNUMBERS[x]);
            } else {
                result = x + y;
                suChinese.append(CNUMBERS[x]);
                suChinese.append("+");
                suChinese.append(CNUMBERS[y]);
            }
        } else {
            if (x >= y) {
                result = x - y;
                suChinese.append(CNUMBERS[x]);
                suChinese.append("-");
                suChinese.append(CNUMBERS[y]);
            } else {
                result = y - x;
                suChinese.append(CNUMBERS[y]);
                suChinese.append("-");
                suChinese.append(CNUMBERS[x]);
            }
        }
        suChinese.append("=?@" + result);
        return suChinese.toString();
    }
}
```

### 第五步： 增加获取验证码的工具类

这里增加一个工具类是为解耦，避免在调用 http 请求的时候直接直接使用下面的代码，

```java
@Component
public class KaptchaUtil {

    @Value("${captcha.type}")
    private String captchaType;

    @Resource(name = "captchaProducer")
    private Producer captchaProducer;

    @Resource(name = "captchaProducerMath")
    private Producer captchaProducerMath;

    /**
     * 获取验证码
     * @return Map {"code": "进行验证的值", "img": "base64 格式的图片"}
     */
    public Map getCaptcha() {

        // code 是我们需要验证的值，可以放到 缓存 中方便于验证
        String capStr = null, code = null;
        BufferedImage image = null;

        if ("math".equals(captchaType)) {
            String capText = captchaProducerMath.createText();
            capStr = capText.substring(0, capText.lastIndexOf("@"));
            code = capText.substring(capText.lastIndexOf("@") + 1);
            image = captchaProducerMath.createImage(capStr);
        } else if ("char".equals(captchaType)) {
            capStr = code = captchaProducer.createText();
            image = captchaProducer.createImage(capStr);
        }

        // 转换流信息写出
        FastByteArrayOutputStream os = new FastByteArrayOutputStream();
        try {
            ImageIO.write(image, "jpg", os);
        } catch (IOException e) {
            throw new RuntimeException("获取验证码报错：" + e.getMessage());
        }

        Map resultMap = new HashMap();
        resultMap.put("code", code);
        resultMap.put("img", Base64.encode(os.toByteArray()));
        return resultMap;
    }
}
```

另外说明一点，由于 `Base64` 在当前的这个 jdk 版本中没有了，所以我偷下懒直接复制了一份出来，具体直接见源码。

这里可以看下这个博客：[Base64的替换方案](https://blog.csdn.net/qq_16977023/article/details/103769231)

### 第六步： 增加 Controller 用于访问

由于我们增加一个了工具类了，所以 controller 写的就会比较简洁，如下：

```java
@RestController
public class CaptchaController {

    @Autowired
    private KaptchaUtil kaptchaUtil;


    @GetMapping("getCaptchaImage")
    public Map getCode(HttpServletRequest request) {
        return kaptchaUtil.getCaptcha();
    }
}
```

## 测试

通过浏览器访问 http://localhost:8080/getCaptchaImage 会得到 base64 的值，然后将 base64 的值放入到 `static\index.html` 中的 `img标签`中，通过浏览器访问就好了。
