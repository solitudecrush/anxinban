package com.anxinban.service;

import com.anxinban.dto.LoginByClientRequest;
import com.anxinban.dto.LoginRequest;
import com.anxinban.dto.LoginResponse;
import com.anxinban.dto.RegisterRequest;
import com.anxinban.dto.StaffDto;
import com.anxinban.entity.FamilyUser;
import com.anxinban.entity.StaffUser;
import com.anxinban.mapper.FamilyUserRepository;
import com.anxinban.mapper.StaffUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 认证授权业务服务。
 *
 * <p>负责登录、注册、密码重置等认证相关业务逻辑。
 * 登录按客户端方向分离：Web 端仅允许工作人员登录，App 端仅允许家属登录，
 * 老人用户不参与认证流程。</p>
 *
 * @author 安心伴开发团队
 * @since 0.0.2-SNAPSHOT
 */
@Service
public class AuthService {

    private final StaffUserRepository staffUserRepository;
    private final FamilyUserRepository familyUserRepository;

    @Autowired
    public AuthService(StaffUserRepository staffUserRepository,
                       FamilyUserRepository familyUserRepository) {
        this.staffUserRepository = staffUserRepository;
        this.familyUserRepository = familyUserRepository;
    }

    // ==================== 登录 ====================

    /**
     * Web 端工作人员登录。
     *
     * <p>仅查询 staff 表，验证手机号和密码。</p>
     *
     * @param phone    工作人员手机号
     * @param password 密码
     * @return 登录成功返回 LoginResponse，失败返回 null
     */
    public LoginResponse staffLogin(String phone, String password) {
        StaffUser staff = staffUserRepository.findByPhone(phone);
        if (staff == null || !password.equals(staff.getPassword())) {
            return null;
        }
        LoginResponse response = new LoginResponse();
        response.setAccessToken("token-" + UUID.randomUUID());
        response.setRefreshToken("refresh-" + UUID.randomUUID());
        response.setUserId(staff.getStaffId());
        response.setName(staff.getName());
        response.setPhone(staff.getPhone());
        response.setRole(staff.getRole());
        response.setCommunityId(staff.getCommunityId());
        response.setAvatar(staff.getAvatar());
        return response;
    }

    /**
     * App 端家属登录。
     *
     * <p>仅查询 family_user 表，验证手机号和密码。</p>
     *
     * @param phone    家属手机号
     * @param password 密码
     * @return 登录成功返回 LoginResponse，失败返回 null
     */
    public LoginResponse familyLogin(String phone, String password) {
        FamilyUser family = familyUserRepository.findByPhone(phone);
        if (family == null || !password.equals(family.getPassword())) {
            return null;
        }
        LoginResponse response = new LoginResponse();
        response.setAccessToken("token-" + UUID.randomUUID());
        response.setRefreshToken("refresh-" + UUID.randomUUID());
        response.setUserId(family.getFamilyId());
        response.setName(family.getName());
        response.setPhone(family.getPhone());
        response.setRole("family");
        response.setAvatar(family.getAvatar());
        return response;
    }

    /**
     * 旧版登录方法（保留向后兼容）。
     *
     * <p>根据 userType 转发到对应的登录方法。
     * elder（老人）类型不再支持登录，直接返回 null。</p>
     *
     * @param request 登录请求
     * @return 登录成功返回 LoginResponse，失败返回 null
     * @see #staffLogin(String, String)
     * @see #familyLogin(String, String)
     */
    public LoginResponse login(LoginRequest request) {
        if ("staff".equals(request.getUserType())) {
            return staffLogin(request.getPhone(), request.getPassword());
        } else if ("family".equals(request.getUserType())) {
            return familyLogin(request.getPhone(), request.getPassword());
        }
        // elder 及其他类型不支持登录
        return null;
    }

    // ==================== 注册 ====================

    /**
     * 用户注册方法。
     *
     * <p>仅支持 staff 和 family 注册，老人（elder）不能自行注册。
     * 手机号已存在时返回 null。</p>
     *
     * @param request 注册请求
     * @return 注册成功返回 LoginResponse，失败返回 null
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
            staff.setUpdatedAt(LocalDateTime.now());
            staffUserRepository.save(staff);
            LoginResponse response = new LoginResponse();
            response.setAccessToken("token-" + UUID.randomUUID());
            response.setRefreshToken("refresh-" + UUID.randomUUID());
            response.setUserId(staff.getStaffId());
            response.setName(staff.getName());
            response.setPhone(staff.getPhone());
            response.setRole(staff.getRole());
            return response;
        } else if ("family".equals(request.getUserType())) {
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
            family.setUpdatedAt(LocalDateTime.now());
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
        // elder 及其他类型不支持注册
        return null;
    }

    // ==================== 密码重置 ====================

    /**
     * 重置密码。
     *
     * <p>仅支持 staff 和 family 用户，老人不支持。</p>
     *
     * @param phone       用户手机号
     * @param newPassword 新密码
     * @return 重置成功返回 true，用户不存在返回 false
     */
    public boolean resetPassword(String phone, String newPassword) {
        StaffUser staff = staffUserRepository.findByPhone(phone);
        if (staff != null) {
            staff.setPassword(newPassword);
            staff.setUpdatedAt(LocalDateTime.now());
            staffUserRepository.save(staff);
            return true;
        }
        FamilyUser family = familyUserRepository.findByPhone(phone);
        if (family != null) {
            family.setPassword(newPassword);
            family.setUpdatedAt(LocalDateTime.now());
            familyUserRepository.save(family);
            return true;
        }
        return false;
    }

    // ==================== 用户查询 ====================

    /**
     * 根据手机号查询当前登录用户（staff 或 family）。
     *
     * <p>优先查 staff 表，未找到再查 family 表，
     * 返回统一的 {@link StaffDto}（family 用户部分字段为 null）。</p>
     *
     * @param phone 用户手机号
     * @return 用户信息 DTO，未找到返回 null
     */
    public StaffDto getStaffByPhone(String phone) {
        // 先查 staff
        StaffUser staff = staffUserRepository.findByPhone(phone);
        if (staff != null) {
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
        // 再查 family
        FamilyUser family = familyUserRepository.findByPhone(phone);
        if (family != null) {
            StaffDto dto = new StaffDto();
            dto.setStaffId(family.getFamilyId());
            dto.setUsername(family.getPhone());
            dto.setName(family.getName());
            dto.setPhone(family.getPhone());
            dto.setRole("family");
            dto.setAvatar(family.getAvatar());
            return dto;
        }
        return null;
    }

    /**
     * 根据 staffId 获取工作人员信息。
     *
     * @param staffId 工作人员 ID
     * @return 工作人员 DTO，未找到返回 null
     */
    public StaffDto getStaffById(String staffId) {
        StaffUser staff = staffUserRepository.findByStaffId(staffId);
        if (staff == null) {
            return null;
        }
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
