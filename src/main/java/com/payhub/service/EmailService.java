package com.payhub.service;

public interface EmailService {
    /**
     * 发送邮件。
     * @param to 收件人邮箱地址
     * @param subject 邮件主题
     * @param body 邮件内容 (可以是HTML或纯文本)
     * @throws RuntimeException 如果邮件发送失败
     */
    void sendEmail(String to, String subject, String body);
}