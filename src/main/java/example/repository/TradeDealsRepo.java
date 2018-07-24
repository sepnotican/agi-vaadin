package example.repository;

import example.entity.Customer;
import example.entity.TradeDeal;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TradeDealsRepo extends JpaRepository<TradeDeal, Long> {
    Optional<TradeDeal> findFirst1ByCustomerOrderByIdDesc(Customer customer);
}
