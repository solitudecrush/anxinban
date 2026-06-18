package com.anxinban.dto;

/**
 * 用户注册请求参数封装类。
 *
 * <p>封装前端注册界面提交的账号、密码、姓名、验证码、用户类型等信息。
 * 家属用户注册时可能需要关联老人（elderId + relation），
 * 由 {@link com.anxinban.controller.AuthController} 接收后交给
 * {@link com.anxinban.service.AuthService} 完成注册。</p>
 */
public class RegisterRequest {

    /** 注册手机号，作为登录账号，需保证唯一性 */
    private String phone;

    /** 登录密码，建议由前端进行初步加密或在服务端进行哈希处理 */
    private String password;

    /** 用户真实姓名 */
    private String name;

    /** 短信验证码，用于校验手机号归属，示例：123456 */
    private String verifyCode;

    /**
     * 用户类型。
     *
     * <p>常见取值：</p>
     * <ul>
     *   <li>elder：老人用户</li>
     *   <li>family：家属用户</li>
     *   <li>staff：工作人员</li>
     * </ul>
     */
    private String userType;

    /**
     * 关联的老人用户 ID。
     *
     * <p>主要用于家属注册时绑定被照护老人，老人自身注册时可为空。</p>
     */
    private String elderId;

    /**
     * 与老人的关系。
     *
     * <p>常见取值：子女、配偶、护工、志愿者等。</p>
     */
    private String relation;

    /**
     * 获取注册手机号。
     *
     * @return 手机号
     */
    public String getPhone() {
        return phone;
    }

    /**
     * 设置注册手机号。
     *
     * @param phone 手机号
     */
    public void setPhone(String phone) {
        this.phone = phone;
    }

    /**
     * 获取登录密码。
     *
     * @return 密码
     */
    public String getPassword() {
        return password;
    }

    /**
     * 设置登录密码。
     *
     * @param password 密码
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * 获取用户姓名。
     *
     * @return 姓名
     */
    public String getName() {
        return name;
    }

    /**
     * 设置用户姓名。
     *
     * @param name 姓名
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * 获取短信验证码。
     *
     * @return 验证码
     */
    public String getVerifyCode() {
        return verifyCode;
    }

    /**
     * 设置短信验证码。
     *
     * @param verifyCode 验证码
     */
    public void setVerifyCode(String verifyCode) {
        this.verifyCode = verifyCode;
    }

    /**
     * 获取用户类型。
     *
     * @return 用户类型
     */
    public String getUserType() {
        return userType;
    }

    /**
     * 设置用户类型。
     *
     * @param userType 用户类型
     */
    public void setUserType(String userType) {
        this.userType = userType;
    }

    /**
     * 获取关联老人 ID。
     *
     * @return 老人 ID
     */
    public String getElderId() {
        return elderId;
    }

    /**
     * 设置关联老人 ID。
     *
     * @param elderId 老人 ID
     */
    public void setElderId(String elderId) {
        this.elderId = elderId;
    }

    /**
     * 获取与老人的关系。
     *
     * @return 关系描述
     */
    public String getRelation() {
        return relation;
    }

    /**
     * 设置与老人的关系。
     *
     * @param relation 关系描述
     */
    public void setRelation(String relation) {
        this.relation = relation;
    }
}
