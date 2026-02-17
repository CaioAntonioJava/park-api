package com.caiohenrique.demo_park_api.repository;

import com.caiohenrique.demo_park_api.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
}
