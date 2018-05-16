package com.sepnotican.agi.example.repository;

import com.sepnotican.agi.example.entity.TradeDeal;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TradeDealsRepo extends JpaRepository<TradeDeal, Long> {
}
