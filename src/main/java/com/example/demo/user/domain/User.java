package com.example.demo.user.domain;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.sql.Timestamp;

@Entity
@Data
@NoArgsConstructor
@Table(name = "user_master")
public class User {


    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userCode;
    private Long id;
    private String profileImg;

    private String nickname;

    private String email;

    private String userRole;

    @CreationTimestamp //(4)
    private Timestamp createTime;

    @Builder
    public User(Long id, String profileImg, String nickname,
                String email, String userRole) {

        this.id = id;
        this.profileImg = profileImg;
        this.nickname = nickname;
        this.email = email;
        this.userRole = userRole;
    }

}
