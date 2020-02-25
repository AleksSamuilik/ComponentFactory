package com.alex.factory.repository;

import com.alex.factory.model.UserDescription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserDescriptionRepository extends JpaRepository<UserDescription, Long> {
}
