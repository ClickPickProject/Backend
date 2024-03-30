package com.clickpick.service;

import com.clickpick.domain.*;
import com.clickpick.dto.comment.CreateCommentReq;
import com.clickpick.dto.comment.CreateReCommentReq;
import com.clickpick.dto.comment.ReportCommentReq;
import com.clickpick.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommentService {
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final CommentLikeRepository commentLikeRepository;
    private final ReportCommentRepository reportCommentRepository;

    /* 댓글 작성 */
    @Transactional
    public ResponseEntity createComment(String userId, CreateCommentReq createCommentReq) {
        Optional<User> result = userRepository.findById(userId);
        if(result.isPresent()){
            User user = result.get();
            Optional<Post> postResult = postRepository.findById(createCommentReq.getPostId());
            if(postResult.isPresent()){
                Post post = postResult.get();
                Comment comment = new Comment(post,user,createCommentReq.getContent(),null);
                commentRepository.save(comment);
                return ResponseEntity.status(HttpStatus.OK).body("댓글이 등록되었습니다.");
            }


            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("존재하지 않는 게시글입니다.");
        }
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body("회원만 가능한 기능입니다.");
    }


    /* 댓글 삭제 */
    @Transactional
    public ResponseEntity deleteComment(String userId, Long commentId) {
        //유저와 게시글 id를 비교하여 동일한 값을 가져옴 => 없으면 본인이 작성하지 않음
        Optional<Comment> result = commentRepository.findUserComment(commentId, userId);

        if(result.isPresent()){
            if(result.get().getParent()!=null){ // 대댓글을 삭제한 경우
                commentRepository.delete(result.get());
                if(result.get().getParent().getStatus() == CommentStatus.DELETE && result.get().getParent().getComments().size() -1 == 0){ // 해당 대댓글 삭제 시 관련된 모든 댓글 삭제된 경우 -1은 DB반영이 해당 함수가 끝나야 적용되므로 -1을 하며 미리 적용된것으로 함
                    commentRepository.delete(result.get().getParent());
                    return ResponseEntity.status(HttpStatus.OK).body("삭제가 완료되었습니다.");
                }
                return ResponseEntity.status(HttpStatus.OK).body("삭제가 완료되었습니다.");
            }
            else if(result.get().getParent() == null && result.get().getComments().size() == 0){ // 댓글이면서 대댓글이 없는 경우
                commentRepository.delete(result.get());
                return ResponseEntity.status(HttpStatus.OK).body("삭제가 완료되었습니다.");
            }
            else { // 댓글이면서 대댓글이 있는 경우
                result.get().tempDelete();
                return ResponseEntity.status(HttpStatus.OK).body("삭제가 완료되었습니다.");
            }
        }
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body("사용자가 삭제할 수 없는 댓글입니다.");

    }

    /* 댓글 수정 */
    @Transactional
    public ResponseEntity renewComment(String userId, Long commentId, CreateCommentReq updateCommentReq) {
        Optional<Comment> result = commentRepository.findUserComment(commentId, userId);

        if(result.isPresent()){
            Comment comment = result.get();
            comment.changeComment(updateCommentReq.getContent());

            return ResponseEntity.status(HttpStatus.OK).body("수정이 완료되었습니다.");
        }
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body("사용자가 수정할 수 없는 댓글입니다.");

    }

    /* 댓글 좋아요 */
    @Transactional
    public ResponseEntity likeCount(String userId, Long commentId) {
        Optional<User> userResult = userRepository.findById(userId);
        if (userResult.isPresent()){

            Optional<Comment> commentResult = commentRepository.findById(commentId);
            if(commentResult.isPresent()){
                Optional<CommentLike> commentLikeResult = commentLikeRepository.checkLikeComment(commentId, userId);
                if(commentLikeResult.isPresent()){
                    CommentLike commentLike = commentLikeResult.get();
                    commentLikeRepository.delete(commentLike);
                    return ResponseEntity.status(HttpStatus.OK).body("좋아요를 취소하였습니다.");
                }
                else {
                    CommentLike commentLike = new CommentLike(userResult.get(), commentResult.get());
                    commentLikeRepository.save(commentLike);
                    return ResponseEntity.status(HttpStatus.OK).body("해당 댓글을 좋아요 하였습니다.");
                }
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("댓글을 찾을 수 없습니다.");

        }
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body("회원만 가능한 기능입니다.");
    }

    /* 대댓글 작성 */
    public ResponseEntity renewReComment(String userId, CreateReCommentReq createReCommentReq) {
        Optional<User> userResult = userRepository.findById(userId);
        if(userResult.isPresent()){
            User user = userResult.get();
            Optional<Post> postResult = postRepository.findById(createReCommentReq.getPostId());
            if(postResult.isPresent()){
                Post post = postResult.get();
                Optional<Comment> parentCommentResult = commentRepository.findById(createReCommentReq.getParentCommentId());
                if(parentCommentResult.isPresent()){
                    Comment parentComment = parentCommentResult.get();
                    Comment comment = new Comment(post, user, createReCommentReq.getContent(), parentComment);
                    commentRepository.save(comment);
                    return ResponseEntity.status(HttpStatus.OK).body("댓글이 등록되었습니다.");
                }

                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("존재하지 않는 게시글 입니다.");
            }

            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("상위 댓글을 찾을 수 없습니다.");
        }

        return ResponseEntity.status(HttpStatus.FORBIDDEN).body("회원만 가능한 기능입니다.");
    }

    /* 댓글 신고 */
    @Transactional
    public ResponseEntity complainComment(String userId, ReportCommentReq reportCommentReq) {
        Optional<User> userResult = userRepository.findById(userId);
        if(userResult.isPresent()){
            Optional<ReportComment> reportCommentResult = reportCommentRepository.findReportComment(reportCommentReq.getReportedUserNickname(), reportCommentReq.getCommentId());
            if(reportCommentResult.isPresent()){
                return ResponseEntity.status(HttpStatus.CONFLICT).body("이미 처리된 신고입니다.");
            }
            Optional<User> reportedUserResult = userRepository.findByNickname(reportCommentReq.getReportedUserNickname());
            Optional<Comment> userCommentResult = commentRepository.findUserComment(reportCommentReq.getCommentId(), reportedUserResult.get().getId());
            if(userCommentResult.isPresent()){
                ReportComment reportComment = new ReportComment(userCommentResult.get(),userResult.get(),reportedUserResult.get(), reportCommentReq.getReason());
                reportCommentRepository.save(reportComment); //중복 신고 체크 해야함
                return ResponseEntity.status(HttpStatus.OK).body("신고를 완료하였습니다.");
            }
            return  ResponseEntity.status(HttpStatus.NOT_FOUND).body("댓글과 작성자가 올바르지 않습니다.");
        }

        return ResponseEntity.status(HttpStatus.FORBIDDEN).body("회원만 사용 가능한 기능입니다.");
    }
}
