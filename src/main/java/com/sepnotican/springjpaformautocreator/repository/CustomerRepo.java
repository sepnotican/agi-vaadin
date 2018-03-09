package com.sepnotican.springjpaformautocreator.repository;

import com.sepnotican.springjpaformautocreator.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CustomerRepo extends JpaRepository<Customer, Long> {
    Customer save(Customer customer);

    Optional<Customer> findById(Long id);

    Customer findFirstByName(String name);
}
