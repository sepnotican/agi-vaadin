package com.sepnotican.agi.example.repository;

import com.sepnotican.agi.example.entity.Customer;
import com.sepnotican.agi.example.entity.TradeDeal;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TradeDealsRepo extends JpaRepository<TradeDeal, Long> {
    Optional<TradeDeal> findFirst1ByCustomerOrderByIdDesc(Customer customer);
}
