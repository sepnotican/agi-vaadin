package com.sepnotican.springjpaformautocreator.repository;

import com.sepnotican.springjpaformautocreator.entity.TradeDeal;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TradeDealsRepo extends JpaRepository<TradeDeal, Long> {
}
