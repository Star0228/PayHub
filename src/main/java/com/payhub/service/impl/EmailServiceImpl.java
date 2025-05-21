package com.payhub.service.impl;

import com.payhub.service.EmailService;
// import org.slf4j.Logger; // Logger 导入已移除
// import org.slf4j.LoggerFactory; // LoggerFactory 导入已移除
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service // 标记为Spring服务组件
public class EmailServiceImpl implements EmailService {

    // private static final Logger logger = LoggerFactory.getLogger(EmailServiceImpl.class); // Logger 定义已移除

    private final JavaMailSender mailSender;

    // 从配置文件中读取发件人邮箱地址
    @Value("${payhub.mail.sender.from}")
    private String fromEmailAddress;

    @Autowired
    public EmailServiceImpl(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    /**
     * 发送邮件。
     *
     * @param to      收件人邮箱地址
     * @param subject 邮件主题
     * @param body    邮件内容 (支持HTML格式)
     * @throws RuntimeException 如果邮件发送失败
     */
    @Override
    public void sendEmail(String to, String subject, String body) {
        // logger.info("准备发送邮件至: {}, 主题: {}", to, subject); // Logger 调用已移除
        try {
            MimeMessage message = mailSender.createMimeMessage();

            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmailAddress); // 设置发件人
            helper.setTo(to);                 // 设置收件人
            helper.setSubject(subject);       // 设置主题
            helper.setText(body, true);       // 设置内容，第二个参数 true 表示内容是HTML格式

            mailSender.send(message); // 发送邮件
            // logger.info("邮件已成功发送至: {}", to); // Logger 调用已移除

        } catch (MessagingException e) {
            // logger.error("构建邮件消息失败 (收件人: {}): {}", to, e.getMessage(), e); // Logger 调用已移除
            // 仍然抛出异常，以便上层服务可以捕获并处理
            throw new RuntimeException("发送邮件失败：邮件消息构建或地址错误。", e);
        } catch (MailException e) {
            // logger.error("发送邮件至 {} 失败: {}", to, e.getMessage(), e); // Logger 调用已移除
            throw new RuntimeException("发送邮件失败，请检查邮件服务器配置或网络连接。", e);
        } catch (Exception e) {
            // logger.error("发送邮件至 {} 时发生未知错误: {}", to, e.getMessage(), e); // Logger 调用已移除
            throw new RuntimeException("发送邮件时发生未知错误，请联系管理员。", e);
        }
    }
}