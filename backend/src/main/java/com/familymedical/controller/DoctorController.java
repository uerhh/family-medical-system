package com.familymedical.controller;

import com.familymedical.common.Constants;
import com.familymedical.common.Result;
import com.familymedical.entity.DoctorInfo;
import com.familymedical.entity.User;
import com.familymedical.service.DoctorInfoService;
import com.familymedical.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/doctor")
@RequiredArgsConstructor
public class DoctorController {

    private final DoctorInfoService doctorInfoService;
    private final UserService userService;

    /** 医生列表（患者选医生用） */
    @GetMapping("/list")
    public Result<?> list(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "12") int size,
            @RequestParam(required = false) String department,
            @RequestParam(required = false) String keyword) {
        return Result.success(doctorInfoService.getDoctorPage(page, size, department, keyword));
    }

    /** 医生详情 */
    @GetMapping("/detail/{userId}")
    public Result<?> detail(@PathVariable Long userId) {
        return Result.success(doctorInfoService.getDoctorDetail(userId));
    }

    /** 获取当前医生自己的详情 */
    @GetMapping("/my-info")
    public Result<?> myInfo(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute(Constants.USER_ID_KEY);
        return Result.success(doctorInfoService.getDoctorDetail(userId));
    }

    /** 医生更新自己的信息 */
    @PutMapping("/my-info")
    public Result<?> updateMyInfo(@RequestBody DoctorInfo info, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute(Constants.USER_ID_KEY);
        info.setUserId(userId);
        doctorInfoService.saveOrUpdateDoctor(info);
        return Result.success();
    }

    /** 管理员添加医生（创建用户+医生详情） */
    @PostMapping("/add")
    public Result<?> addDoctor(@RequestBody DoctorInfoRequest req) {
        // 创建用户
        User user = new User();
        user.setUsername(req.getUsername());
        user.setPassword(DigestUtils.md5DigestAsHex(req.getPassword().getBytes()));
        user.setRealName(req.getRealName());
        user.setPhone(req.getPhone());
        user.setGender(req.getGender());
        user.setRole(Constants.ROLE_DOCTOR);
        user.setStatus(Constants.STATUS_ENABLE);
        userService.save(user);

        // 创建医生详情
        DoctorInfo info = new DoctorInfo();
        info.setUserId(user.getId());
        info.setDepartment(req.getDepartment());
        info.setTitle(req.getTitle());
        info.setSpecialty(req.getSpecialty());
        info.setIntroduction(req.getIntroduction());
        info.setConsultFee(req.getConsultFee());
        info.setWorkYears(req.getWorkYears());
        doctorInfoService.save(info);

        return Result.success();
    }

    /** 管理员更新医生详情 */
    @PutMapping("/info/{userId}")
    public Result<?> updateDoctorInfo(@PathVariable Long userId, @RequestBody DoctorInfo info) {
        info.setUserId(userId);
        doctorInfoService.saveOrUpdateDoctor(info);
        return Result.success();
    }

    public static class DoctorInfoRequest {
        private String username;
        private String password;
        private String realName;
        private String phone;
        private Integer gender;
        private String department;
        private String title;
        private String specialty;
        private String introduction;
        private java.math.BigDecimal consultFee;
        private Integer workYears;

        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
        public String getRealName() { return realName; }
        public void setRealName(String realName) { this.realName = realName; }
        public String getPhone() { return phone; }
        public void setPhone(String phone) { this.phone = phone; }
        public Integer getGender() { return gender; }
        public void setGender(Integer gender) { this.gender = gender; }
        public String getDepartment() { return department; }
        public void setDepartment(String department) { this.department = department; }
        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
        public String getSpecialty() { return specialty; }
        public void setSpecialty(String specialty) { this.specialty = specialty; }
        public String getIntroduction() { return introduction; }
        public void setIntroduction(String introduction) { this.introduction = introduction; }
        public java.math.BigDecimal getConsultFee() { return consultFee; }
        public void setConsultFee(java.math.BigDecimal consultFee) { this.consultFee = consultFee; }
        public Integer getWorkYears() { return workYears; }
        public void setWorkYears(Integer workYears) { this.workYears = workYears; }
    }
}
