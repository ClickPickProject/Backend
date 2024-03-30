package com.clickpick.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.DynamicInsert;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@Entity
@DynamicInsert
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString(of = {"id","password","name","nickname","phone","createAt","status"})
public class User {

    @Id
    private String id; // 이메일 형식
    @Column(nullable = false)
    private String password;
    @Column(nullable = false)
    private String name;
    @Column(nullable = false, unique = true)
    private String nickname;
    @Column(nullable = false, unique = true)
    private String phone;
    @CreationTimestamp
    @Column(nullable = false)
    private LocalDateTime createAt;
    @Enumerated(EnumType.STRING)
    @ColumnDefault("'NORMAL'")
    private UserStatus status;
    @OneToOne(mappedBy = "user", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private ProfileImage profileImage;
    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE, orphanRemoval = true) // 유저 삭제시 post 연쇄 삭제
    private List<Post> posts = new ArrayList<>();
    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<PostImage> postImages = new ArrayList<>();



    public User(String id, String password, String name, String nickname, String phone) {
        this.id = id;
        this.password = password;
        this.name = name;
        this.nickname = nickname;
        this.phone = phone;
    }

    //세션 생성용
    public User(String id, UserStatus status) {
        this.id = id;
        this.status = status;
    }

    //비밀번호 변경 시 사용 함수
    public void updatePassword(String password){
        this.password = password;

    }

    // 닉네임 변경 시 사용 함수
    public void updateNickname(String nickname){
        this.nickname = nickname;
    }

    // 전화번호 변경 시 사용 함수
    public void updatePhone(String phone){
        this.phone = phone;
    }


}
