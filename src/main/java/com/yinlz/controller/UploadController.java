package com.yinlz.controller;

import com.yinlz.tool.ToolImage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Vector;

/**
 * 图片、头像、视频的文件上传中心
 * @作者 田应平
 * @版本 v1.0
 * @创建时间 2019-09-30 14:06
 * @QQ号码 444141300
 * @Email service@yinlz.com
 * @官网 http://www.yinlz.com
*/
@RestController
public class UploadController{

    private Logger logger = LoggerFactory.getLogger(getClass());

    final String select_file = ToolClient.createJson(ConfigFile.code199,"请选择上传文件");
    final String oversize = ToolClient.createJson(ConfigFile.code199,"操作失败,某个文件过大");

    //上传图片的限制大小
    @Value("${limit_size_image}")
    private Long limit_size_image;

    //上传文件的根目录
    @Value("${dir_linux}")
    private String dir_folder;

    //图片访问服务器
    @Value("${domain_image}")
    private String domain_image;

    //视频访问服务器
    @Value("${domain_video}")
    private String domain_video;

    //头像访问服务器
    @Value("${domain_avatar}")
    private String domain_avatar;

    private final String type_images = "images/";
    private final String type_video = "video/";
    private final String type_photo = "photo/";

    /**
     * 图片上传文件 upimg.jumaitong.cn
     * @描述 支持单个或多文件文件上传-图片-推荐
     * @param
     * @作者 田应平
     * @QQ 444141300
     * @创建时间 2019/10/10 18:04
    */
    @PostMapping("/images")
    @ResponseBody
    public final void images(@RequestParam("images") MultipartFile[] files,final HttpServletResponse response){
        if (files == null || files.length == 0){
            ToolClient.responseJson(select_file,response);
            return;
        }
        if(files.length > 3){
            ToolClient.responseJson(ToolClient.createJson(ConfigFile.code199,"操作失败,上传文件过多"),response);
            return;
        }
        try {
            boolean bl = false;
            final Vector<String> listFile = new Vector<String>();
            final Vector<Hashtable<String,String>> result = new Vector<>();
            for (final MultipartFile file : files){
                if(file.getSize() > limit_size_image){
                    bl = true;
                    break;
                }
                final String key = ToolClient.getIdsChar32();
                final String name = file.getOriginalFilename();
                final String extName = name.substring(name.lastIndexOf("."));
                final String fileName = key + extName;
                final String dir_day = new SimpleDateFormat("yyyyMMdd").format(new Date())+"/";//不能写在静态方法里
                final String dirDayOriginal = dir_day + "original/";//原图路径
                final String dirDayLarge = dir_day + "large/";//大图路径
                final String dirDayMedium = dir_day + "medium/";//中图路径
                final String dirDaySmall = dir_day + "small/";//小图路径

                final String base_dir = type_images + dir_day;
                final String original_dir = type_images + dirDayOriginal;//原图路径
                final String dir_large = base_dir + "large/";//大图路径
                final String dir_medium = base_dir + "medium/";//中图路径
                final String dir_small = base_dir + "small/";//小图路径

                final String original_file_dir = dir_folder + original_dir;
                final String large_file_dir = dir_folder + dir_large;//大图路径
                final String medium_file_dir = dir_folder + dir_medium;//中图路径
                final String small_file_dir = dir_folder + dir_small;//小图路径

                final File fileDir = new File(original_file_dir);
                if(!fileDir.exists()){
                    fileDir.mkdirs();
                    new File(large_file_dir).mkdirs();
                    new File(medium_file_dir).mkdirs();
                    new File(small_file_dir).mkdirs();
                }
                final String temp = original_file_dir + fileName;
                file.transferTo(new File(temp));
                listFile.add(temp);
                final String originalPath = dir_folder + base_dir + "original/" + fileName;

                ToolImage.compress(originalPath,large_file_dir + fileName,1);
                ToolImage.compress(originalPath,medium_file_dir + fileName,2);
                ToolImage.compress(originalPath,small_file_dir + fileName,3);

                final Hashtable<String,String> map = new Hashtable<String,String>(16);
                map.put("originalFilename",file.getOriginalFilename());
                map.put("fileName",fileName);
                map.put("total",files.length + "");
                map.put("fileSize", file.getSize() + "");
                map.put("dir_base",original_file_dir);
                map.put("filePath",original_dir + fileName);
                map.put("url",domain_image + dirDayOriginal + fileName);
                map.put("large",domain_image + dirDayLarge + fileName);
                map.put("medium",domain_image + dirDayMedium + fileName);
                map.put("small",domain_image + dirDaySmall + fileName);
                result.add(map);
            }
            if(bl){
                ToolClient.delFileByThread(listFile);
                ToolClient.responseJson(oversize,response);
                return;
            }
            ToolClient.responseJson(ToolClient.queryJson(result),response);
        } catch (Exception e) {
            logger.error(e.getMessage());
            ToolClient.responseJson(ToolClient.exceptionJson(),response);
        }
    }

