package com.pw.demo.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.pw.demo.entity.Files;
import com.pw.demo.mapper.FileMapper;
import com.pw.demo.service.IFileService;
import org.springframework.stereotype.Service;

/**
 * @Author: P
 * @DateTime: 2022/1/10 16:29
 **/
@Service
public class FileServiceImpl extends ServiceImpl<FileMapper, Files> implements IFileService {
}
