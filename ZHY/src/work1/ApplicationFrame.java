/*
 * Copyright (c) 2007, Romain Guy
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *   * Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *   * Redistributions in binary form must reproduce the above
 *     copyright notice, this list of conditions and the following
 *     disclaimer in the documentation and/or other materials provided
 *     with the distribution.
 *   * Neither the name of the TimingFramework project nor the names of its
 *     contributors may be used to endorse or promote products derived
 *     from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 * OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package work1;

import java.awt.*;
import java.awt.color.ColorSpace;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.AffineTransform;
import java.awt.image.*;
import java.io.IOException;

import javax.swing.*;

public class ApplicationFrame extends JFrame implements ActionListener {
    private BufferedImage sourceImage;
    JMenuItem affineTransformLabel;
    String affineTransformString;

    public ApplicationFrame() {
        super("数字图像例子");

        loadSourceImage();
        buildTabbedPane();

        pack();

        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }

    public static void main(String... args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new ApplicationFrame().setVisible(true);
            }
        });
    }

    private void buildTabbedPane() {
        JTabbedPane tabbedPane = new JTabbedPane();

        buildNoOpTab(tabbedPane);
        buildColorConvertOpTab(tabbedPane);
        buildFilterOpTab(tabbedPane);
        add(tabbedPane);
    }

    private int clamp(int value) {
        return value > 255 ? 255 :(value < 0 ? 0 : value);
    }

    private void buildFilterOpTab(JTabbedPane tabbedPane) {
        float contrast = 1.8f;
        float brightness = 1.7f;

        //获得源图片长度和宽度
        int width=sourceImage.getWidth();
        int height=sourceImage.getHeight();
        BufferedImage dstImage=new BufferedImage(width,height,sourceImage.getType());
        int[] inPixels=new int[width*height];
        int[] outPixels=new int[width*height];
        sourceImage.getRGB(0,0,width,height,inPixels,0,width);

        //计算一个像素的红，绿，蓝方法
        int index=0;
        int[] rgbmeans=new int[3];
        double redSum=0,greenSum=0,blueSum=0;
        double total=height*width;
        for(int row=0;row<height;row++)
        {
            int ta=0,tr=0,tg=0,tb=0;
            for(int col=0;col<width;col++)
            {
                index=row*width+col;
                ta=(inPixels[index] >> 24) & 0xff;
                tr=(inPixels[index] >> 16) & 0xff;
                tg=(inPixels[index] >> 8) & 0xff;
                tb=inPixels[index] & 0xff;
                redSum+=tr;
                greenSum+=tg;
                blueSum+=tb;
            }
        }
        //求出图像像素平均值
        rgbmeans[0]=(int)(redSum/total);
        rgbmeans[1]=(int)(greenSum/total);
        rgbmeans[2]=(int)(blueSum/total);

        //调整对比度，亮度
        for(int row=0;row<height;row++)
        {
            int ta=0,tr=0,tg=0,tb=0;
            for(int col=0;col<width;col++)
            {
                index = row * width + col;
                ta=(inPixels[index] >> 24) & 0xff;
                tr=(inPixels[index] >> 16) & 0xff;
                tg=(inPixels[index] >> 8) & 0xff;
                tb=inPixels[index] & 0xff;

                //移去平均值
                tr -=rgbmeans[0];
                tg -=rgbmeans[1];
                tb -=rgbmeans[2];

                //调整对比度
                tr=(int)(tr * contrast);
                tg=(int)(tg * contrast);
                tb=(int)(tb * contrast);

                //调整亮度
                tr=(int)((tr+rgbmeans[0])*brightness);
                tg=(int)((tg+rgbmeans[1])*brightness);
                tb=(int)((tb+rgbmeans[2])*brightness); //end;

                outPixels[index] = (ta << 24) | (clamp(tr) << 16) | (clamp(tg) << 8) | clamp(tb);

            }
        }
        dstImage.setRGB(0, 0, width, height, outPixels, 0, width);
        tabbedPane.add("对比度亮度", new JLabel(new ImageIcon(dstImage)));
    }

    private void loadSourceImage() {
        try {
            // Load a compatible image for performance
            sourceImage = GraphicsUtilities.loadCompatibleImage(
                    getClass().getResource("/imageop/Provence.jpg"));
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private void buildNoOpTab(JTabbedPane tabbedPane) {
        tabbedPane.add("原始图像", new JLabel(new ImageIcon(sourceImage)));
    }

    private void buildAffineTransformOpTab(JTabbedPane tabbedPane) {
        affineTransformLabel = new JMenuItem();
//        affineTransformLabel.addActionListener(this);
        BufferedImage dstImage = null;
        AffineTransform transform = AffineTransform.getScaleInstance(0.5, 0.5);
        boolean hasErr = false;
        if(affineTransformString!=null && affineTransformString.length()>0) {
            String parts[] = affineTransformString.split(",");
            if(parts.length==2) {
                double p1 = Double.parseDouble(parts[0]);
                double p2 = Double.parseDouble(parts[1]);
                transform = AffineTransform.getScaleInstance(p1, p2);
            } else
                hasErr = true;
        }
        else
            hasErr = true;
//        if(hasErr)
//            JOptionPane.showMessageDialog(null, "输入数字不正确，请重新输入!");
        AffineTransformOp op = new AffineTransformOp(transform,
                AffineTransformOp.TYPE_BILINEAR);
        dstImage = op.filter(sourceImage, null);
        affineTransformLabel.setIcon(new ImageIcon(dstImage));
        affineTransformLabel.setHorizontalAlignment(SwingConstants.CENTER);
//        String st = JOptionPane.showInputDialog(null, "采样级数(256/128/64/32/16):","128");
        tabbedPane.add("仿射变换", affineTransformLabel);
        repaint();
    }

    private void buildColorConvertOpTab(JTabbedPane tabbedPane) {
        Image dstImage = null;
        int iw = sourceImage.getWidth(this);
        int ih = sourceImage.getHeight(this);
        int[] pixels = grabber(sourceImage, iw, ih);

        ColorModel cm = ColorModel.getRGBdefault();
        int r, g, b, gray;
        for (int i = 0; i < iw * ih; i++) {
            r = cm.getRed(pixels[i]);
            g = cm.getGreen(pixels[i]);
            b = cm.getBlue(pixels[i]);
            gray = (int) ((r + g + b) / 3);
            pixels[i] = 255 << 24 | gray << 16 | gray << 8 | gray;
        }
        ImageProducer ip = new MemoryImageSource(iw, ih, pixels, 0, iw);
        dstImage = createImage(ip);
        tabbedPane.add("灰度化", new JLabel(new ImageIcon(dstImage)));
    }

    private void buildColorConvertOpTab2(JTabbedPane tabbedPane) {
        Image dstImage = null;
        ColorSpace colorSpace = ColorSpace.getInstance(ColorSpace.CS_GRAY);
        ColorConvertOp op = new ColorConvertOp(colorSpace, null);
        dstImage = op.filter(sourceImage, null);
        tabbedPane.add("颜色变换2", new JLabel(new ImageIcon(dstImage)));
    }

    private void buildConvolveOpTab(JTabbedPane tabbedPane) {
        BufferedImage dstImage = null;
        float[] sharpen = new float[] {
                0.0f, -1.0f,  0.0f,
                -1.0f,  5.0f, -1.0f,
                0.0f, -1.0f,  0.0f
        };
        Kernel kernel = new Kernel(3, 3, sharpen);
        ConvolveOp op = new ConvolveOp(kernel, ConvolveOp.EDGE_NO_OP, null);
        dstImage = op.filter(sourceImage, null);

        tabbedPane.add("Convolve", new JLabel(new ImageIcon(dstImage)));
    }

    private void buildLookupOpTab(JTabbedPane tabbedPane) {
        BufferedImage dstImage = null;
        short[] data = new short[256];
        for (int i = 0; i < 256; i++) {
            data[i] = (short) (255 - i);
        }
//        short[] red = new short[256];
//        short[] green = new short[256];
//        short[] blue = new short[256];
//        for (short i = 0; i < 256; i++) {
//        red[i] = 0;
//        green[i] = 0;
//        blue[i] = i;
//        }
//        short[][] data = new short[][] {
//        red, green, blue
//        };
        LookupTable lookupTable = new ShortLookupTable(0, data);
        LookupOp op = new LookupOp(lookupTable, null);
        dstImage = op.filter(sourceImage, null);

        tabbedPane.add("Lookup", new JLabel(new ImageIcon(dstImage)));
    }

    private void buildRescaleOpTab(JTabbedPane tabbedPane) {
        BufferedImage dstImage = null;
        float[] factors = new float[] {
                1.4f, 1.4f, 1.4f
        };
        float[] offsets = new float[] {
                0.0f, 0.0f, 30.0f
        };
        RescaleOp op = new RescaleOp(factors, offsets, null);
        dstImage = op.filter(sourceImage, null);

        tabbedPane.add("Rescale", new JLabel(new ImageIcon(dstImage)));
    }

    public int[] grabber(Image im, int iw, int ih)
    {
        int [] pix = new int[iw * ih];
        try
        {
            PixelGrabber pg = new PixelGrabber(im, 0, 0, iw,  ih, pix, 0, iw);
            pg.grabPixels();
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }
        return pix;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
    }
}
