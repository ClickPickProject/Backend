package com.clickpick.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Hashtag {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "hashtag_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id",nullable = false)
    private Post post;

    @Column(nullable = false)
    private String content;

    public Hashtag(Post post, String content) {
        this.post = post;
        this.content = content;
    }

    public void changeHashtag(String content){
        this.content = content;
    }

    public void addPost(Post post){
        this.post = post;
    }


}
