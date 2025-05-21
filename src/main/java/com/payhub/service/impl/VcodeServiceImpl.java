package com.payhub.service.impl;

import com.payhub.mapper.VcodeMapper;
import com.payhub.pojo.Vcode;
import com.payhub.service.EmailService;
import com.payhub.service.VcodeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // 引入事务注解

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Random;

@Service
public class VcodeServiceImpl implements VcodeService {

    private static final Logger logger = LoggerFactory.getLogger(VcodeServiceImpl.class);

    @Value("${vcode.length:6}")
    private int codeLength;

    @Value("${vcode.validity.minutes:5}")
    private int validityInMinutes;

    private final VcodeMapper vcodeMapper;
    private final EmailService emailService; // EmailService 仍然需要，即使 verifyCode 不直接用它
    private final Random random = new SecureRandom();

    @Autowired
    public VcodeServiceImpl(VcodeMapper vcodeMapper, EmailService emailService) {
        this.vcodeMapper = vcodeMapper;
        this.emailService = emailService; // EmailService 注入保留
    }

    @Override
    @Transactional // 建议将整个操作设为事务性
    public void generateAndSendCode(String email, String type) {
        logger.info("开始为邮箱 [{}] 生成类型为 [{}] 的验证码", email, type);
        String normalizedType = type.trim().toUpperCase();

        String code = generateRandomCode(codeLength);
        logger.debug("为邮箱 [{}] 生成的验证码是: {}", email, code);

        Vcode vcode = new Vcode();
        vcode.setEmail(email);
        vcode.setCode(code);
        vcode.setType(normalizedType);
        vcode.setCreatedAt(LocalDateTime.now());
        vcode.setExpiresAt(LocalDateTime.now().plusMinutes(validityInMinutes));
        vcode.setUsed(false);

        vcodeMapper.insert(vcode);
//        if (insertedRows == 0) {
//            logger.error("验证码未能保存到数据库 (邮箱: {}, 类型: {})", email, normalizedType);
//            throw new RuntimeException("系统错误：保存验证码失败，请稍后重试。");
//        }
//        logger.info("验证码 (ID: {}) 已成功保存到数据库，关联邮箱 [{}]", vcode.getId(), email);

        String emailSubject = String.format("您的 %s 验证码", getApplicationName());
        String emailBody = String.format(
                "尊敬的用户：\n\n" +
                        "您正在进行 %s 操作，您的验证码是：\n\n" +
                        "【 %s 】\n\n" +
                        "此验证码将在 %d 分钟后失效。请勿将验证码泄露给他人。\n\n" +
                        "如非本人操作，请忽略此邮件。\n\n" +
                        "感谢您的使用！\n" +
                        "%s团队",
                getPurposeDescription(normalizedType),
                code,
                validityInMinutes,
                getApplicationName()
        );

        try {
            emailService.sendEmail(email, emailSubject, emailBody);
//            logger.info("验证码邮件已成功请求发送至邮箱 [{}]", email);
        } catch (RuntimeException e) {
//            logger.error("发送验证码邮件至 [{}] 失败: {}", email, e.getMessage());
            throw new RuntimeException("验证码邮件发送失败：" + e.getMessage(), e);
        }
    }

    /**
     * 校验用户提供的验证码是否有效。
     * 如果有效，则将其标记为已使用。
     *
     * @param email 用户的邮箱地址。
     * @param type  验证码的类型。
     * @param code  用户提交的验证码字符串。
     * @return 如果验证码有效且成功通过验证并标记为已用，则返回 {@code true}；否则返回 {@code false}。
     * @throws RuntimeException 如果在校验过程中发生意外的系统错误（例如数据库连接问题）。
     */
    @Override
    @Transactional // 建议将验证和标记操作设为事务性，确保原子性
    public boolean verifyCode(String email, String type, String code) {
        if (email == null || type == null || code == null) {
            logger.warn("验证码校验失败：email, type, 或 code 为空。");
            return false;
        }
        String normalizedType = type.trim().toUpperCase();
        logger.info("尝试校验验证码：邮箱 [{}], 类型 [{}], 验证码 [{}]", email, normalizedType, code);

        Vcode vcode = vcodeMapper.findValidCode(email, normalizedType, code);

        if (vcode != null) {
            // 找到了有效的、未使用的、未过期的验证码
            logger.info("找到有效验证码记录 (ID: {})，尝试标记为已使用...", vcode.getId());
            try {
                // 假设 VcodeMapper.markAsUsed(Long id) 返回受影响的行数 (int)
                // 并且其 SQL 实现包含了 AND used = FALSE AND expires_at > CURRENT_TIMESTAMP 以防止重复标记或标记过期码
                // 如果您的 VcodeMapper.markAsUsed 返回 void，您需要调整这里的逻辑，
                // 可能需要承担一定的风险（如果并发场景下，在 find 和 mark 之间状态改变）。
                vcodeMapper.markAsUsed(vcode.getId());
                // 更好的做法是让 markAsUsed 返回 int (受影响的行数)
                // int rowsAffected = vcodeMapper.markAsUsed(vcode.getId());
                // if (rowsAffected > 0) {
                //    logger.info("验证码 (ID: {}) 已成功标记为已使用。", vcode.getId());
                //    return true;
                // } else {
                //    logger.warn("标记验证码 (ID: {}) 为已使用失败，可能已被其他请求使用或刚过期。", vcode.getId());
                //    return false; // 虽然找到了，但未能成功标记为已用
                // }
                logger.info("验证码 (ID: {}) 已请求标记为已使用 (假设mapper.markAsUsed是void且SQL条件严格)。", vcode.getId());
                return true; // 如果 markAsUsed 是 void，我们乐观地假设它成功了（前提是SQL条件足够严格）
            } catch (Exception e) {
                logger.error("标记验证码 (ID: {}) 为已使用时发生数据库错误: {}", vcode.getId(), e.getMessage(), e);
                // 根据业务需求，这里可以抛出异常或返回false
                // 抛出异常会回滚事务（如果方法上有@Transactional）
                throw new RuntimeException("更新验证码状态失败，请稍后重试。", e);
            }
        } else {
            logger.warn("未找到有效的验证码记录，或验证码不匹配/已使用/已过期 (邮箱: {}, 类型: {}, 码: {})", email, normalizedType, code);
            return false;
        }
    }

    private String generateRandomCode(int length) {
        if (length <= 0) {
            throw new IllegalArgumentException("验证码长度必须为正数。");
        }
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(random.nextInt(10));
        }
        return sb.toString();
    }

    private String getPurposeDescription(String type) {
        switch (type) { // normalizedType 已转大写
            case "REGISTRATION":
                return "新用户注册";
            case "PASSWORD_RESET":
                return "密码重置";
            case "EMAIL_VERIFICATION":
                return "邮箱地址验证";
            default:
                return "账户安全操作";
        }
    }

    private String getApplicationName() {
        return "PayHub"; // 可以从配置读取
    }
}