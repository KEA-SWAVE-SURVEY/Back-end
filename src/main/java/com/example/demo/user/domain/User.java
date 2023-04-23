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
    private Long kakaoId;
    private String kakaoProfileImg;

    private String kakaoNickname;

    private String kakaoEmail;

    private String userRole;

    @CreationTimestamp //(4)
    private Timestamp createTime;

    @Builder
    public User(Long kakaoId, String kakaoProfileImg, String kakaoNickname,
                String kakaoEmail, String userRole) {

        this.kakaoId = kakaoId;
        this.kakaoProfileImg = kakaoProfileImg;
        this.kakaoNickname = kakaoNickname;
        this.kakaoEmail = kakaoEmail;
        this.userRole = userRole;
    }

}
