package com.payhub.controller;

import com.payhub.pojo.CreditCard;
import com.payhub.pojo.CreditCardTransaction;
import com.payhub.pojo.Result;
import com.payhub.service.CreditCardService;
import com.payhub.utils.UserContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/credit-card")
@CrossOrigin(origins = "*")
public class CreditCardController {

    @Autowired
    private CreditCardService creditCardService;

    @GetMapping("/{id}")
    public Result<CreditCard> getCreditCard(@PathVariable Long id) {
        try {
            CreditCard creditCard = creditCardService.getCreditCardById(id);
            if (creditCard == null) {
                return Result.error("信用卡不存在");
            }
            return Result.success(creditCard, 200);
        } catch (Exception e) {
            return Result.error("查询信用卡失败：" + e.getMessage());
        }
    }

    @GetMapping("/card-number/{cardNumber}")
    public Result<CreditCard> getCreditCardByCardNumber(@PathVariable String cardNumber) {
        try {
            CreditCard creditCard = creditCardService.getCreditCardByCardNumber(cardNumber);
            if (creditCard == null) {
                return Result.error("信用卡不存在");
            }
            return Result.success(creditCard, 200);
        } catch (Exception e) {
            return Result.error("查询信用卡失败：" + e.getMessage());
        }
    }

    @GetMapping("/my-cards")
    public Result<List<CreditCard>> getMyCreditCards() {
        try {
            Long accountId = UserContext.getAccountId();
            if (accountId == null) {
                System.out.println("用户未登录");
                return Result.error("用户未登录");
            }
            
            List<CreditCard> creditCards = creditCardService.getCreditCardsByAccountId(accountId);
            return Result.success(creditCards, 200);
        } catch (Exception e) {
            return Result.error("查询信用卡失败：" + e.getMessage());
        }
    }

    @PostMapping("/{id}/report-lost")
    public Result<Boolean> reportLost(@PathVariable Long id, @RequestBody Map<String, String> request) {
        String reason = request.get("reason");
        if (reason == null || reason.trim().isEmpty()) {
            return Result.error("挂失原因不能为空");
        }
        return Result.success(creditCardService.reportLost(id, reason), 200);
    }

    @PostMapping("/{id}/cancel-lost")
    public Result<Boolean> cancelLost(@PathVariable Long id) {
        try {
            // 检查是否是管理员
            Integer userFlag = UserContext.getUserFlag();
            if (userFlag == null || userFlag != 2) {
                return Result.error("无权限操作");
            }
            
            boolean result = creditCardService.cancelLost(id);
            return Result.success(result, 200);
        } catch (IllegalArgumentException e) {
            return Result.error(e.getMessage());
        } catch (IllegalStateException e) {
            return Result.error(e.getMessage());
        } catch (Exception e) {
            return Result.error("解挂信用卡失败：" + e.getMessage());
        }
    }

    @PostMapping("/{id}/consume")
    public Result<CreditCardTransaction> consume(@PathVariable Long id,
                                               @RequestBody CreditCardTransactionRequest request) {
        try {
            if (request == null || request.getAmount() == null || request.getDescription() == null) {
                return Result.error("参数不完整");
            }
            CreditCardTransaction transaction = creditCardService.consume(id, request.getAmount(), request.getDescription());
            return Result.success(transaction, 200);
        } catch (IllegalArgumentException e) {
            return Result.error(e.getMessage());
        } catch (IllegalStateException e) {
            return Result.error(e.getMessage());
        } catch (Exception e) {
            return Result.error("消费失败：" + e.getMessage());
        }
    }

    @PostMapping("/{id}/repay")
    public Result<CreditCardTransaction> repay(@PathVariable Long id,
                                             @RequestBody CreditCardTransactionRequest request) {
        try {
            if (request == null || request.getAmount() == null || request.getDescription() == null) {
                return Result.error("参数不完整");
            }
            CreditCardTransaction transaction = creditCardService.repay(id, request.getAmount(), request.getDescription());
            return Result.success(transaction, 200);
        } catch (IllegalArgumentException e) {
            return Result.error(e.getMessage());
        } catch (IllegalStateException e) {
            return Result.error(e.getMessage());
        } catch (Exception e) {
            return Result.error("还款失败：" + e.getMessage());
        }
    }

    @GetMapping("/{id}/transactions")
    public Result<List<CreditCardTransaction>> getTransactions(@PathVariable Long id) {
        List<CreditCardTransaction> transactions = creditCardService.getTransactions(id);
        return Result.success(transactions, 200);
    }

    @GetMapping("/{id}/transactions/{type}")
    public Result<List<CreditCardTransaction>> getTransactionsByType(@PathVariable Long id,
                                                                   @PathVariable String type) {
        List<CreditCardTransaction> transactions = creditCardService.getTransactionsByType(id, type);
        return Result.success(transactions, 200);
    }
}

// 请求体类
class CreditCardTransactionRequest {
    private BigDecimal amount;
    private String description;

    // Getters and Setters
    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
} 