package io.egargo.jwt.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import io.egargo.jwt.models.User;

public interface UserRepository extends JpaRepository<User, Long> {
    public Optional<User> findUserByEmail(String email);

    public Optional<User> findUserByUsername(String username);
}
