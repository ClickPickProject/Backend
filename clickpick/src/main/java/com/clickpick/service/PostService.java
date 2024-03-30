package com.clickpick.service;

import com.clickpick.domain.*;
import com.clickpick.dto.comment.ViewCommentRes;
import com.clickpick.dto.comment.ViewRecommentRes;
import com.clickpick.dto.post.*;
import com.clickpick.repository.*;
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
@RequiredArgsConstructor
@Transactional(readOnly = true) //읽기 전용 메서드에서 사용 (데이터의 변경 x)
public class PostService {
    private final UserRepository userRepository;
    private final AdminRepository adminRepository;
    private final PostRepository postRepository;
    private final HashtagRepository hashtagRepository;
    private final PostLikeRepository postLikeRepository;
    private final CommentRepository commentRepository;
    private final CommentLikeRepository commentLikeRepository;
    private final ReportPostRepository reportPostRepository;
    private final PostImageRepository postImageRepository;

    /* 게시글 작성 */
    @Transactional
    public ResponseEntity createPost(String userId, CreatePostReq createPostReq) {
        Optional<User> result = userRepository.findById(userId);
        if(isEnumValue(createPostReq.getPostCategory())){
            return ResponseEntity.status(HttpStatus.CONFLICT).body("존재하지 않는 카테고리 입니다.");
        }
        if(result.isPresent()){
            User user = result.get();
            Post post = new Post(user,createPostReq.getPosition(),createPostReq.getContent(),createPostReq.getTitle(),createPostReq.getPostCategory());
            postRepository.save(post);
            if(createPostReq.getHashtags() != null){
                for (String hashtag : createPostReq.getHashtags()) {
                    Hashtag addHashtag = new Hashtag(post,hashtag);
                    hashtagRepository.save(addHashtag);
                }
            }
            if(createPostReq.getThumbnailImage() != null){
                Optional<PostImage> thumbnail = postImageRepository.findByFileName(createPostReq.getThumbnailImage());
                if(thumbnail.isPresent()){
                    thumbnail.get().changeThumbnail();
                    thumbnail.get().addPost(post);
                }
            }
            if(createPostReq.getImageNames() != null){
                for (String name : createPostReq.getImageNames()){
                    Optional<PostImage> postImageResult = postImageRepository.findByFileName(name);
                    if(postImageResult.isPresent()){
                        postImageResult.get().addPost(post);
                    }
                }
            }


            return ResponseEntity.status(HttpStatus.OK).body("게시글이 등록되었습니다.");
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("존재하지 않는 이메일(아이디)입니다.");



    }

    /* 게시글 삭제 */
    @Transactional
    public ResponseEntity deletePost(Long postId, String userId) {
        //유저와 게시글 id를 비교하여 동일한 값을 가져옴 => 없으면 본인이 작성하지 않음
        Optional<Post> result = postRepository.findUserPost(postId, userId);

        if(result.isPresent()){
            postRepository.delete(result.get());
            return ResponseEntity.status(HttpStatus.OK).body("삭제가 완료되었습니다.");
        }
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body("사용자가 삭제할 수 없는 게시글입니다.");

    }

    /* 게시글 수정 */
    @Transactional
    public ResponseEntity renewPost(Long postId, String userId, UpdatePostReq updatePostReq){
        Optional<Post> result = postRepository.findUserPost(postId, userId);
        if(isEnumValue(updatePostReq.getPostCategory())){
            return ResponseEntity.status(HttpStatus.CONFLICT).body("존재하지 않는 카테고리입니다.");
        }
        if(result.isPresent()){
            Post post = result.get();
            post.changePost(updatePostReq.getTitle(), updatePostReq.getContent(), updatePostReq.getPosition(), updatePostReq.getPostCategory());

            /* 게시글 중 해시 태그 수정 */
            Optional<List<Hashtag>> hashResult = hashtagRepository.findPostHashtag(postId);
            if(hashResult.isEmpty()){ // 게시글에 해시태그가 존재하지 않은 경우
                if(updatePostReq.getHashtags().size() > 0) { // 추가 할 해시태그 존재
                    for (String hashtag : updatePostReq.getHashtags()) {
                        Hashtag addHashtag = new Hashtag(post,hashtag);
                        hashtagRepository.save(addHashtag);
                    }

                }
            }
            else{ // 해시태그 존재하는 경우
                List<Hashtag> hashtags = hashResult.get();
                for (Hashtag hashtag : hashtags) {
                    hashtagRepository.delete(hashtag);
                }
                if(updatePostReq.getHashtags() != null) { // 추가 할 해시태그 존재
                    for (String hashtag : updatePostReq.getHashtags()) {
                        Hashtag addHashtag = new Hashtag(post,hashtag);
                        hashtagRepository.save(addHashtag);
                    }
                }

            }
            if(updatePostReq.getThumbnailImage() != null){
                Optional<PostImage> thumbnail = postImageRepository.findByFileName(updatePostReq.getThumbnailImage());
                if(thumbnail.isPresent()){
                    Optional<PostImage> preThumbnail = postImageRepository.findThumbnail(post.getId(), PostImageStatus.THUMBNAIL);
                    if(preThumbnail.isPresent()){
                        preThumbnail.get().changeNotThumbnail();
                    }
                    thumbnail.get().changeThumbnail();
                    thumbnail.get().addPost(post);

                }
            }

            if(updatePostReq.getUpdateImageNames() != null){
                for (String name : updatePostReq.getUpdateImageNames()){
                    Optional<PostImage> postImageResult = postImageRepository.findByFileName(name);
                    if(postImageResult.isPresent()){
                        postImageResult.get().addPost(post);
                    }
                }
            }
            return ResponseEntity.status(HttpStatus.OK).body("수정이 완료되었습니다.");
        }
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body("사용자가 수정할 수 없는 게시글입니다.");

    }

    /* 게시글 상세 조회 */
    @Transactional
    public ResponseEntity selectPost(String userId, Long postId) {
        Optional<Post> postResult = postRepository.findById(postId);
        if(postResult.isPresent()){
            Post post = postResult.get();
            boolean likePostCheck = false;
            post.upViewCount(); //조회수 증가 ->@Transaction 필요
            if(userId != null || userId.equals("anonymousUser")){ // 게시글 좋아요 확인 로직
                Optional<PostLike> postLikeResult = postLikeRepository.checkLikePost(postId, userId);
                if(postLikeResult.isPresent()){
                    likePostCheck = true;
                }
            }

            ViewPostRes viewPostRes = new ViewPostRes(post.getUser().getNickname(), post, likePostCheck);

            if(post.getHashtags().size()>0){
                for(Hashtag hashtag : post.getHashtags()){
                    viewPostRes.addHashtag(hashtag.getContent());
                }
            }

            /* 댓글 조회 */
            Optional<List<Comment>> commentResult = commentRepository.findByPostId(postId);
            List<ViewCommentRes> viewCommentResList = new ArrayList<>();
            if(commentResult.isPresent()){
                for(Comment comment : commentResult.get()){
                    boolean likeCommentCheck = false;
                    if(userId != null || userId.equals("anonymousUser")){ // 댓글 좋아요 확인 로직
                        Optional<CommentLike> commentLikeResult = commentLikeRepository.checkLikeComment(comment.getId(), userId);
                        if(commentLikeResult.isPresent()){
                            likeCommentCheck = true;
                        }
                    }
                    if(comment.getParent() == null){
                        ViewCommentRes viewCommentRes = new ViewCommentRes(comment,likeCommentCheck);
                        if(comment.getComments().size() > 0){ //해당 댓글의 대댓글이 있으면
                            for(Comment recomment : comment.getComments()){
                                boolean likeRecommentCheck = false;
                                Optional<CommentLike> recommentLikeResult = commentLikeRepository.checkLikeComment(recomment.getId(), userId);
                                System.out.println("recommentLikeResult = " + recommentLikeResult);
                                if(recommentLikeResult.isPresent()){
                                    likeRecommentCheck = true;
                                }
                                ViewRecommentRes viewRecommentRes = new ViewRecommentRes(recomment, likeRecommentCheck);
                                viewCommentRes.addRecomment(viewRecommentRes);
                            }

                        }
                        viewCommentResList.add(viewCommentRes);
                    }


                }

                viewPostRes.addComment(viewCommentResList);
            }


            return ResponseEntity.status(HttpStatus.OK).body(viewPostRes);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("게시글을 찾을 수 없습니다.");
    }

    /* 게시글 좋아요 */
    @Transactional
    public ResponseEntity likeCount(String userId, Long postId) {

        Optional<User> userResult = userRepository.findById(userId);
        if (userResult.isPresent()){

            Optional<Post> postResult = postRepository.findById(postId);
            if(postResult.isPresent()){
                Post post = postResult.get();
                Optional<PostLike> postLikeResult = postLikeRepository.checkLikePost(postId, userId);
                if(postLikeResult.isPresent()){
                    PostLike postLike = postLikeResult.get();
                    postLikeRepository.delete(postLike);
                    post.downLikeCount();

                    return ResponseEntity.status(HttpStatus.OK).body("좋아요를 취소하였습니다.");
                }
                else {
                    PostLike postLike = new PostLike(userResult.get(),postResult.get());
                    postLikeRepository.save(postLike);
                    post.upLikeCount();
                    return ResponseEntity.status(HttpStatus.OK).body("해당 게시글을 좋아요 하였습니다.");
                }
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("게시글을 찾을 수 없습니다.");

        }
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body("회원만 가능한 기능입니다.");
    }

    /* 게시글 전체 리스트 조회 */
    public ResponseEntity listPost(int page) {
        PageRequest pageRequest = PageRequest.of(page, 10, Sort.by(Sort.Direction.DESC,"createAt"));
        Page<Post> pagingResult = postRepository.findAll(pageRequest);
        Page<ViewPostListRes> map = pagingResult.map(post -> new ViewPostListRes(post));

    return ResponseEntity.status(HttpStatus.OK).body(map);

    }

    /* 자신이 작성한 게시글 리스트 조회 */
    public ResponseEntity myPostList(int page, String userId){
        PageRequest pageRequest = PageRequest.of(page, 10, Sort.by(Sort.Direction.DESC,"createAt"));
        Page<Post> pagingResult = postRepository.findUserId(userId, pageRequest);
        Page<ViewPostListRes> map = pagingResult.map(post -> new ViewPostListRes(post));

        return ResponseEntity.status(HttpStatus.OK).body(map);
    }

    /* 좋아요 한 게시글 리스트 조회*/
    public ResponseEntity myLikePostList(int page, String userId) {
        PageRequest pageRequest = PageRequest.of(page, 10, Sort.by(Sort.Direction.DESC,"createAt"));
        Page<Post> pagingResult = postRepository.findLikePost(userId, pageRequest);
        Page<ViewPostListRes> map = pagingResult.map(post -> new ViewPostListRes(post));

        return ResponseEntity.status(HttpStatus.OK).body(map);
    }

    /* 댓글을 단 게시글 리스트 조회 */
    public ResponseEntity myCommentList(int page, String userId) {
        PageRequest pageRequest = PageRequest.of(page, 10, Sort.by(Sort.Direction.DESC,"createAt"));
        Page<Post> pagingResult = postRepository.findCommentUserId(userId, pageRequest);
        Page<ViewPostListRes> map = pagingResult.map(post -> new ViewPostListRes(post));

        return ResponseEntity.status(HttpStatus.OK).body(map);
    }

    /* 좋아요 한 댓글이 달린 게시글 리스트 조회 */
    public ResponseEntity myLikeCommentList(int page, String userId) {
        PageRequest pageRequest = PageRequest.of(page, 10, Sort.by(Sort.Direction.DESC,"createAt"));
        Page<Post> pagingResult = postRepository.findLikeComment(userId, pageRequest);
        Page<ViewPostListRes> map = pagingResult.map(post -> new ViewPostListRes(post));

        return ResponseEntity.status(HttpStatus.OK).body(map);
    }

    /* 좋아요가 많은 게시글 리스트 조회(3개) */
    public ResponseEntity bestListPost(){
        List<Post> bestPosts = postRepository.findTop3LikePost();
        List<ViewBestPostListRes> viewBestPostListResList = new ArrayList<>();

        if(bestPosts.size() > 0){
            for (Post bestPost : bestPosts) {
                ViewBestPostListRes viewBestPostListRes = new ViewBestPostListRes(bestPost);
                Optional<PostImage> thumbnail = postImageRepository.findThumbnail(bestPost.getId(), PostImageStatus.THUMBNAIL);
                if(thumbnail.isPresent()){
                    viewBestPostListRes.addThumbnail(thumbnail.get().getReturnUrl());
                }

                viewBestPostListResList.add(viewBestPostListRes);
            }
        }

        return ResponseEntity.status(HttpStatus.OK).body(viewBestPostListResList);
    }

    /* 게시글 작성자의 닉네임 검색 */
    public ResponseEntity findUserNickname(int page, String nickname){
        PageRequest pageRequest = PageRequest.of(page, 10, Sort.by(Sort.Direction.ASC,"createAt"));
        Page<Post> pagingResult = postRepository.findNickname(nickname, pageRequest);
        Page<ViewPostListRes> map = pagingResult.map(post -> new ViewPostListRes(post));

        return ResponseEntity.status(HttpStatus.OK).body(map);
    }

    /* 게시글 내용 검색 */
    public ResponseEntity findContent(int page, String content){
        PageRequest pageRequest = PageRequest.of(page, 10, Sort.by(Sort.Direction.DESC,"createAt"));
        Page<Post> pagingResult = postRepository.findContent(content, pageRequest);
        Page<ViewPostListRes> map = pagingResult.map(post -> new ViewPostListRes(post));

        return ResponseEntity.status(HttpStatus.OK).body(map);
    }

    /* 게시글 제목 검색 */
    public ResponseEntity findTitle(int page, String title){
        PageRequest pageRequest = PageRequest.of(page, 10, Sort.by(Sort.Direction.DESC,"createAt"));
        Page<Post> pagingResult = postRepository.findTitle(title, pageRequest);
        Page<ViewPostListRes> map = pagingResult.map(post -> new ViewPostListRes(post));

        return ResponseEntity.status(HttpStatus.OK).body(map);
    }

    /* 게시글 해시태그 검색 */
    public ResponseEntity findHashtag(int page, String hashtag){
        PageRequest pageRequest = PageRequest.of(page, 10, Sort.by(Sort.Direction.DESC,"createAt"));
        Page<Post> pagingResult = postRepository.findHashtag(hashtag, pageRequest);
        Page<ViewPostListRes> map = pagingResult.map(post -> new ViewPostListRes(post));

        return ResponseEntity.status(HttpStatus.OK).body(map);
    }

    /* 게시글 카테고리 정렬 */
    public ResponseEntity findCategory(int page, String category){
        PageRequest pageRequest = PageRequest.of(page, 10, Sort.by(Sort.Direction.DESC,"createAt"));
        if(isEnumValue(category)){
            return ResponseEntity.status(HttpStatus.CONFLICT).body("존재하지 않는 카테고리 입니다.");
        }
        PostCategory postCategory = PostCategory.valueOf(category);
        Page<Post> pagingResult = postRepository.findCategory(postCategory, pageRequest);
        Page<ViewPostListRes> map = pagingResult.map(post -> new ViewPostListRes(post));

        return ResponseEntity.status(HttpStatus.OK).body(map);
    }


    /* 게시글 신고 */
    @Transactional
    public ResponseEntity complainPost(String userId, ReportPostReq reportPostReq) {
        Optional<User> userResult = userRepository.findById(userId);
        if(userResult.isPresent()){
            Optional<ReportPost> reportPostResult = reportPostRepository.findReportPost(reportPostReq.getReportedUserNickname(), reportPostReq.getPostId());
            if(reportPostResult.isPresent()){
                return ResponseEntity.status(HttpStatus.CONFLICT).body("이미 처리된 신고입니다.");
            }
            Optional<User> reportedUserResult = userRepository.findByNickname(reportPostReq.getReportedUserNickname());
            Optional<Post> userPostResult = postRepository.findUserPost(reportPostReq.getPostId(), reportedUserResult.get().getId());
            if(userPostResult.isPresent()){

                ReportPost reportPost = new ReportPost(userResult.get(),reportedUserResult.get(),userPostResult.get(),reportPostReq.getReason());
                reportPostRepository.save(reportPost);
                return ResponseEntity.status(HttpStatus.OK).body("신고를 완료하였습니다.");
            }
            return  ResponseEntity.status(HttpStatus.NOT_FOUND).body("게시글과 작성자가 올바르지 않습니다.");
        }

        return ResponseEntity.status(HttpStatus.FORBIDDEN).body("회원만 사용 가능한 기능입니다.");
    }

    public static boolean isEnumValue(String category) {
        try {
            // Enum.valueOf() 메서드를 사용하여 입력값이 Enum 타입에 속하는지 확인
            PostCategory postCategory = Enum.valueOf(PostCategory.class, category);
            return false; // 속한다면 false 반환
        } catch (IllegalArgumentException e) {
            return true; // 속하지 않는다면 true 반환
        }
    }


}
