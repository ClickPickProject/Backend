package com.clickpick.dto.image;

import lombok.Data;

@Data
public class UrlRes {
    private String url;
    private Long capacity;

    public UrlRes(String url, Long capacity) {

        this.url = url;
        this.capacity = capacity;
    }

    public UrlRes(String url) {
        this.url = url;
        this.capacity = 4352L;
    }
}
