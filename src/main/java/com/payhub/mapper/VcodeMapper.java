package com.payhub.mapper;

import com.payhub.pojo.Vcode;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.time.LocalDateTime;

/**
 * VerificationCode (Vcode) Mapper 接口
 * 定义了与验证码数据表 (verification_codes) 相关的数据库操作。
 * 使用 MyBatis 注解方式。
 */
@Mapper // 声明这是一个 MyBatis Mapper 接口
public interface VcodeMapper {

    /**
     * 插入一条新的验证码记录。
     * 假设 verificationCode 对象中的 createdAt, expiresAt, 和 used (通常为 false) 字段已被设置。
     * 数据库会自动生成主键 id，并通过 @Options 注解回填到传入的 verificationCode对象的 id 属性中。
     */
    @Insert("INSERT INTO verification_codes (code, email, type, created_at, expires_at, used, user_id) " +
            "VALUES (#{code}, #{email}, #{type}, #{createdAt}, #{expiresAt}, #{used}, #{userId})")
    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
    void insert(Vcode verificationCode);

    /**
     * 根据邮箱、类型和验证码本身查找一个有效的验证码记录。
     * "有效" 指的是：记录存在、尚未使用 (used = FALSE) 且未过期 (expires_at > CURRENT_TIMESTAMP)。
     * CURRENT_TIMESTAMP 是 SQL 标准函数，用于获取当前时间戳，适用于大多数数据库。
     */
    @Select("SELECT id, code, email, type, created_at, expires_at, used, user_id " + // 明确列出字段是个好习惯
            "FROM verification_codes " +
            "WHERE email = #{email} AND type = #{type} AND code = #{code} " +
            "AND used = FALSE AND expires_at > CURRENT_TIMESTAMP " +
            "LIMIT 1")
    Vcode findValidCode(@Param("email") String email,
                                   @Param("type") String type,
                                   @Param("code") String code);

    /**
     * 将指定ID的验证码标记为已使用。
     * 为确保安全和幂等性，此操作仅在该验证码当前为未使用状态且尚未过期时执行。
     */
    @Update("UPDATE verification_codes SET used = TRUE " +
            "WHERE id = #{id} AND used = FALSE AND expires_at > CURRENT_TIMESTAMP")
    void markAsUsed(@Param("id") Long id); // 返回 void 以匹配 UserMapper 风格
    // （实际开发中，返回 int 表示受影响行数可能更有用）

    /**
     * 删除所有在指定时间点或之前已过期的验证码记录。
     * 用于定期清理数据库。
     */
    @Delete("DELETE FROM verification_codes WHERE expires_at <= #{currentTime}")
    void deleteExpiredCodes(@Param("currentTime") LocalDateTime currentTime); // 返回 void 以匹配 UserMapper 风格

    /**
     * (可选) 根据ID查找验证码记录。
     * 主要用于调试、管理或特定业务场景。
     */
    /*
    @Select("SELECT id, code, email, type, created_at, expires_at, used, user_id " +
            "FROM verification_codes WHERE id = #{id}")
    VerificationCode findById(@Param("id") Long id);
    */
}