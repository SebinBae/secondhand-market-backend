package com.sebin.secondhand_market.domain.user.repository;

import com.sebin.secondhand_market.domain.user.entity.UserEntity;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<UserEntity, UUID> {

  boolean existsByEmail(String email);

  Optional<UserEntity> findByEmail(String email);
}
