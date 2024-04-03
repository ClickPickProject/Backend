package com.clickpick.service;

import com.clickpick.domain.PositionLike;
import com.clickpick.domain.Post;
import com.clickpick.domain.User;
import com.clickpick.dto.map.LikedPositionReq;
import com.clickpick.dto.map.MarkerReq;
import com.clickpick.dto.map.MarkerRes;
import com.clickpick.dto.post.ViewPostListRes;
import com.clickpick.repository.PositionLikeRepository;
import com.clickpick.repository.PostRepository;
import com.clickpick.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
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

    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final PositionLikeRepository positionLikeRepository;

    /* 범위에 해당하는 게시글 반환 */
    public ResponseEntity findMarkers(MarkerReq makerReq) {
        Optional<List<Post>> makerResult = postRepository.findBound(makerReq.getSouth(), makerReq.getWest(), makerReq.getNorth(), makerReq.getEast());
        List<MarkerRes> makerResList = new ArrayList<>();
        if(makerResult.isPresent()){
            List<Post> posts = makerResult.get();
            for(Post post : posts){
                MarkerRes makerRes = new MarkerRes(post);
                makerResList.add(makerRes);
            }
        }
        return ResponseEntity.status(HttpStatus.OK).body(makerResList);
    }

    /* 좌표가 동일한 게시글 반환 */
    public ResponseEntity findPosts(int page, double xPosition, double yPosition){
        PageRequest pageRequest = PageRequest.of(page, 10, Sort.by(Sort.Direction.DESC,"createAt"));
        Page<Post> pagingResult = postRepository.findPosition(xPosition, yPosition, pageRequest);
        Page<ViewPostListRes> map = pagingResult.map(post -> new ViewPostListRes(post));

        return ResponseEntity.status(HttpStatus.OK).body(map);

    }

    /* 장소 좋아요 */
    @Transactional
    public ResponseEntity bookmarkPosition(LikedPositionReq likedPositionReq, String userId) {
        Optional<User> userResult = userRepository.findById(userId);
        if(userResult.isPresent()){
            Optional<PositionLike> positionResult = positionLikeRepository.findPosition(likedPositionReq.getXPosition(), likedPositionReq.getYPosition());
            PositionLike positionLike = new PositionLike(userResult.get(), likedPositionReq.getXPosition(), likedPositionReq.getYPosition(), likedPositionReq.getStatus());
            if(positionResult.isPresent()){
                positionLikeRepository.delete(positionResult.get());
                return ResponseEntity.status(HttpStatus.OK).body("즐겨찾기에서 삭제하였습니다.");
            }
            else{
                positionLikeRepository.save(positionLike);
                return ResponseEntity.status(HttpStatus.OK).body("즐겨찾기에 등록하였습니다.");
            }

        }
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body("회원만 사용 가능한 기능입니다.");
    }

    /* 장소 좋아요 리스트 */ //범위는?
    public ResponseEntity viewBookmarkPosition(String userId){
        Optional<User> userResult = userRepository.findById(userId);
        if(userResult.isPresent()){
            Optional<List<PositionLike>> positionLikeResult = positionLikeRepository.findUser(userId);
            if(positionLikeResult.isPresent()){

            }

        }

        return ResponseEntity.status(HttpStatus.FORBIDDEN).body("회원만 사용 가능한 기능입니다.");
    }

}
