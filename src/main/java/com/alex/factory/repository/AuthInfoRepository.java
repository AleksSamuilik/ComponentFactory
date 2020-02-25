package com.alex.factory.repository;

import com.alex.factory.model.AuthInfoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AuthInfoRepository extends JpaRepository<AuthInfoEntity, Long> {

    Optional<AuthInfoEntity> findByLogin(String username);

    Optional<AuthInfoEntity> deleteAllByLogin(String username);


}
