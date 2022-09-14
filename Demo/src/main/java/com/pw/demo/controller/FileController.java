package com.pw.demo.controller;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.lang.TypeReference;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pw.demo.common.Constants;
import com.pw.demo.common.Result;
import com.pw.demo.entity.Files;
import com.pw.demo.mapper.FileMapper;
import com.pw.demo.service.IFileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.List;

/**
 * 文件上传接口
 * @Author: P
 * @DateTime: 2022/1/10 15:49
 **/
@RestController
@RequestMapping("/file")
public class FileController {

    @Value("${files.upload.path}")
    private String fileUploadPath;

    @Value("${server.ip}")
    private String serverIp;

    @Autowired
    private FileMapper fileMapper;
    @Autowired
    private IFileService fileService;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;



    @GetMapping("/detail/{id}")
    public Result getById(@PathVariable Integer id) {
        return Result.success(fileMapper.selectById(id));
    }

    /**
     * 文件上传接口
     * @param file
     * @return
     */
    @PostMapping("/upload")
    public String upload(@RequestParam MultipartFile file) throws IOException {
        //获取名字
        String originalFilename = file.getOriginalFilename();
        //判断是否存在 type:后缀
        String type = FileUtil.extName(originalFilename);
        //获取大小
        long size=file.getSize();

        //标识码
        String uuid= IdUtil.simpleUUID();
        String fileUUID=uuid+ StrUtil.DOT+type;

        //上传文件
        File uploadFile=new File(fileUploadPath+ fileUUID);
        // 判断配置的文件目录是否存在，若不存在则创建一个新的文件目录
        File parentFile = uploadFile.getParentFile();
        if(!parentFile.exists()) {
            parentFile.mkdirs();
        }

        //设置md5
        String md5 = SecureUtil.md5(file.getInputStream());
        // 从数据库查询是否存在相同的记录
        Files dbFiles = getFileByMd5(md5);

        String url;
        if (dbFiles != null) { // 文件已存在
            url = dbFiles.getUrl();
        } else {
            // 上传文件到磁盘
            file.transferTo(uploadFile);
            // 数据库若不存在重复文件，则不删除刚才上传的文件
            url = "http://"+serverIp+":8085/file/" + fileUUID;
        }

        //构造
        Files saveFile=Files.builder()
                .name(originalFilename).type(type).size(size/1024).url(url).md5(md5)
                .build();
        fileService.save(saveFile);


        //从redis取出并设置
        String jsonStr = stringRedisTemplate.opsForValue().get(Constants.File_KEY);
        List<Files> files= JSONUtil.toBean(jsonStr, new TypeReference<List<Files>>() {
        },true);
        files.add(saveFile);
        setRedis(Constants.File_KEY, JSONUtil.toJsonStr(files));


//        s刷新缓存
//        flushRedis(Constants.File_KEY);
        //插入数据库
        return url;
    }

    /**
     * http://localhost:8085/file/{fileUUID}
     * @param fileUUID
     * @param response
     * @throws IOException
     */
    @GetMapping("/{fileUUID}")
    public void download(@PathVariable String fileUUID, HttpServletResponse response) throws IOException {
        //g根据唯一路径获取文件
        File uploadFile=new File(fileUploadPath+fileUUID);

        //设置格式
        ServletOutputStream os = response.getOutputStream();
        response.addHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileUUID, "UTF-8"));
        response.setContentType("application/octet-stream");

        //读取 写入
        os.write(FileUtil.readBytes(uploadFile));
        os.flush();
        os.close();


    }

    /**
     * 获取文件的md5
     * @param md5
     * @return
     */
    private Files getFileByMd5(String md5){
        QueryWrapper<Files> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("md5", md5);
        List<Files> filesList = fileMapper.selectList(queryWrapper);
        return filesList.size() == 0 ? null : filesList.get(0);
    }

    /**
     * 分页查询接口
     * @param pageNum
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    public Result findPage(@RequestParam Integer pageNum,
                           @RequestParam Integer pageSize,
                           @RequestParam(defaultValue = "") String name) {

        QueryWrapper<Files> queryWrapper = new QueryWrapper<>();
        // 查询未删除的记录
        queryWrapper.eq("is_delete", false);
        queryWrapper.orderByDesc("id");
        if (!"".equals(name)) {
            queryWrapper.like("name", name);
        }
        return Result.success(fileMapper.selectPage(new Page<>(pageNum, pageSize), queryWrapper));
    }

    @PostMapping("/update")
    public Result update(@RequestBody Files files){
        fileMapper.updateById(files);
        flushRedis(Constants.File_KEY);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Integer id){
//        QueryWrapper<Files> wrapper=new QueryWrapper<>();
        Files file=fileMapper.selectById(id);
        file.setIsDelete(true);
        fileMapper.updateById(file);
        flushRedis(Constants.File_KEY);
        return Result.success();
    }

    @DeleteMapping("/del/batch")
    public Result deleteBatch(@RequestBody List<Integer> ids){
        QueryWrapper<Files> wrapper=new QueryWrapper<>();
        wrapper.in("id",ids);
        List<Files> files = fileMapper.selectList(wrapper);
        for(Files file:files){
            file.setIsDelete(true);
            fileMapper.updateById(file);
        }
        return Result.success();
    }


    public void setRedis(String key,String value){
        stringRedisTemplate.opsForValue().set(key,value);
    }

    public void flushRedis(String key){
        stringRedisTemplate.delete(key);
    }

}
