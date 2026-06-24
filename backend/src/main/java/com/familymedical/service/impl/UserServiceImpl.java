package com.familymedical.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.familymedical.common.Constants;
import com.familymedical.entity.User;
import com.familymedical.mapper.UserMapper;
import com.familymedical.service.UserService;
import com.familymedical.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    private final JwtUtil jwtUtil;

    @Override
    public Map<String, Object> login(String username, String password) {
        User user = lambdaQuery()
                .eq(User::getUsername, username)
                .one();
        if (user == null) {
            throw new RuntimeException("用户名不存在");
        }
        String md5Pwd = DigestUtils.md5DigestAsHex(password.getBytes());
        if (!user.getPassword().equals(md5Pwd)) {
            throw new RuntimeException("密码错误");
        }
        if (user.getStatus() == Constants.STATUS_DISABLE) {
            throw new RuntimeException("账号已被禁用");
        }
        String token = jwtUtil.generateToken(user.getId(), user.getRole());
        Map<String, Object> result = new HashMap<>();
        result.put("token", token);
        result.put("user", user);
        return result;
    }

    @Override
    public void register(User user) {
        long count = lambdaQuery()
                .eq(User::getUsername, user.getUsername())
                .count();
        if (count > 0) {
            throw new RuntimeException("用户名已存在");
        }
        user.setPassword(DigestUtils.md5DigestAsHex(user.getPassword().getBytes()));
        user.setRole(Constants.ROLE_USER);
        user.setStatus(Constants.STATUS_ENABLE);
        save(user);
    }

    @Override
    public IPage<User> getUserPage(int page, int size, Integer role, String keyword) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        if (role != null) {
            wrapper.eq(User::getRole, role);
        }
        if (StringUtils.hasText(keyword)) {
            wrapper.and(w -> w.like(User::getUsername, keyword)
                    .or().like(User::getRealName, keyword)
                    .or().like(User::getPhone, keyword));
        }
        wrapper.orderByDesc(User::getCreateTime);
        return page(new Page<>(page, size), wrapper);
    }

    @Override
    public User getUserById(Long id) {
        User user = getById(id);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        user.setPassword(null);
        return user;
    }

    @Override
    public void updateUser(User user) {
        user.setPassword(null);
        updateById(user);
    }

    @Override
    public void deleteUser(Long id) {
        removeById(id);
    }

    @Override
    public List<User> getUsersByIds(List<Long> ids) {
        if (ids == null || ids.isEmpty()) return List.of();
        List<User> users = listByIds(ids);
        users.forEach(u -> u.setPassword(null));
        return users;
    }

    @Override
    public List<Map<String, Object>> getUserGrowthTrend() {
        return baseMapper.selectMaps(
                new QueryWrapper<User>()
                        .select("DATE_FORMAT(create_time, '%Y-%m') as month", "COUNT(*) as count")
                        .groupBy("DATE_FORMAT(create_time, '%Y-%m')")
                        .orderByAsc("month"));
    }
}
