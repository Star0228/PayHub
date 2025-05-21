package com.payhub.service;

/**
 * 验证码服务接口 (VcodeService)
 * 定义了与生成、发送和校验验证码相关的业务逻辑。
 */
public interface VcodeService {

    /**
     * 生成一个特定类型的验证码，将其存储起来，并通过邮件发送给指定的用户邮箱。
     *
     * <p>此方法应处理：</p>
     * <ul>
     * <li>生成一个随机且安全的验证码。</li>
     * <li>设置验证码的有效期。</li>
     * <li>将验证码信息（包括邮箱、类型、码本身、创建时间、过期时间、使用状态）保存到数据库。</li>
     * <li>调用邮件服务将验证码发送到用户邮箱。</li>
     * <li>处理过程中可能发生的各种异常（例如，无效的邮箱格式、不支持的验证码类型、数据库操作失败、邮件发送失败、达到频率限制等）。</li>
     * </ul>
     *
     * @param email 目标用户的邮箱地址。此邮箱地址将用于接收验证码。
     * @param type  验证码的类型，例如 "REGISTRATION"（用于新用户注册），"PASSWORD_RESET"（用于密码重置）等。
     * 服务实现层应根据此类型执行相应的业务校验（如果需要）并生成对应内容的邮件。
     * @throws IllegalArgumentException 如果提供的 email 或 type 无效（例如格式错误、类型不支持）。
     * @throws RuntimeException         如果在处理过程中发生其他错误，例如数据库持久化失败、邮件发送服务失败或超出频率限制等。
     * 具体的实现可能会抛出更细化的自定义运行时异常。
     */
    void generateAndSendCode(String email, String type);

    /**
     * 校验用户提供的验证码是否有效。
     *
     * <p>一个验证码被认为是有效的，如果：</p>
     * <ul>
     * <li>它与提供的邮箱和类型匹配。</li>
     * <li>它与用户提供的码字符串一致。</li>
     * <li>它尚未被使用过。</li>
     * <li>它尚未过期。</li>
     * </ul>
     * <p>如果验证码有效，此方法通常还应将该验证码标记为“已使用”，以防止其被重复使用。</p>
     *
     * @param email 用户的邮箱地址。
     * @param type  验证码的类型。
     * @param code  用户提交的验证码字符串。
     * @return 如果验证码有效且成功通过验证，则返回 {@code true}；否则返回 {@code false}。
     * @throws RuntimeException 如果在校验过程中发生意外的系统错误（例如数据库连接问题）。
     */
    boolean verifyCode(String email, String type, String code);

}