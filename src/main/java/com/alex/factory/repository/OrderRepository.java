package com.alex.factory.repository;

import com.alex.factory.model.Order;
import com.alex.factory.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.Month;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    Optional<Order> deleteAllByUser(User user);
    Optional<List<Order>>  findAllByCostAfter(Long cost);
    Optional<List<Order>>  findAllByStatus(String status);
    Optional<List<Order>>  findAllByStatusAndCostAfter(String status,Long Cost);

}
