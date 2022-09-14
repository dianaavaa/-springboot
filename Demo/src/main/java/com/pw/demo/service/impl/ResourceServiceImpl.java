package com.pw.demo.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.pw.demo.entity.Resource;
import com.pw.demo.entity.RoleResource;
import com.pw.demo.exception.ServiceException;
import com.pw.demo.mapper.ResourceMapper;
import com.pw.demo.mapper.RoleResourceMapper;
import com.pw.demo.service.ResourceService;
import com.pw.demo.utils.BeanCopyUtils;
import com.pw.demo.vo.ResourceVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.pw.demo.common.CommonConst.FALSE;
import static com.pw.demo.common.Constants.CODE_500;

/**
 * @Author: P
 * @DateTime: 2022/2/6 10:43
 **/
@Service
public class ResourceServiceImpl extends ServiceImpl<ResourceMapper, Resource> implements ResourceService {
    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private ResourceMapper resourceMapper;
    @Autowired
    private RoleResourceMapper roleResourceMapper;


    @Transactional(rollbackFor = Exception.class)
    @Override
    public void importSwagger() {
        this.remove(null);
        roleResourceMapper.delete(null);
        List<Resource> resourceList = new ArrayList<>();
        Map<String, Object> data = restTemplate.getForObject("http://localhost:8085/v2/api-docs?group=Api",Map.class);
        //获取所有模块
        List<Map<String, String>> tagList = (List<Map<String, String>>) data.get("tags");

        tagList.forEach(item ->{
            Resource resource = Resource.builder()
                    .resourceName(item.get("name"))
                    .isAnonymous(FALSE)
                    .build();
            resourceList.add(resource);
        });
        this.saveBatch(resourceList);

        Map<String, Integer> permissionMap = resourceList.stream().collect(Collectors.toMap(Resource::getResourceName,Resource::getId));
        resourceList.clear();

        //获取所有接口
        Map<String, Map<String, Map<String, Object>>> path = (Map<String, Map<String, Map<String, Object>>>) data.get("paths");
        path.forEach((url,value)-> value.forEach((requestMethod, info) -> {
            String permissionName = info.get("summary").toString();
            List<String> tag = (List<String>) info.get("tags");
            Integer parentId = permissionMap.get(tag.get(0));
            Resource resource = Resource.builder()
                    .resourceName(permissionName)
                    .url(url.replaceAll("\\{[^}]*\\}", "*"))
                    .parentId(parentId)
                    .requestMethod(requestMethod.toUpperCase())
                    .isAnonymous(FALSE)
                    .build();
            resourceList.add(resource);
        }));
        this.saveBatch(resourceList);
    }

    @Override
    public void saveOrUpdateResource(ResourceVO resourceVO) {
        Resource resource = BeanCopyUtils.copyObject(resourceVO, Resource.class);
        this.saveOrUpdate(resource);
        //重新加载角色信息
    }


    @Override
    public void deleteResource(Integer resourceId) {
        Integer count = Math.toIntExact(roleResourceMapper.selectCount(new LambdaQueryWrapper<RoleResource>()
                .eq(RoleResource::getResourceId, resourceId)));
        if (count > 0){
            throw new ServiceException("存在角色", CODE_500);
        }

        List<Integer> resourceIdList = resourceMapper.selectList(new LambdaQueryWrapper<Resource>()
                        .select(Resource::getId)
                        .eq(Resource::getParentId, resourceId))
                .stream()
                .map(Resource::getId)
                .collect(Collectors.toList());

        resourceIdList.add(resourceId);
        resourceMapper.deleteBatchIds(resourceIdList);
    }
}
