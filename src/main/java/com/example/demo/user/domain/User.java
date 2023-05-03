package com.example.demo.user.domain;

import com.example.demo.survey.domain.Survey;
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
    @Column(name = "user_Id")
    private Long userCode;
    private Long id;
    private String profileImg;

    private String nickname;

    private String email;

    private String provider;

    private String userRole;

    @OneToOne(mappedBy = "user")
    private Survey survey;

    @CreationTimestamp //(4)
    private Timestamp createTime;

    @Builder
    public User(Long id, String profileImg, String nickname,
                String email,String provider, String userRole) {

        this.id = id;
        this.profileImg = profileImg;
        this.nickname = nickname;
        this.email = email;
        this.provider=provider;
        this.userRole = userRole;
    }

}
