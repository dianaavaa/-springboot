package com.pw.demo.service;

import com.pw.demo.entity.Menu;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author p
 * @since 2022-01-11
 */
public interface IMenuService extends IService<Menu> {

    List<Menu> findMenus (String name);
}
