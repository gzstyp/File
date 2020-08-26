package com.yinlz.tool;

import javax.imageio.ImageIO;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * 图片工具类
 * @author 田应平
*/
public final class ToolImage{

    /**
     * 无损输出图片到指定路径
     * @param image 图片对象
     * @param fileFullPath 输出目标完整路径
     */
    private static boolean thumbnail(final BufferedImage image,final String fileFullPath){
        try{
            ImageIO.write(image,"jpeg", new File(fileFullPath));
            image.flush();
            return true;
        }catch(Exception e){
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 将图片压缩成指定尺寸的图片
     * @param src 源图路径
     * @param target 目标图路径
     * @param width 压缩后的宽度
     * @param height 压缩后的高度
     * @return true:压缩成功 otherwise false 压缩失败
    */
    public static boolean resize(final String src,final String target,final int width,final int height){
        try{
            final File targetfile = new File(target);
            if(!targetfile.exists()){
                targetfile.getParentFile().mkdirs();
            }
            final BufferedImage im = ImageIO.read(new File(src));
            // 生成新的图片
            final BufferedImage result = new BufferedImage(width,height,BufferedImage.TYPE_INT_RGB);
            result.getGraphics().drawImage(im.getScaledInstance(width,height,Image.SCALE_SMOOTH),0,0,null);
            return thumbnail(result,target);
        }catch(FileNotFoundException e){
            e.printStackTrace();
        }catch(IOException e){
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 指定路径压缩大中小图
     * @param type 1: 800x480 大图路径；2: 480x320 中图路径；3: 320x240 小图路径
     * @作者 田应平
     * @QQ 444141300
     * @创建时间 2019/10/11 19:34
    */
    public final static void compress(final String originalPath,final String targetPath,final int type){
        try {
            switch (type){
                case 1:// 800x480 大图路径
                    new Thread(){
                        public void run() {
                            resize(originalPath,targetPath,800,480);
                        }
                    }.start();
                    break;
                case 2:// 480x320 中图路径
                    new Thread(){
                        public void run() {
                            resize(originalPath,targetPath,480,320);
                        }
                    }.start();
                    break;
                case 3:// 320x240 小图路径
                    new Thread(){
                        public void run() {
                            resize(originalPath,targetPath,320,240);
                        }
                    }.start();
                    break;
                default:
                    break;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}