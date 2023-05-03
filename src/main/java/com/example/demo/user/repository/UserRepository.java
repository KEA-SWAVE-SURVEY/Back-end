package com.example.demo.user.repository;

import com.example.demo.user.domain.User;
import jakarta.jws.soap.SOAPBinding;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User,Long> {
    User findByEmail(String email);

    Optional<User> findByUserCode(Long userCode);

    User findByEmailAndProvider(String email, String provider);


}
