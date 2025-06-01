package com.payhub.mapper;

import com.payhub.pojo.ForeignExchange;
import com.payhub.pojo.ForeignExchangeTrade;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface ForexMapper {
    
    @Select("SELECT * FROM ForeignExchange WHERE currency_pair = #{currencyPair}")
    ForeignExchange findByCurrencyPair(String currencyPair);
    
    @Select("SELECT * FROM ForeignExchange")
    List<ForeignExchange> findAllExchangeRates();
    
    @Insert("INSERT INTO ForeignExchangeTrade (foreign_exchange_trade_id, account_id, foreign_exchange_id, currency_pair, amount, trade_type, status, trade_time, exchanged_amount, exchange_rate) " +
            "VALUES (#{foreignExchangeTradeId}, #{accountId}, #{foreignExchangeId}, #{currencyPair}, #{amount}, #{tradeType}, #{status}, #{tradeTime}, #{exchangedAmount}, #{exchangeRate})")
    void insertTrade(ForeignExchangeTrade trade);
      @Select("SELECT * FROM ForeignExchangeTrade WHERE account_id = #{accountId}")
    List<ForeignExchangeTrade> findTradesByAccountId(Long accountId);
    
    @Select("SELECT * FROM ForeignExchangeTrade WHERE foreign_exchange_trade_id = #{tradeId}")
    ForeignExchangeTrade findTradeById(String tradeId);
    
    @Update("UPDATE ForeignExchange SET current_rate = #{currentRate} WHERE currency_pair = #{currencyPair}")
    void updateExchangeRate(@Param("currencyPair") String currencyPair, @Param("currentRate") Float currentRate);
}
