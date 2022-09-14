package com.pw.demo.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.pw.demo.entity.Menu;
import com.pw.demo.entity.Role;
import com.pw.demo.entity.RoleMenu;
import com.pw.demo.mapper.RoleMapper;
import com.pw.demo.mapper.RoleMenuMapper;
import com.pw.demo.service.IMenuService;
import com.pw.demo.service.IRoleService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author p
 * @since 2022-01-11
 */
@Service
public class RoleServiceImpl extends ServiceImpl<RoleMapper, Role> implements IRoleService {

    @Resource
    private RoleMenuMapper roleMenuMapper;

    @Resource
    private IMenuService menuService;

//    @Transactional
//    @Override
//    public void setRoleMenu(Integer roleId, List<Integer> menuIds) {
////        QueryWrapper<RoleMenu> queryWrapper = new QueryWrapper<>();
////        queryWrapper.eq("role_id", roleId);
////        roleMenuMapper.delete(queryWrapper);
//
//        // 先删除当前角色id所有的绑定关系
//        roleMenuMapper.deleteByRoleId(roleId);
//
//        // 再把前端传过来的菜单id数组绑定到当前的这个角色id上去
//        List<Integer> menuIdsCopy = CollUtil.newArrayList(menuIds);
//        for (Integer menuId : menuIds) {
//            Menu menu = menuService.getById(menuId);
//            if (menu.getPid() != null && !menuIdsCopy.contains(menu.getPid())) { // 二级菜单 并且传过来的menuId数组里面没有它的父级id
//                // 那么我们就得补上这个父级id
//                RoleMenu roleMenu = new RoleMenu();
//                roleMenu.setRoleid(roleId);
//                roleMenu.setMenuid(menu.getPid());
//                roleMenuMapper.insert(roleMenu);
//                menuIdsCopy.add(menu.getPid());
//            }
//            RoleMenu roleMenu = new RoleMenu();
//            roleMenu.setRoleid(roleId);
//            roleMenu.setMenuid(menuId);
//            roleMenuMapper.insert(roleMenu);
//        }
//    }


    @Transactional
    @Override
    public void setRoleMenu(Integer roleId, List<Integer> menuIds) {
        //先删除当前id所有绑带关系 再添加
        roleMenuMapper.deleteByRoleId(roleId);

        // 再把前端传过来的菜单id数组绑定到当前的这个角色id上去
        List<Integer> menuIdsCopy = CollUtil.newArrayList(menuIds);

        //把前端绑定传过来的id绑定回来
        for(Integer menuId:menuIds){
            Menu menu = menuService.getById(menuId);
            if (menu.getPid() != null && !menuIdsCopy.contains(menu.getPid())) { // 二级菜单 并且传过来的menuId数组里面没有它的父级id
                // 那么我们就得补上这个父级id
                RoleMenu roleMenu = RoleMenu.builder().roleid(roleId).menuid(menu.getPid()).build();
                roleMenuMapper.insert(roleMenu);
                menuIdsCopy.add(menu.getPid());
            }
            RoleMenu roleMenu=RoleMenu.builder().roleid(roleId).menuid(menuId).build();
            roleMenuMapper.insert(roleMenu);
        }
    }

    @Override
    public List<Integer> getRoleMenu(Integer roleId) {
        return roleMenuMapper.selectByRoleId(roleId);
    }
}
