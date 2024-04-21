package cn.lpc.util;

import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.QuadCurve2D;
import java.awt.image.BufferedImage;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Random;


/**
 * 工具类
 * 用于生成验证码
 */
@Component
public class VerifyCode {
    //宽和高
    private int w = 80;
    private int h = 32;

    private Random r = new Random();
    // 定义有那些字体
    private String[] fontNames = { "宋体", "华文楷体", "黑体", "微软雅黑", "楷体_GB2312" };
    // 定义有那些验证码的随机字符
    private String codes = "23456789abcdefghjkmnopqrstuvwxyzABCDEFGHJKLMNOPQRSTUVWXYZ";
    // 生成背景色
    private Color bgColor = new Color(0, 255, 255);
    // 用于gettext 方法 获得生成的验证码文本
    private String text;

    // 生成随机颜色
    private Color randomColor() {
        int red = r.nextInt(255);
        int green = r.nextInt(255);
        int blue = r.nextInt(255);
        return new Color(red, green, blue);
    }

    // 生成随机字体
    private Font randomFont() {
        int index = r.nextInt(fontNames.length);
        String fontName = fontNames[index];
        int style = r.nextInt(4);
        int size = r.nextInt(5) + 24;

        return new Font(fontName, style, size);
    }

    // 画干扰线
    private void drawLine(BufferedImage image) {
        int num = 3; // 增加干扰线数量
        Graphics2D g2 = (Graphics2D) image.getGraphics();

        for (int i = 0; i < num; i++) {
            int x1 = r.nextInt(w);
            int y1 = r.nextInt(h);
            int x2 = r.nextInt(w);
            int y2 = r.nextInt(h);

            // 设置干扰线的粗细和颜色
            g2.setStroke(new BasicStroke(1.0F)); // 设置干扰线粗细为 2.0
            // 白色
            g2.setColor(Color.white);

            // 绘制随机形状的干扰线，这里采用曲线
            QuadCurve2D curve = new QuadCurve2D.Float();
            curve.setCurve(x1, y1, w / 2, h / 2, x2, y2);
            g2.draw(curve);
        }
    }


    // 得到codes的长度内的随机数 并使用charAt 取得随机数位置上的codes中的字符
    private char randomChar() {
        int index = r.nextInt(codes.length());
        return codes.charAt(index);
    }

    // 创建一张验证码的图片
    public BufferedImage createImage() {
        BufferedImage image = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2 = (Graphics2D) image.getGraphics();
        StringBuilder sb = new StringBuilder();
        // 向图中画四个字符
        for (int i = 0; i < 4; i++) {
            String s = randomChar() + "";
            sb.append(s);
            float x = i * 1.0F * w / 4;
            g2.setFont(randomFont());
            g2.setColor(randomColor());
            g2.drawString(s, x, h - 5);

        }
        this.text = sb.toString();
        drawLine(image);

        // 返回图片
        return image;

    }

    // 得到验证码的文本 后面是用来和用户输入的验证码 检测用
    public String getText() {
        return text;
    }

    // 定义输出的对象和输出的方向
    public static void output(BufferedImage bi, OutputStream fos)
            throws FileNotFoundException, IOException {
        ImageIO.write(bi, "JPEG", fos);
    }

}
