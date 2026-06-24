package com.familymedical.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.familymedical.entity.DoctorInfo;
import com.familymedical.entity.User;
import com.familymedical.mapper.DoctorInfoMapper;
import com.familymedical.mapper.UserMapper;
import com.familymedical.service.DoctorInfoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DoctorInfoServiceImpl extends ServiceImpl<DoctorInfoMapper, DoctorInfo> implements DoctorInfoService {

    private final UserMapper userMapper;

    @Override
    public DoctorInfo getByUserId(Long userId) {
        return lambdaQuery().eq(DoctorInfo::getUserId, userId).one();
    }

    @Override
    public void saveOrUpdateDoctor(DoctorInfo doctorInfo) {
        DoctorInfo existing = getByUserId(doctorInfo.getUserId());
        if (existing != null) {
            doctorInfo.setId(existing.getId());
            updateById(doctorInfo);
        } else {
            save(doctorInfo);
        }
    }

    @Override
    public IPage<Map<String, Object>> getDoctorPage(int page, int size, String department, String keyword) {
        // 查医生用户
        LambdaQueryWrapper<User> userWrapper = new LambdaQueryWrapper<>();
        userWrapper.eq(User::getRole, 2).eq(User::getStatus, 1);
        if (StringUtils.hasText(keyword)) {
            userWrapper.and(w -> w.like(User::getRealName, keyword).or().like(User::getUsername, keyword));
        }
        IPage<User> userPage = userMapper.selectPage(new Page<>(page, size), userWrapper);
        List<Long> userIds = userPage.getRecords().stream().map(User::getId).collect(Collectors.toList());

        // 查医生详情
        final Map<Long, DoctorInfo> infoMap;
        if (!userIds.isEmpty()) {
            List<DoctorInfo> infos = lambdaQuery().in(DoctorInfo::getUserId, userIds).list();
            infoMap = infos.stream().collect(Collectors.toMap(DoctorInfo::getUserId, d -> d));
        } else {
            infoMap = new HashMap<>();
        }

        // 组装结果
        IPage<Map<String, Object>> result = new Page<>(page, size, userPage.getTotal());
        List<Map<String, Object>> records = userPage.getRecords().stream().map(u -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", u.getId());
            map.put("username", u.getUsername());
            map.put("realName", u.getRealName());
            map.put("phone", u.getPhone());
            map.put("avatar", u.getAvatar());
            map.put("gender", u.getGender());
            DoctorInfo info = infoMap.get(u.getId());
            if (info != null) {
                map.put("department", info.getDepartment());
                map.put("title", info.getTitle());
                map.put("specialty", info.getSpecialty());
                map.put("introduction", info.getIntroduction());
                map.put("consultFee", info.getConsultFee());
                map.put("workYears", info.getWorkYears());
            }
            return map;
        }).collect(Collectors.toList());
        result.setRecords(records);
        return result;
    }

    @Override
    public Map<String, Object> getDoctorDetail(Long userId) {
        User user = userMapper.selectById(userId);
        DoctorInfo info = getByUserId(userId);
        Map<String, Object> map = new HashMap<>();
        if (user != null) {
            map.put("id", user.getId());
            map.put("username", user.getUsername());
            map.put("realName", user.getRealName());
            map.put("phone", user.getPhone());
            map.put("avatar", user.getAvatar());
            map.put("gender", user.getGender());
        }
        if (info != null) {
            map.put("department", info.getDepartment());
            map.put("title", info.getTitle());
            map.put("specialty", info.getSpecialty());
            map.put("introduction", info.getIntroduction());
            map.put("consultFee", info.getConsultFee());
            map.put("workYears", info.getWorkYears());
        }
        return map;
    }
}
