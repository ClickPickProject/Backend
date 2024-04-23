package com.clickpick.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;


@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Admin {
    @Id
    @Column(name = "admin_id")
    private String id;
    @Column(name = "admin_pw",nullable = false)
    private String password;
    @Column(nullable = false)
    private String name;
    @Column(nullable = false, unique = true)
    private String phone;
    @CreationTimestamp
    @Column(nullable = false)
    private LocalDateTime createAt;

    public Admin(String id, String password, String name, String phone) {
        this.id = id;
        this.password = password;
        this.name = name;
        this.phone = phone;
    }
}
