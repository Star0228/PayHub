package com.payhub.controller;

import com.payhub.service.ForexService;
import com.payhub.pojo.ForeignExchange;
import com.payhub.pojo.ForeignExchangeTrade;
import com.payhub.pojo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/forex")
@CrossOrigin(origins = "*")
public class ForexController {
    
    @Autowired
    private ForexService forexService;
    
    /**
     * 查询汇率
     */
    @GetMapping("/rate")
    public Result<Map<String, Object>> getExchangeRate(@RequestParam String currencyPair) {
        return forexService.getExchangeRate(currencyPair);
    }
    
    /**
     * 查询所有汇率
     */
    @GetMapping("/rates")
    public Result<List<ForeignExchange>> getAllExchangeRates() {
        return forexService.getAllExchangeRates();
    }
    
    /**
     * 外汇买卖
     */    @PostMapping("/trade")
    public Result<Map<String, Object>> executeTrade(@RequestBody Map<String, Object> request) {
        String accountId = (String) request.get("accountId");
        String currencyPair = (String) request.get("currencyPair");
        BigDecimal amount = new BigDecimal(request.get("amount").toString());
        String type = (String) request.get("type");
        return forexService.executeTrade(Long.parseLong(accountId), currencyPair, amount, type);
    }
      /**
     * 查询交易记录
     */    @GetMapping("/trades/{accountId}")
    public Result<List<ForeignExchangeTrade>> getTradeHistory(@PathVariable Long accountId) {
        return forexService.getTradeHistory(accountId);
    }
    
    /**
     * 查询交易详情
     */
    @GetMapping("/trade/{tradeId}")
    public Result<ForeignExchangeTrade> getTradeById(@PathVariable String tradeId) {
        return forexService.getTradeById(tradeId);
    }
}