    /**
     * 视频上传文件 upv.jumaitong.cn
     * @描述 单视频文件上传-推荐
     * @param
     * @作者 田应平
     * @QQ 444141300
     * @创建时间 2019/10/10 18:05
    */
    @PostMapping("/video")
    @ResponseBody
    public final void video(@RequestParam("video") MultipartFile file,final HttpServletResponse response){
        try {
            if(file == null || file.isEmpty()){
                ToolClient.responseJson(select_file,response);
                return;
            }
            final HashMap<String,String> result = new HashMap<>(16);
            final String key = ToolClient.getIdsChar32();
            final String name = file.getOriginalFilename();
            final String extName = name.substring(name.lastIndexOf("."));
            final String fileName = key + extName;
            final String dir_day = new SimpleDateFormat("yyyyMMdd").format(new Date())+"/";//不能写在静态方法里
            final String dir = type_video + dir_day;
            final String file_dir = dir_folder + dir;
            final File fileDir = new File(file_dir);
            if(!fileDir.exists()){
                fileDir.mkdirs();
            }
            file.transferTo(new File(file_dir + fileName));
            result.put("originalFilename",file.getOriginalFilename());
            result.put("dir_base",file_dir);
            result.put("fileName",fileName);
            result.put("url",domain_video + dir_day + fileName);
            result.put("fileSize",file.getSize() + "");
            result.put("filePath",dir + fileName);
            ToolClient.responseJson(ToolClient.queryJson(result),response);
        } catch (Exception e) {
            logger.error(e.getMessage());
            ToolClient.responseJson(ToolClient.exceptionJson(),response);
        }
    }

    /**
     * 头像上传文件 photo.jumaitong.cn
     * @param
     * @作者 田应平
     * @QQ 444141300
     * @创建时间 2019/10/10 18:05
    */
    @PostMapping("/photo")
    @ResponseBody
    public final void photo(@RequestParam("photo") MultipartFile file,final HttpServletResponse response){
        try {
            if(file == null || file.isEmpty()){
                ToolClient.responseJson(select_file,response);
                return;
            }
            if(file.getSize() > limit_size_image){
                ToolClient.responseJson(oversize,response);
                return;
            }
            final HashMap<String,String> result = new HashMap<>(16);
            final String key = ToolClient.getIdsChar32();
            final String name = file.getOriginalFilename();
            final String extName = name.substring(name.lastIndexOf("."));
            final String fileName = key + extName;
            final String dir_day = new SimpleDateFormat("yyyyMMdd").format(new Date())+"/";//不能写在静态方法里
            final String dir = type_photo + dir_day;
            final String base_dir = dir_folder + type_photo + dir_day;
            final String dirDayOriginal = base_dir + "original/";//原图路径
            final String dir_medium = base_dir + "medium/";//中图路径
            final String dir_small = base_dir + "small/";//小图图路径
            final File fileDir = new File(dirDayOriginal);
            if(!fileDir.exists()){
                fileDir.mkdirs();
                new File(dir_medium).mkdirs();//中图路径
                new File(dir_small).mkdirs();//小图路径
            }
            final String originalPath = dirDayOriginal + fileName;
            file.transferTo(new File(originalPath));
            ToolImage.compress(originalPath,dir_medium + fileName,2);
            ToolImage.compress(originalPath,dir_small + fileName,3);
            result.put("originalFilename",file.getOriginalFilename());
            result.put("dir_base",dirDayOriginal);
            result.put("fileName",fileName);
            result.put("url",domain_avatar + dir_day + "original/" + fileName);//原图
            result.put("medium",domain_avatar + dir_day + "medium/" + fileName);//中图
            result.put("small",domain_avatar + dir_day + "small/" + fileName);//小图
            result.put("fileSize",file.getSize() + "");
            result.put("filePath",dir + "original/" + fileName);
            ToolClient.responseJson(ToolClient.queryJson(result),response);
        } catch (Exception e) {
            logger.error(e.getMessage());
            ToolClient.responseJson(ToolClient.exceptionJson(),response);
        }
    }

