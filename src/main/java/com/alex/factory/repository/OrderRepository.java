package com.alex.factory.repository;

import com.alex.factory.model.Order;
import com.alex.factory.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    Optional<Order> deleteAllByUser(User user);
}
