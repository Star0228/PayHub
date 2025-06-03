package com.payhub.pojo;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 验证码实体类
 * 用于存储发送给用户的验证码信息，例如用于邮箱验证、密码重置等。
 */
@Data
public class Vcode {

    /**
     * 验证码记录的唯一ID (主键)
     */
    private Long id;

    /**
     * 验证码本身 (例如: "123456", "ABXY7Z")
     */
    private String code;

    /**
     * 接收验证码的目标邮箱地址
     * 这个邮箱可能用于新用户注册（此时用户表中可能还没有对应记录），
     * 或者用于现有用户的操作（如密码重置）。
     */
    private String email;

    /**
     * 验证码的类型 (例如: "REGISTRATION", "PASSWORD_RESET", "EMAIL_VERIFICATION")
     * 用于区分不同业务场景下的验证码。
     */
    private String type;

    /**
     * 验证码的创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 验证码的过期时间
     * 超过此时间的验证码将无效。
     */
    private LocalDateTime expiresAt;

    /**
     * 验证码是否已被使用
     * true 表示已使用，false 表示尚未使用。
     * 成功验证后应将其标记为已使用，以防重复使用。
     */
    private boolean used;

    /**
     * （可选）关联的用户ID
     * 如果验证码是为已存在的特定用户生成的（例如密码重置），可以存储用户ID。
     * 对于新用户注册时的邮箱验证，此字段可能为空。
     */
    private Long accountId;


    // Lombok @Data 会自动生成以下内容：
    // - Getters for all fields
    // - Setters for all fields
    // - toString() method
    // - equals() and hashCode() methods
    // - RequiredArgsConstructor (if no explicit constructor is defined that takes arguments)

    // 如果需要，您可以添加自定义的构造函数或方法。
    // 例如，一个方便设置创建时间和过期时间的构造函数：
    // public VerificationCode(String code, String email, String type, int validityDurationInMinutes) {
    //     this.code = code;
    //     this.email = email;
    //     this.type = type;
    //     this.createdAt = LocalDateTime.now();
    //     this.expiresAt = this.createdAt.plusMinutes(validityDurationInMinutes);
    //     this.used = false;
    // }
}