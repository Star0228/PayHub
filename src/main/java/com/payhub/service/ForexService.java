package com.payhub.service;

import com.payhub.mapper.ForexMapper;
import com.payhub.mapper.AccountBalanceMapper;
import com.payhub.pojo.ForeignExchange;
import com.payhub.pojo.ForeignExchangeTrade;
import com.payhub.pojo.AccountBalance;
import com.payhub.pojo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class ForexService {
    
    @Autowired
    private ForexMapper forexMapper;
    
    @Autowired
    private AccountBalanceMapper accountBalanceMapper;
    
    /**
     * 查询汇率
     */
    public Result<Map<String, Object>> getExchangeRate(String currencyPair) {
        try {
            ForeignExchange exchange = forexMapper.findByCurrencyPair(currencyPair);
            if (exchange != null) {                Map<String, Object> data = new HashMap<>();
                data.put("currencyPair", currencyPair);
                data.put("rate", exchange.getCurrentRate());
                data.put("exchangeId", exchange.getForeignExchangeId());
                return Result.success(data, 1);
            } else {
                return Result.error("不支持的货币对: " + currencyPair);
            }
        } catch (Exception e) {
            return Result.error("查询汇率失败: " + e.getMessage());
        }
    }
    
    /**
     * 查询所有汇率
     */
    public Result<List<ForeignExchange>> getAllExchangeRates() {        try {
            List<ForeignExchange> exchanges = forexMapper.findAllExchangeRates();
            return Result.success(exchanges, exchanges.size());
        } catch (Exception e) {
            return Result.error("查询汇率失败: " + e.getMessage());
        }
    }
      /**
     * 执行外汇交易
     */
    @Transactional
    public Result<Map<String, Object>> executeTrade(Long accountId, String currencyPair, BigDecimal amount, String type) {
        try {
            // 参数校验
            if (accountId == null) {
                return Result.error("账户ID不能为空");
            }
            if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
                return Result.error("交易金额必须大于0");
            }
            if (!"BUY".equals(type) && !"SELL".equals(type)) {
                return Result.error("交易类型只能是BUY或SELL");
            }
            
            // 获取汇率信息
            ForeignExchange exchange = forexMapper.findByCurrencyPair(currencyPair);
            if (exchange == null) {
                return Result.error("不支持的货币对: " + currencyPair);
            }
            
            // 解析货币对
            String[] currencies = currencyPair.split("/");
            if (currencies.length != 2) {
                return Result.error("货币对格式错误");
            }
            String fromCurrency = currencies[0];
            String toCurrency = currencies[1];
            
            // 计算兑换金额
            BigDecimal rate = BigDecimal.valueOf(exchange.getCurrentRate());
            BigDecimal exchangedAmount;
            String sourceCurrency, targetCurrency;
            
            if ("BUY".equals(type)) {
                // 买入外币，用人民币购买外币
                sourceCurrency = toCurrency; // CNY
                targetCurrency = fromCurrency; // USD
                exchangedAmount = amount.divide(rate, 2, RoundingMode.HALF_UP);
            } else {
                // 卖出外币，用外币换取人民币
                sourceCurrency = fromCurrency; // USD
                targetCurrency = toCurrency; // CNY
                exchangedAmount = amount.multiply(rate).setScale(2, RoundingMode.HALF_UP);
            }
              // 检查余额
            AccountBalance sourceBalance = accountBalanceMapper.selectByAccountIdAndCurrency(
                accountId, sourceCurrency);
            if (sourceBalance == null || sourceBalance.getBalance().compareTo(amount) < 0) {
                return Result.error("余额不足，需要 " + amount + " " + sourceCurrency);
            }
              // 执行交易
            // 1. 扣除源币种余额
            int decreaseResult = accountBalanceMapper.decreaseBalance(
                accountId, amount, sourceCurrency);
            if (decreaseResult == 0) {
                return Result.error("余额扣除失败");
            }
              // 2. 增加目标币种余额
            AccountBalance targetBalance = accountBalanceMapper.selectByAccountIdAndCurrency(
                accountId, targetCurrency);
            if (targetBalance == null) {
                // 创建新的余额记录
                targetBalance = new AccountBalance();
                targetBalance.setAccountId(accountId);
                targetBalance.setBalance(exchangedAmount);
                targetBalance.setCurrency(targetCurrency);
                accountBalanceMapper.insert(targetBalance);
            } else {                accountBalanceMapper.increaseBalance(
                    accountId, exchangedAmount, targetCurrency);
            }
              // 3. 记录交易
            ForeignExchangeTrade trade = new ForeignExchangeTrade();
            trade.setForeignExchangeTradeId(UUID.randomUUID().toString());
            trade.setAccountId(accountId);
            trade.setForeignExchangeId(exchange.getForeignExchangeId());
            trade.setCurrencyPair(currencyPair);
            trade.setAmount(amount);
            trade.setTradeType(type);
            trade.setTradeTime(LocalDateTime.now());
            trade.setStatus("SUCCESS");
            trade.setExchangedAmount(exchangedAmount);
            trade.setExchangeRate(exchange.getCurrentRate());
            
            forexMapper.insertTrade(trade);
            
            Map<String, Object> data = new HashMap<>();
            data.put("tradeId", trade.getForeignExchangeTradeId());
            data.put("currencyPair", currencyPair);
            data.put("tradeType", type);
            data.put("amount", amount);
            data.put("exchangedAmount", exchangedAmount);
            data.put("rate", exchange.getCurrentRate());            data.put("sourceCurrency", sourceCurrency);
            data.put("targetCurrency", targetCurrency);
            data.put("status", "SUCCESS");
              return Result.success(data, 1);
            
        } catch (Exception e) {
            return Result.error("交易失败: " + e.getMessage());
        }
    }
      /**
     * 查询用户的外汇交易记录
     */
    public Result<List<ForeignExchangeTrade>> getTradeHistory(Long accountId) {        try {
            List<ForeignExchangeTrade> trades = forexMapper.findTradesByAccountId(accountId);
            return Result.success(trades, trades.size());
        } catch (Exception e) {
            return Result.error("查询交易记录失败: " + e.getMessage());
        }
    }
    
    /**
     * 根据交易ID查询交易详情
     */
    public Result<ForeignExchangeTrade> getTradeById(String tradeId) {
        try {            ForeignExchangeTrade trade = forexMapper.findTradeById(tradeId);
            if (trade != null) {
                return Result.success(trade, 1);
            } else {
                return Result.error("交易记录不存在");
            }
        } catch (Exception e) {
            return Result.error("查询交易详情失败: " + e.getMessage());
        }
    }
}
