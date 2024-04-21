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
@DynamicInsert
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Comment {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comment_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id",nullable = false)
    private Post post;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id",nullable = false)
    private User user;
    @Column(nullable = false)
    private String content;
    @CreationTimestamp
    @Column(nullable = false)
    private LocalDateTime createAt;
    @OneToMany(mappedBy = "comment",cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<CommentLike> commentLikes = new ArrayList<>(); //좋아요 수

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Comment parent;

    @OneToMany(mappedBy = "parent", orphanRemoval = true)
    private List<Comment> comments = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    @ColumnDefault("'LIVE'")
    private CommentStatus status;

    public Comment(Post post, User user, String content, Comment parent) { // 게시글 작성
        this.post = post;
        this.user = user;
        this.content = content;
        this.parent = parent;
    }

    public void tempDelete(){
        this.status = CommentStatus.valueOf("DELETE");
        this.content = "삭제된 댓글입니다.";

    }

    public void leaveUserComment(User user){
        this.status = CommentStatus.valueOf("DELETE");
        this.content = "삭제된 댓글입니다.";
        this.user = user;
    }
    public void changeComment(String content){
        this.content = content;
    }

}
