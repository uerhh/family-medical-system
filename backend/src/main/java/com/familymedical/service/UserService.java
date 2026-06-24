package com.familymedical.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.familymedical.entity.User;

import java.util.List;
import java.util.Map;

public interface UserService extends IService<User> {
    Map<String, Object> login(String username, String password);
    void register(User user);
    IPage<User> getUserPage(int page, int size, Integer role, String keyword);
    User getUserById(Long id);
    void updateUser(User user);
    void deleteUser(Long id);
    List<User> getUsersByIds(List<Long> ids);

    List<Map<String, Object>> getUserGrowthTrend();
}