    /**
     * 手机客户端已压缩后才上传的
     * @param
     * @作者 田应平
     * @QQ 444141300
     * @创建时间 2019/10/17 18:23
    */
    @PostMapping("/imgs")
    @ResponseBody
    public final void imgs(@RequestParam("images") MultipartFile[] files,final HttpServletResponse response){
        if (files == null || files.length == 0){
            ToolClient.responseJson(select_file,response);
            return;
        }
        if(files.length > 3){
            ToolClient.responseJson(ToolClient.createJson(ConfigFile.code199,"操作失败,上传文件过多"),response);
            return;
        }
        try {
            boolean bl = false;
            final Vector<String> listFile = new Vector<String>();
            final Vector<Hashtable<String,String>> result = new Vector<>();
            for (final MultipartFile file : files){
                if(file.getSize() > limit_size_image){
                    bl = true;
                    break;
                }
                final String key = ToolClient.getIdsChar32();
                final String name = file.getOriginalFilename();
                final String extName = name.substring(name.lastIndexOf("."));
                final String fileName = key + extName;
                final String dir_day = new SimpleDateFormat("yyyyMMdd").format(new Date())+"/";//不能写在静态方法里
                final String dirDayOriginal = dir_day + "original/";//原图路径
                final String original_dir = type_images + dirDayOriginal;//原图路径
                final String original_file_dir = dir_folder + original_dir;

                final File fileDir = new File(original_file_dir);
                if(!fileDir.exists()){
                    fileDir.mkdirs();
                }
                final String temp = original_file_dir + fileName;
                file.transferTo(new File(temp));
                listFile.add(temp);
                final Hashtable<String,String> map = new Hashtable<String,String>(16);
                map.put("originalFilename",file.getOriginalFilename());
                map.put("fileName",fileName);
                map.put("total",files.length + "");
                map.put("fileSize", file.getSize() + "");
                map.put("dir_base",original_file_dir);
                map.put("filePath",original_dir + fileName);
                map.put("url",domain_image + dirDayOriginal + fileName);
                map.put("large",domain_image + dirDayOriginal + fileName);
                map.put("medium",domain_image + dirDayOriginal + fileName);
                map.put("small",domain_image + dirDayOriginal + fileName);
                result.add(map);
            }
            if(bl){
                ToolClient.delFileByThread(listFile);
                ToolClient.responseJson(oversize,response);
                return;
            }
            ToolClient.responseJson(ToolClient.queryJson(result),response);
        } catch (Exception e) {
            logger.error(e.getMessage());
            ToolClient.responseJson(ToolClient.exceptionJson(),response);
        }
    }

    /**手机客户端已压缩后才上传的*/
    @PostMapping("/avatar")
    @ResponseBody
    public final void avatar(@RequestParam("photo") MultipartFile file,final HttpServletResponse response){
        try {
            if(file == null || file.isEmpty()){
                ToolClient.responseJson(select_file,response);
                return;
            }
            if(file.getSize() > limit_size_image){
                ToolClient.responseJson(oversize,response);
                return;
            }
            final HashMap<String,String> result = new HashMap<>(16);
            final String dir_day = new SimpleDateFormat("yyyyMMdd").format(new Date())+"/";//不能写在静态方法里
            final String key = ToolClient.getIdsChar32();
            final String name = file.getOriginalFilename();
            final String extName = name.substring(name.lastIndexOf("."));
            final String fileName = key + extName;
            final String dir = type_photo + dir_day;
            final String base_dir = dir_folder + type_photo + dir_day;
            final String dirDayOriginal = base_dir + "original/";//原图路径
            final File fileDir = new File(dirDayOriginal);
            if(!fileDir.exists()){
                fileDir.mkdirs();
            }
            final String originalPath = dirDayOriginal + fileName;
            file.transferTo(new File(originalPath));
            result.put("originalFilename",file.getOriginalFilename());
            result.put("dir_base",dirDayOriginal);
            result.put("fileName",fileName);
            result.put("url",domain_avatar + dir_day + "original/" + fileName);//原图
            result.put("medium",domain_avatar + dir_day + "original/" + fileName);
            result.put("small",domain_avatar + dir_day + "original/" + fileName);
            result.put("fileSize",file.getSize() + "");
            result.put("filePath",dir + "original/" + fileName);
            ToolClient.responseJson(ToolClient.queryJson(result),response);
        } catch (Exception e) {
            logger.error(e.getMessage());
            ToolClient.responseJson(ToolClient.exceptionJson(),response);
        }
    }

    /*public static boolean isFile(final InputStream inputStream){
        try {
            byte[] byteArray = IOUtils.toByteArray(inputStream);
            String str = bytesToHexString(byteArray);
            // 匹配16进制中的 <% ( ) %>
            // 匹配16进制中的 <? ( ) ?>
            // 匹配16进制中的 <script | /script> 大小写亦可
            // 通过匹配十六进制代码检测是否存在木马脚本
            String pattern = "(3c25.*?28.*?29.*?253e)|(3c3f.*?28.*?29.*?3f3e)|(3C534352495054)|(2F5343524950543E)|(3C736372697074)|(2F7363726970743E)";
            Pattern mPattern = Pattern.compile(pattern);
            Matcher mMatcher = mPattern.matcher(str);
            // 查找相应的字符串
            boolean flag = true;
            if(mMatcher.find()) {
                flag = false;
                //过滤java关键字(java import String print write( read() php request alert system)（暂时先这样解决，这样改动最小，以后想后更好的解决方案再优化）
                String keywordPattern = "(6a617661)|(696d706f7274)|(537472696e67)|(7072696e74)|(777269746528)|(726561642829)|(706870)|(72657175657374)|(616c657274)|(73797374656d)";
                Pattern keywordmPattern = Pattern.compile(keywordPattern);
                Matcher keywordmMatcher = keywordmPattern.matcher(str);
                if(keywordmMatcher.find()){
                    flag = false;
                }
            }
            return flag;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }*/
}