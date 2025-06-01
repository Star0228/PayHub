package com.payhub.service;

import com.payhub.mapper.ForexMapper;
import com.payhub.mapper.AccountBalanceMapper;
import com.payhub.pojo.ForeignExchange;
import com.payhub.pojo.ForeignExchangeTrade;
import com.payhub.pojo.AccountBalance;
import com.payhub.pojo.Result;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ForexServiceTest {

    @Mock
    private ForexMapper forexMapper;

    @Mock
    private AccountBalanceMapper accountBalanceMapper;

    @InjectMocks
    private ForexService forexService;

    private ForeignExchange testExchange;
    private ForeignExchangeTrade testTrade;
    private AccountBalance testBalance;

    @BeforeEach
    void setUp() {
        testExchange = new ForeignExchange();
        testExchange.setForeignExchangeId("fx123");
        testExchange.setCurrencyPair("USD/CNY");
        testExchange.setCurrentRate(7.25f);

        testTrade = new ForeignExchangeTrade();
        testTrade.setForeignExchangeTradeId("trade123");
        testTrade.setAccountId(123L);
        testTrade.setCurrencyPair("USD/CNY");
        testTrade.setAmount(new BigDecimal("1000"));
        testTrade.setExchangeRate(7.25f);
        testTrade.setExchangedAmount(new BigDecimal("7250"));
        testTrade.setTradeType("BUY");
        testTrade.setStatus("SUCCESS");
        testTrade.setTradeTime(LocalDateTime.now());

        testBalance = new AccountBalance();
        testBalance.setAccountId(123L);
        testBalance.setCurrency("CNY");
        testBalance.setBalance(new BigDecimal("10000"));
    }

    @Test
    void testGetExchangeRate_Success() {
        // Given
        when(forexMapper.findByCurrencyPair("USD/CNY")).thenReturn(testExchange);

        // When
        Result<Map<String, Object>> result = forexService.getExchangeRate("USD/CNY");

        // Then
        assertEquals(0, result.getCode());
        assertEquals("成功", result.getMsg());
        assertNotNull(result.getData());
        assertEquals("USD/CNY", result.getData().get("currencyPair"));
        assertEquals(7.25f, result.getData().get("rate"));
        verify(forexMapper).findByCurrencyPair("USD/CNY");
    }

    @Test
    void testGetExchangeRate_NotFound() {
        // Given
        when(forexMapper.findByCurrencyPair("INVALID/PAIR")).thenReturn(null);

        // When
        Result<Map<String, Object>> result = forexService.getExchangeRate("INVALID/PAIR");

        // Then
        assertEquals(1, result.getCode());
        assertTrue(result.getMsg().contains("不支持的货币对"));
        verify(forexMapper).findByCurrencyPair("INVALID/PAIR");
    }

    @Test
    void testGetAllExchangeRates_Success() {
        // Given
        List<ForeignExchange> exchanges = Arrays.asList(testExchange);
        when(forexMapper.findAllExchangeRates()).thenReturn(exchanges);

        // When
        Result<List<ForeignExchange>> result = forexService.getAllExchangeRates();

        // Then
        assertEquals(0, result.getCode());
        assertEquals("成功", result.getMsg());
        assertEquals(1, result.getCount());
        assertEquals(1, result.getData().size());
        assertEquals("USD/CNY", result.getData().get(0).getCurrencyPair());
        verify(forexMapper).findAllExchangeRates();
    }

    @Test
    void testExecuteTrade_BuySuccess() {
        // Given
        when(forexMapper.findByCurrencyPair("USD/CNY")).thenReturn(testExchange);
        when(accountBalanceMapper.selectByAccountIdAndCurrency(123L, "CNY")).thenReturn(testBalance);
        when(accountBalanceMapper.decreaseBalance(eq(123L), any(BigDecimal.class), eq("CNY"))).thenReturn(1);
        when(accountBalanceMapper.selectByAccountIdAndCurrency(123L, "USD")).thenReturn(null);
        doNothing().when(forexMapper).insertTrade(any(ForeignExchangeTrade.class));

        // When
        Result<Map<String, Object>> result = forexService.executeTrade(123L, "USD/CNY", 
                new BigDecimal("1000"), "BUY");

        // Then
        assertEquals(0, result.getCode());
        assertEquals("成功", result.getMsg());
        assertNotNull(result.getData());
        assertEquals("SUCCESS", result.getData().get("status"));
        assertEquals(7.25f, result.getData().get("rate"));
        verify(forexMapper).insertTrade(any(ForeignExchangeTrade.class));
    }

    @Test
    void testExecuteTrade_SellSuccess() {
        // Given
        AccountBalance usdBalance = new AccountBalance();
        usdBalance.setAccountId(123L);
        usdBalance.setCurrency("USD");
        usdBalance.setBalance(new BigDecimal("1000"));

        when(forexMapper.findByCurrencyPair("USD/CNY")).thenReturn(testExchange);
        when(accountBalanceMapper.selectByAccountIdAndCurrency(123L, "USD")).thenReturn(usdBalance);
        when(accountBalanceMapper.decreaseBalance(eq(123L), any(BigDecimal.class), eq("USD"))).thenReturn(1);
        when(accountBalanceMapper.selectByAccountIdAndCurrency(123L, "CNY")).thenReturn(testBalance);
        when(accountBalanceMapper.increaseBalance(eq(123L), any(BigDecimal.class), eq("CNY"))).thenReturn(1);
        doNothing().when(forexMapper).insertTrade(any(ForeignExchangeTrade.class));

        // When
        Result<Map<String, Object>> result = forexService.executeTrade(123L, "USD/CNY", 
                new BigDecimal("100"), "SELL");

        // Then
        assertEquals(0, result.getCode());
        assertEquals("成功", result.getMsg());
        assertNotNull(result.getData());
        assertEquals("SUCCESS", result.getData().get("status"));
        verify(forexMapper).insertTrade(any(ForeignExchangeTrade.class));
    }

    @Test
    void testExecuteTrade_InvalidCurrencyPair() {
        // Given
        when(forexMapper.findByCurrencyPair("INVALID/PAIR")).thenReturn(null);

        // When
        Result<Map<String, Object>> result = forexService.executeTrade(123L, "INVALID/PAIR", 
                new BigDecimal("1000"), "BUY");

        // Then
        assertEquals(1, result.getCode());
        assertTrue(result.getMsg().contains("不支持的货币对"));
        verify(forexMapper, never()).insertTrade(any());
    }

    @Test
    void testExecuteTrade_InvalidType() {
        // When
        Result<Map<String, Object>> result = forexService.executeTrade(123L, "USD/CNY", 
                new BigDecimal("1000"), "INVALID");

        // Then
        assertEquals(1, result.getCode());
        assertTrue(result.getMsg().contains("交易类型只能是BUY或SELL"));
        verify(forexMapper, never()).insertTrade(any());
    }

    @Test
    void testExecuteTrade_InsufficientBalance() {
        // Given
        AccountBalance lowBalance = new AccountBalance();
        lowBalance.setAccountId(123L);
        lowBalance.setCurrency("CNY");
        lowBalance.setBalance(new BigDecimal("500"));

        when(forexMapper.findByCurrencyPair("USD/CNY")).thenReturn(testExchange);
        when(accountBalanceMapper.selectByAccountIdAndCurrency(123L, "CNY")).thenReturn(lowBalance);

        // When
        Result<Map<String, Object>> result = forexService.executeTrade(123L, "USD/CNY", 
                new BigDecimal("1000"), "BUY");

        // Then
        assertEquals(1, result.getCode());
        assertTrue(result.getMsg().contains("余额不足"));
        verify(forexMapper, never()).insertTrade(any());
    }

    @Test
    void testExecuteTrade_ZeroAmount() {
        // When
        Result<Map<String, Object>> result = forexService.executeTrade(123L, "USD/CNY", 
                BigDecimal.ZERO, "BUY");

        // Then
        assertEquals(1, result.getCode());
        assertTrue(result.getMsg().contains("交易金额必须大于0"));
        verify(forexMapper, never()).insertTrade(any());
    }

    @Test
    void testExecuteTrade_EmptyAccountId() {
        // When
        Result<Map<String, Object>> result = forexService.executeTrade(null, "USD/CNY", 
                new BigDecimal("1000"), "BUY");

        // Then
        assertEquals(1, result.getCode());
        assertTrue(result.getMsg().contains("账户ID不能为空"));
        verify(forexMapper, never()).insertTrade(any());
    }

    @Test
    void testGetTradeHistory_Success() {
        // Given
        List<ForeignExchangeTrade> trades = Arrays.asList(testTrade);
        when(forexMapper.findTradesByAccountId(123L)).thenReturn(trades);

        // When
        Result<List<ForeignExchangeTrade>> result = forexService.getTradeHistory(123L);

        // Then
        assertEquals(0, result.getCode());
        assertEquals("成功", result.getMsg());
        assertEquals(1, result.getCount());
        assertEquals(1, result.getData().size());
        assertEquals("trade123", result.getData().get(0).getForeignExchangeTradeId());
        verify(forexMapper).findTradesByAccountId(123L);
    }

    @Test
    void testGetTradeById_Success() {
        // Given
        when(forexMapper.findTradeById("trade123")).thenReturn(testTrade);

        // When
        Result<ForeignExchangeTrade> result = forexService.getTradeById("trade123");

        // Then
        assertEquals(0, result.getCode());
        assertEquals("成功", result.getMsg());
        assertEquals(1, result.getCount());
        assertNotNull(result.getData());
        assertEquals("trade123", result.getData().getForeignExchangeTradeId());
        assertEquals(123L, result.getData().getAccountId());
        verify(forexMapper).findTradeById("trade123");
    }

    @Test
    void testGetTradeById_NotFound() {
        // Given
        when(forexMapper.findTradeById("nonexistent")).thenReturn(null);

        // When
        Result<ForeignExchangeTrade> result = forexService.getTradeById("nonexistent");

        // Then
        assertEquals(1, result.getCode());
        assertTrue(result.getMsg().contains("交易记录不存在"));
        verify(forexMapper).findTradeById("nonexistent");
    }

    @Test
    void testGetExchangeRate_ExceptionHandling() {
        // Given
        when(forexMapper.findByCurrencyPair("USD/CNY"))
                .thenThrow(new RuntimeException("Database error"));

        // When
        Result<Map<String, Object>> result = forexService.getExchangeRate("USD/CNY");

        // Then
        assertEquals(1, result.getCode());
        assertTrue(result.getMsg().contains("查询汇率失败"));
        verify(forexMapper).findByCurrencyPair("USD/CNY");
    }

    @Test
    void testExecuteTrade_ExceptionHandling() {
        // Given
        when(forexMapper.findByCurrencyPair("USD/CNY")).thenReturn(testExchange);
        when(accountBalanceMapper.selectByAccountIdAndCurrency(anyLong(), anyString()))
                .thenThrow(new RuntimeException("Database error"));

        // When
        Result<Map<String, Object>> result = forexService.executeTrade(123L, "USD/CNY", 
                new BigDecimal("1000"), "BUY");

        // Then
        assertEquals(1, result.getCode());
        assertTrue(result.getMsg().contains("交易失败"));
    }
}
