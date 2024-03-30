package com.clickpick.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.DynamicInsert;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@DynamicInsert
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Post {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id",nullable = false)
    private User user;

    @CreationTimestamp
    @Column(nullable = false)
    private LocalDateTime createAt;
    private String position;
    @Column(nullable = false, length = 50000)
    private String content;
    @Column(nullable = false)
    private String title;
    @Column(name = "view_count",nullable = false)
    @ColumnDefault("0")
    private Long viewCount;
    @Column(name = "like_count",nullable = false) //베스트 게시물 조회 시 필요
    @ColumnDefault("0")
    private Long likeCount;
    @Column(name = "photo_date")
    private LocalDateTime photoDate;
    @Column(name = "post_category")
    @Enumerated(EnumType.STRING)
    @ColumnDefault("'자유'")
    private PostCategory postCategory;
    @OneToMany(mappedBy = "post", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<Hashtag> hashtags = new ArrayList<>(); //
    @OneToMany(mappedBy = "post", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<PostLike> postLikes = new ArrayList<>();
    @OneToMany(mappedBy = "post", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<Comment> comments = new ArrayList<>();
    @OneToMany(mappedBy = "post", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<PostImage> images = new ArrayList<>();


    // 이미지 넣어야함


    public Post(User user, String position, String content, String title, String category) {
        this.user = user;
        this.position = position;
        this.content = content;
        this.title = title;
        this.postCategory = PostCategory.valueOf(category);
    }

    public void changePost(String title, String content, String position, String category){
        this.title = title;
        this.content = content;
        this.position = position;
        this.postCategory = PostCategory.valueOf(category);
    }

    public void upViewCount(){
        this.viewCount += 1;
    }
    public void upLikeCount() {this.likeCount += 1;}
    public void downLikeCount() {this.likeCount -= 1;}



}
