package com.anxinban.service;

/**
 * Auth 业务服务类，处理 Auth 领域的业务逻辑。
 *
 * @author 安心伴开发团队
 * @since 0.0.1-SNAPSHOT
 */
import com.anxinban.dto.LoginRequest;
import com.anxinban.dto.LoginResponse;
import com.anxinban.dto.RegisterRequest;
import com.anxinban.dto.StaffDto;
import com.anxinban.entity.ElderUser;
import com.anxinban.entity.FamilyUser;
import com.anxinban.entity.StaffUser;
import com.anxinban.mapper.ElderUserRepository;
import com.anxinban.mapper.FamilyUserRepository;
import com.anxinban.mapper.StaffUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class AuthService {
    private final StaffUserRepository staffUserRepository;
    private final FamilyUserRepository familyUserRepository;
    /** 数据访问仓库，用于持久化操作 */
    private final ElderUserRepository elderUserRepository;

    @Autowired
    public AuthService(StaffUserRepository staffUserRepository, FamilyUserRepository familyUserRepository, ElderUserRepository elderUserRepository) {
        this.staffUserRepository = staffUserRepository;
        this.familyUserRepository = familyUserRepository;
        this.elderUserRepository = elderUserRepository;
    }

        /**
         * login 方法。
         *
         * @param request 字段含义待补充
         */
    public LoginResponse login(LoginRequest request) {
        LoginResponse response = new LoginResponse();
        if ("staff".equals(request.getUserType())) {
            StaffUser staff = staffUserRepository.findByPhone(request.getPhone());
            if (staff == null || !request.getPassword().equals(staff.getPassword())) {
                return null;
            }
            response.setUserId(staff.getStaffId());
            response.setName(staff.getName());
            response.setPhone(staff.getPhone());
            response.setRole(staff.getRole());
            response.setCommunityId(staff.getCommunityId());
            response.setAvatar(staff.getAvatar());
        } else if ("elder".equals(request.getUserType())) {
            ElderUser elder = elderUserRepository.findByPhone(request.getPhone());
            if (elder == null || !request.getPassword().equals(elder.getPassword())) {
                return null;
            }
            response.setUserId(elder.getElderId());
            response.setName(elder.getName());
            response.setPhone(elder.getPhone());
            response.setRole("elder");
            response.setAvatar(elder.getAvatar());
        } else {
            FamilyUser family = familyUserRepository.findByPhone(request.getPhone());
            if (family == null || !request.getPassword().equals(family.getPassword())) {
                return null;
            }
            response.setUserId(family.getFamilyId());
            response.setName(family.getName());
            response.setPhone(family.getPhone());
            response.setRole("family");
            response.setAvatar(family.getAvatar());
        }
        response.setAccessToken("token-" + UUID.randomUUID());
        response.setRefreshToken("refresh-" + UUID.randomUUID());
        return response;
    }

        /**
         * register 方法。
         *
         * @param request 字段含义待补充
         */
    public LoginResponse register(RegisterRequest request) {
        if ("staff".equals(request.getUserType())) {
            if (staffUserRepository.findByPhone(request.getPhone()) != null) {
                return null;
            }
            StaffUser staff = new StaffUser();
            staff.setStaffId("STF-" + UUID.randomUUID().toString().substring(0, 8));
            staff.setUsername(request.getPhone());
            staff.setName(request.getName());
            staff.setPhone(request.getPhone());
            staff.setPassword(request.getPassword());
            staff.setRole("staff");
            staff.setCreatedAt(LocalDateTime.now());
            staff.setUpdateTime(LocalDateTime.now());
            staffUserRepository.save(staff);
            LoginResponse response = new LoginResponse();
            response.setAccessToken("token-" + UUID.randomUUID());
            response.setRefreshToken("refresh-" + UUID.randomUUID());
            response.setUserId(staff.getStaffId());
            response.setName(staff.getName());
            response.setPhone(staff.getPhone());
            response.setRole(staff.getRole());
            return response;
        } else if ("elder".equals(request.getUserType())) {
            if (elderUserRepository.findByPhone(request.getPhone()) != null) {
                return null;
            }
            ElderUser elder = new ElderUser();
            elder.setElderId("ELDER-" + UUID.randomUUID().toString().substring(0, 8));
            elder.setName(request.getName());
            elder.setPhone(request.getPhone());
            elder.setPassword(request.getPassword());
            elder.setHealthStatus("normal");
            elder.setHealthStatusText("正常");
            elder.setHealthNote("");
            elder.setCreatedAt(LocalDateTime.now());
            elder.setUpdateTime(LocalDateTime.now());
            elderUserRepository.save(elder);
            LoginResponse response = new LoginResponse();
            response.setAccessToken("token-" + UUID.randomUUID());
            response.setRefreshToken("refresh-" + UUID.randomUUID());
            response.setUserId(elder.getElderId());
            response.setName(elder.getName());
            response.setPhone(elder.getPhone());
            response.setRole("elder");
            return response;
        } else {
            if (familyUserRepository.findByPhone(request.getPhone()) != null) {
                return null;
            }
            FamilyUser family = new FamilyUser();
            family.setFamilyId("FAM-" + UUID.randomUUID().toString().substring(0, 8));
            family.setName(request.getName());
            family.setPhone(request.getPhone());
            family.setPassword(request.getPassword());
            family.setElderId(request.getElderId());
            family.setRelation(request.getRelation());
            family.setCreatedAt(LocalDateTime.now());
            family.setUpdateTime(LocalDateTime.now());
            familyUserRepository.save(family);
            LoginResponse response = new LoginResponse();
            response.setAccessToken("token-" + UUID.randomUUID());
            response.setRefreshToken("refresh-" + UUID.randomUUID());
            response.setUserId(family.getFamilyId());
            response.setName(family.getName());
            response.setPhone(family.getPhone());
            response.setRole("family");
            return response;
        }
    }

        /**
         * resetPassword 方法。
         *
         * @param newPassword 新密码
         */
    public boolean resetPassword(String phone, String newPassword) {
        StaffUser staff = staffUserRepository.findByPhone(phone);
        if (staff != null) {
            staff.setPassword(newPassword);
            staff.setUpdateTime(LocalDateTime.now());
            staffUserRepository.save(staff);
            return true;
        }
        FamilyUser family = familyUserRepository.findByPhone(phone);
        if (family != null) {
            family.setPassword(newPassword);
            family.setUpdateTime(LocalDateTime.now());
            familyUserRepository.save(family);
            return true;
        }
        return false;
    }

        /**
         * 获取唯一标识，主键。
         *
         * @return 唯一标识，主键
         */
    public StaffDto getStaffById(String staffId) {
        StaffUser staff = staffUserRepository.findByStaffId(staffId);
        if (staff == null) return null;
        StaffDto dto = new StaffDto();
        dto.setStaffId(staff.getStaffId());
        dto.setUsername(staff.getUsername());
        dto.setName(staff.getName());
        dto.setPhone(staff.getPhone());
        dto.setRole(staff.getRole());
        dto.setCommunityId(staff.getCommunityId());
        dto.setAvatar(staff.getAvatar());
        return dto;
    }

        /**
         * 获取手机号。
         *
         * @return 手机号
         */
    public StaffDto getStaffByPhone(String phone) {
        StaffUser staff = staffUserRepository.findByPhone(phone);
        if (staff == null) return null;
        StaffDto dto = new StaffDto();
        dto.setStaffId(staff.getStaffId());
        dto.setUsername(staff.getUsername());
        dto.setName(staff.getName());
        dto.setPhone(staff.getPhone());
        dto.setRole(staff.getRole());
        dto.setCommunityId(staff.getCommunityId());
        dto.setAvatar(staff.getAvatar());
        return dto;
    }
}
