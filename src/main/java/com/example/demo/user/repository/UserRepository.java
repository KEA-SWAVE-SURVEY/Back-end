package com.example.demo.user.repository;

import com.example.demo.user.domain.User;
import jakarta.jws.soap.SOAPBinding;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User,Long> {
    public User findByKakaoEmail(String kakaoEmail);

    public User findByUserCode(Long userCode);
}
