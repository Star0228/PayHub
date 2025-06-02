package com.payhub.controller;

import com.payhub.pojo.CreditCardApplication;
import com.payhub.pojo.Result;
import com.payhub.service.CreditCardApplicationService;
import com.payhub.utils.UserContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/credit-card/application")
public class CreditCardApplicationController {

    @Autowired
    private CreditCardApplicationService applicationService;

    @PostMapping("/create")
    public Result<CreditCardApplication> createApplication() {
        try {
            Long accountId = UserContext.getAccountId();
            if (accountId == null) {
                return Result.error("用户未登录");
            }
            
            CreditCardApplication application = applicationService.createApplication(accountId);
            return Result.success(application, 200);
        } catch (IllegalArgumentException e) {
            return Result.error(e.getMessage());
        } catch (IllegalStateException e) {
            return Result.error(e.getMessage());
        } catch (Exception e) {
            return Result.error("创建申请失败：" + e.getMessage());
        }
    }

    @GetMapping("/my")
    public Result<List<CreditCardApplication>> getMyApplications() {
        try {
            Long accountId = UserContext.getAccountId();
            if (accountId == null) {
                return Result.error("用户未登录");
            }
            
            List<CreditCardApplication> applications = applicationService.getAllApplicationsByCurrentUser(accountId);
            return Result.success(applications, 200);
        } catch (Exception e) {
            return Result.error("查询申请记录失败：" + e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public Result<CreditCardApplication> getApplication(@PathVariable Long id) {
        try {
            CreditCardApplication application = applicationService.getApplicationById(id);
            if (application == null) {
                return Result.error("申请记录不存在");
            }
            return Result.success(application, 200);
        } catch (Exception e) {
            return Result.error("查询申请失败：" + e.getMessage());
        }
    }

    @GetMapping("/status/{status}")
    public Result<List<CreditCardApplication>> getApplicationsByStatus(@PathVariable String status) {
        try {
            // 检查是否是管理员
            Integer userFlag = UserContext.getUserFlag();
            if (userFlag == null || userFlag != 2) {
                return Result.error("无权限查看");
            }
            
            List<CreditCardApplication> applications = applicationService.getApplicationsByStatus(status);
            return Result.success(applications, 200);
        } catch (Exception e) {
            return Result.error("查询申请记录失败：" + e.getMessage());
        }
    }

    @PostMapping("/status")
    public Result<Boolean> updateApplicationStatus(@RequestBody CreditCardApplicationStatusRequest request) {
        try {
            // 参数验证
            if (request == null || request.getStatus() == null || request.getId() == null) {
                return Result.error("参数不完整");
            }

            // 检查是否是管理员
            Integer userFlag = UserContext.getUserFlag();
            if (userFlag == null || userFlag != 2) {
                return Result.error("无权限操作");
            }
            
            // 获取当前登录用户ID
            Long currentUserId = UserContext.getAccountId();
            if (currentUserId == null) {
                return Result.error("用户未登录");
            }
            
            // 验证状态值
            if (!isValidStatus(request.getStatus())) {
                return Result.error("无效的状态值");
            }
            
            boolean result = applicationService.updateApplicationStatus(request.getId(), request.getStatus(), currentUserId);
            if (result) {
                return Result.success(true, 200);
            } else {
                return Result.error("审核状态更新失败");
            }
        } catch (IllegalArgumentException e) {
            return Result.error(e.getMessage());
        } catch (IllegalStateException e) {
            return Result.error(e.getMessage());
        } catch (Exception e) {
            return Result.error("更新申请状态失败：" + e.getMessage());
        }
    }

    // 验证状态值是否有效
    private boolean isValidStatus(String status) {
        return status != null && 
               (status.equals("PENDING") || 
                status.equals("APPROVED") || 
                status.equals("REJECTED"));
    }
}

// 请求体类
class CreditCardApplicationStatusRequest {
    private Long id;
    private String status;

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
} 