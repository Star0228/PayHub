package com.payhub.controller;

import com.payhub.dto.SendCodeRequestDto; // 确保引入了DTO类
import com.payhub.pojo.Result;
import com.payhub.service.VcodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

@RestController
@RequestMapping("/vcode") // 根据您提供的代码，路径为 /vcode
@CrossOrigin(origins = "*")
public class VcodeController {

    // private static final Logger logger = LoggerFactory.getLogger(VcodeController.class); // Logger 定义已移除

    private static final List<String> SUPPORTED_VCODE_TYPES = Arrays.asList(
            "REGISTRATION",
            "PASSWORD_RESET",
            "EMAIL_VERIFICATION"
    );

    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$"
    );

    @Autowired
    private VcodeService vcodeService;

    @Value("${vcode.validity.minutes:5}")
    private int validityInMinutes;

    @PostMapping(value = "/send", produces = "application/json")
    public Result<?> sendVerificationCode(@RequestBody SendCodeRequestDto requestPayload) {
        // logger.info("接收到发送验证码请求: email='{}', type='{}'", requestPayload.getEmail(), requestPayload.getType()); // Logger 调用已移除

        String email = requestPayload.getEmail();
        String type = requestPayload.getType();

        if (email == null || email.trim().isEmpty()) {
            // logger.warn("发送验证码请求被拒绝：邮箱地址为空。"); // Logger 调用已移除 (原已注释)
            return Result.error("邮箱地址不能为空");
        }
        if (type == null || type.trim().isEmpty()) {
            // logger.warn("发送验证码请求被拒绝：邮箱 {} 的验证码类型为空。", email); // Logger 调用已移除 (原已注释)
            return Result.error("验证码类型不能为空");
        }

        if (!EMAIL_PATTERN.matcher(email).matches()) {
            // logger.warn("发送验证码请求被拒绝：邮箱 {} 的格式无效。", email); // Logger 调用已移除 (原已注释)
            return Result.error("无效的邮箱地址格式");
        }

        String normalizedType = type.trim().toUpperCase();
        if (!SUPPORTED_VCODE_TYPES.contains(normalizedType)) {
            // logger.warn("发送验证码请求被拒绝：邮箱 {} 的验证码类型 '{}' 不受支持。", email, type); // Logger 调用已移除
            return Result.error("不支持的验证码类型: " + type);
        }

        try {
            vcodeService.generateAndSendCode(email, normalizedType);

            // String successMessage = String.format( // 此变量不再需要，因为 Result.success() 不带参数
            //         "验证码已成功发送至您的邮箱，请注意查收。该验证码 %d 分钟内有效。",
            //         validityInMinutes
            // );
            // logger.info("对于邮箱 [{}], 类型 [{}]: {}", email, normalizedType, successMessage); // Logger 调用已移除
            return Result.success(); // 根据您的代码，此处返回不带参数的 success()
        } catch (IllegalArgumentException e) {
            // logger.warn("发送验证码参数错误 (邮箱: {}): {}", email, e.getMessage()); // Logger 调用已移除
            return Result.error(e.getMessage());
        } catch (RuntimeException e) {
            // logger.error("发送验证码服务异常 (邮箱: {}): {}", email, e.getMessage(), e); // Logger 调用已移除
            // 在移除logger后，如果仍希望在服务器端看到异常堆栈，可以让Spring的默认异常处理器处理，或者在这里手动打印
            // e.printStackTrace(); // 如果需要在控制台查看详细错误（不推荐用于生产环境的stdout）
            return Result.error(e.getMessage());
        } catch (Exception e) {
            // logger.error("发送验证码时发生意外错误 (邮箱: {}): {}", email, e.getMessage(), e); // Logger 调用已移除
            // e.printStackTrace(); // 如果需要在控制台查看详细错误
            return Result.error("验证码发送失败，系统繁忙，请稍后重试。");
        }
    }
}