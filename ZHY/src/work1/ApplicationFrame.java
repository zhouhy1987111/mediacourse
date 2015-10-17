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

import utils.GraphicsUtils;
import utils.HistogramAnalysisUtils;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.*;
import java.io.IOException;

import javax.swing.*;

/**
 * Created with IntelliJ IDEA.
 * User: ZHY
 * Date: 15-10-16
 * Time: 下午11:05
 */
public class ApplicationFrame extends JFrame implements ActionListener {
    private BufferedImage sourceImage;
    private BufferedImage grayImage;
    private BufferedImage filterImage;
    private JLabel yuanshiLabel;
    private JLabel grayLabel;
    private JLabel filterLabel;
    private JButton zhifangtuButton1;
    private JButton zhifangtuButton2;
    private JButton zhifangtuButton3;
    private JButton yuanshituButton1;
    private JButton yuanshituButton2;
    private JButton yuanshituButton3;
    private JButton huiduButton;

    public ApplicationFrame() {
        super("数字图像课程1--灰度和对比度 作者:周弘懿_Z14030746");
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
//        setLayout(new BorderLayout());
//        JPanel p1 = new JPanel();
//        JButton b1 = new JButton("查询");
//        p1.add(b1);
//        add(p1, "West");
        JTabbedPane tabbedPane = new JTabbedPane();
        buildNoOpTab(tabbedPane);
        buildColorConvertOpTab(tabbedPane);
        buildFilterOpTab(tabbedPane);
        add(tabbedPane, "Center");
    }

    private int clamp(int value) {
        return value > 255 ? 255 :(value < 0 ? 0 : value);
    }

    private BufferedImage buildFilterOpTabInner(BufferedImage sourceImage, float contrast, float brightness) {
        //获得源图片长度和宽度
        int width=sourceImage.getWidth();
        int height=sourceImage.getHeight();
        BufferedImage filterImage=new BufferedImage(width,height,sourceImage.getType());
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
        filterImage.setRGB(0, 0, width, height, outPixels, 0, width);
        return filterImage;
    }

    /**
     * 对比度/亮度
     * @param tabbedPane
     */
    private void buildFilterOpTab(JTabbedPane tabbedPane) {
        float contrast = 1f;
        float brightness = 1f;
        filterImage = buildFilterOpTabInner(sourceImage, contrast, brightness);
        filterLabel = new JLabel(new ImageIcon(filterImage));
        filterLabel.setLayout(new FlowLayout());
        zhifangtuButton3 = new JButton("直方图");
        zhifangtuButton3.addActionListener(this);
        yuanshituButton3 = new JButton("原始图");
        yuanshituButton3.addActionListener(this);
        huiduButton = new JButton("对比度/亮度调整");
        huiduButton.addActionListener(this);
        filterLabel.add(zhifangtuButton3);
        filterLabel.add(yuanshituButton3);
        filterLabel.add(huiduButton);
        tabbedPane.add("对比度/亮度图像", filterLabel);
    }

    private void loadSourceImage() {
        try {
            sourceImage = GraphicsUtils.loadCompatibleImage(getClass().getResource("/imageop/beauty.jpg"));
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private void buildNoOpTab(JTabbedPane tabbedPane) {
        yuanshiLabel = new JLabel(new ImageIcon(sourceImage));
        yuanshiLabel.setLayout(new FlowLayout());
        zhifangtuButton1 = new JButton("直方图");
        zhifangtuButton1.addActionListener(this);
        yuanshituButton1 = new JButton("原始图");
        yuanshituButton1.addActionListener(this);
        yuanshiLabel.add(zhifangtuButton1);
        yuanshiLabel.add(yuanshituButton1);
        tabbedPane.add("原始图像", yuanshiLabel);
    }

    private BufferedImage buildColorConvertOpTabInner(BufferedImage sourceImage) {
        int iw = sourceImage.getWidth(this);
        int ih = sourceImage.getHeight(this);
        int[] pixels=new int[iw*ih];
        sourceImage.getRGB(0,0,iw,ih,pixels,0,iw);

        ColorModel cm = ColorModel.getRGBdefault();
        int r, g, b, gray;
        for (int i = 0; i < iw * ih; i++) {
            r = cm.getRed(pixels[i]);
            g = cm.getGreen(pixels[i]);
            b = cm.getBlue(pixels[i]);
            gray = (int) ((r + g + b) / 3);
            pixels[i] = 255 << 24 | gray << 16 | gray << 8 | gray;
        }
        BufferedImage colorConvertImage=new BufferedImage(iw,ih,sourceImage.getType());
        colorConvertImage.setRGB(0, 0, iw, ih, pixels, 0, iw);
        return colorConvertImage;
    }

    /**
     * 灰度
     * @param tabbedPane
     */
    private void buildColorConvertOpTab(JTabbedPane tabbedPane) {
        grayImage =buildColorConvertOpTabInner(sourceImage);
        grayLabel = new JLabel(new ImageIcon(grayImage));
        grayLabel.setLayout(new FlowLayout());
        zhifangtuButton2 = new JButton("直方图");
        zhifangtuButton2.addActionListener(this);
        yuanshituButton2 = new JButton("原始图");
        yuanshituButton2.addActionListener(this);
        grayLabel.add(zhifangtuButton2);
        grayLabel.add(yuanshituButton2);
        tabbedPane.add("灰度化图像", grayLabel);
    }

    @Override
    public void actionPerformed(ActionEvent env) {
        if (env.getSource() == huiduButton) {
            try {
                float duibiduValue = Float.parseFloat(JOptionPane.showInputDialog(null, "对比度", 1.5));
                float liangduValue = Float.parseFloat(JOptionPane.showInputDialog(null, "亮度", 1.5));
                filterImage = buildFilterOpTabInner(sourceImage, duibiduValue, liangduValue);
                filterLabel.setIcon(new ImageIcon(filterImage));
                repaint();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if(env.getSource() == zhifangtuButton1) {
            yuanshiLabel.setIcon(new ImageIcon(new HistogramAnalysisUtils(sourceImage).getHistogram("原始图像直方图")));
            repaint();
        } else if(env.getSource() == zhifangtuButton2) {
            grayLabel.setIcon(new ImageIcon(new HistogramAnalysisUtils(grayImage).getHistogram("灰度化图像直方图")));
            repaint();
        } else if(env.getSource() == zhifangtuButton3) {
            filterLabel.setIcon(new ImageIcon(new HistogramAnalysisUtils(filterImage).getHistogram("对比度/亮度图像直方图")));
            repaint();
        } else if(env.getSource() == yuanshituButton1) {
            yuanshiLabel.setIcon(new ImageIcon(sourceImage));
            repaint();
        } else if(env.getSource() == yuanshituButton2) {
            grayLabel.setIcon(new ImageIcon(buildColorConvertOpTabInner(sourceImage)));
            repaint();
        } else if(env.getSource() == yuanshituButton3) {
            filterLabel.setIcon(new ImageIcon(sourceImage));
            repaint();
        }
    }
}
