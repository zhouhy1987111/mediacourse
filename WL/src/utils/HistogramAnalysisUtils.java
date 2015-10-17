package utils;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Created with IntelliJ IDEA.
 * User: ZHY
 * Date: 15-10-16
 * Time: 下午10:51
 *
 强度直方图图形化显示不同的像素值在不同的强度值上的出现频率，对于灰度图像来说强度
 范围为[0~255]之间，对于RGB的彩色图像可以独立显示三种颜色的强度直方图。强度直方
 图是用来寻找灰度图像二值化阈值常用而且是有效的手段之一，如果一幅灰度图像的直方图
 显示为两个波峰，则二值化阈值应该是这两个波峰之间的某个灰度值。同时强度直方图是调
 整图像对比度的重要依据

 直方图实现方法：
 对一幅灰度图像从上到下，从左到右扫描每个像素值，在每个灰度值上计算像素数目，以这
 些数据为基础完成图像直方图的绘制。

 程序实现：
 1.      首先对一幅RGB图像完成灰度转换，转换代码如下：
 2.      初始化直方图数据数组int[256] 因为灰度值的范围为0～255
 3.      扫描灰度图像，完成强度数据计算。
 4.      使用Java 2D绘制直方图
 */
public class HistogramAnalysisUtils {
    private BufferedImage srcImage;
    private BufferedImage histogramImage;
    private int size = 280;

    public HistogramAnalysisUtils(BufferedImage srcImage){
        histogramImage = new BufferedImage(size,size, BufferedImage.TYPE_4BYTE_ABGR);
        this.srcImage = srcImage;
    }

    public BufferedImage getHistogram(String title) {
        int[] inPixels = new int[srcImage.getWidth()*srcImage.getHeight()];
        int[] intensity = new int[256];
        for(int i=0; i<intensity.length; i++) {
            intensity[i] = 0;
        }
        getRGB( srcImage, 0, 0, srcImage.getWidth(), srcImage.getHeight(), inPixels );
        int index = 0;
        for(int row=0; row<srcImage.getHeight(); row++) {
            int ta = 0, tr = 0, tg = 0, tb = 0;
            for(int col=0; col<srcImage.getWidth(); col++) {
                index = row * srcImage.getWidth() + col;
                ta = (inPixels[index] >> 24) & 0xff;
                tr = (inPixels[index] >> 16) & 0xff;
                tg = (inPixels[index] >> 8) & 0xff;
                tb = inPixels[index] & 0xff;
                int gray = (int)(0.299 * (double)tr + 0.587 * (double)tg + 0.114 * (double)tb);
                intensity[gray]++;
            }
        }

        // draw XY Axis lines
        Graphics2D g2d = histogramImage.createGraphics();
        g2d.setPaint(Color.BLACK);
        g2d.fillRect(0, 0, size, size);
        g2d.setPaint(Color.WHITE);
        g2d.drawLine(5, 250, 265, 250);
        g2d.drawLine(5, 250, 5, 5);

        // scale to 200
        g2d.setPaint(Color.GREEN);
        int max = findMaxValue(intensity);
        float rate = 200.0f/((float)max);
        int offset = 2;
        for(int i=0; i<intensity.length; i++) {
            int frequency = (int)(intensity[i] * rate);
            g2d.drawLine(5 + offset + i, 250, 5 + offset + i, 250-frequency);
        }

        // X Axis Gray intensity
        g2d.setPaint(Color.RED);
        g2d.drawString(title, 100, 270);
        return histogramImage;
    }

    private int findMaxValue(int[] intensity) {
        int max = -1;
        for(int i=0; i<intensity.length; i++) {
            if(max < intensity[i]) {
                max = intensity[i];
            }
        }
        return max;
    }

    /**
     * A convenience method for getting ARGB pixels from an image. This tries to avoid the performance
     * penalty of BufferedImage.getRGB unmanaging the image.
     */
    public int[] getRGB( BufferedImage image, int x, int y, int width, int height, int[] pixels ) {
        int type = image.getType();
        if ( type == BufferedImage.TYPE_INT_ARGB || type == BufferedImage.TYPE_INT_RGB )
            return (int [])image.getRaster().getDataElements( x, y, width, height, pixels );
        return image.getRGB( x, y, width, height, pixels, 0, width );
    }

    /**
     * A convenience method for setting ARGB pixels in an image. This tries to avoid the performance
     * penalty of BufferedImage.setRGB unmanaging the image.
     */
    public void setRGB( BufferedImage image, int x, int y, int width, int height, int[] pixels ) {
        int type = image.getType();
        if ( type == BufferedImage.TYPE_INT_ARGB || type == BufferedImage.TYPE_INT_RGB )
            image.getRaster().setDataElements( x, y, width, height, pixels );
        else
            image.setRGB( x, y, width, height, pixels, 0, width );
    }
}
