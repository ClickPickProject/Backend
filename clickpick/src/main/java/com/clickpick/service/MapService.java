package com.clickpick.service;

import com.clickpick.domain.Post;
import com.clickpick.dto.map.MakerReq;
import com.clickpick.dto.map.MakerRes;
import com.clickpick.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MapService {

    private final PostRepository postRepository;

    //범위에 해당하는 게시글 반환
    public ResponseEntity findMakers(MakerReq makerReq) {
        Optional<List<Post>> makerResult = postRepository.findBound(makerReq.getSouth(), makerReq.getWest(), makerReq.getNorth(), makerReq.getEast());
        List<MakerRes> makerResList = new ArrayList<>();
        if(makerResult.isPresent()){
            List<Post> posts = makerResult.get();
            for(Post post : posts){
                MakerRes makerRes = new MakerRes(post);
                makerResList.add(makerRes);
            }
        }
        return ResponseEntity.status(HttpStatus.OK).body(makerResList);
    }
}
