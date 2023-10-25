package com.example.p2k.reply;

import com.example.p2k._core.exception.Exception400;
import com.example.p2k._core.exception.Exception404;
import com.example.p2k.post.Post;
import com.example.p2k.post.PostRepository;
import com.example.p2k.user.User;
import com.example.p2k.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class ReplyService {

    public static final int DEFAULT_PAGE_SIZE = 10;
    public static final int DEFAULT_PAGINATION_SIZE = 5;
    public static final long MAX_REPLY_COUNT = 15L;

    private final ReplyRepository replyRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    //댓글 목록 조회
    public ReplyResponse.FindRepliesDTO findByPostId(Long postId, int page){
        Pageable pageable = PageRequest.of(page, DEFAULT_PAGE_SIZE);
        Page<Reply> replies = replyRepository.findByPostId(pageable, postId);
        return new ReplyResponse.FindRepliesDTO(replies, DEFAULT_PAGINATION_SIZE);
    }

    //댓글 저장
    @Transactional
    public void saveComment(ReplyRequest.SaveCommentDTO requestDTO, Long userId, Long postId){
        Post post = getPost(postId);
        User user = getUser(userId);

        Reply reply = Reply.builder()
                .content(requestDTO.getContent())
                .author(user.getName())
                .ref(getMaxRef(postId))
                .refOrder(0L)
                .step(0L)
                .parentNum(0L)
                .answerNum(0L)
                .user(user)
                .post(post)
                .build();

        replyRepository.save(reply);
    }

    //답글 저장
    @Transactional
    public void saveReply(ReplyRequest.SaveReplyDTO requestDTO, Long parentId, Long userId, Long postId){
        Post post = getPost(postId);
        User user = getUser(userId);
        Reply parentReply = getReply(parentId);

        validateMaxReplyCount(parentReply);

        Reply reply = Reply.builder()
                .content(requestDTO.getContent())
                .author(user.getName())
                .ref(parentReply.getRef())
                .refOrder(getRefOrder(parentReply))
                .step(parentReply.getStep() + 1L)
                .parentNum(parentId)
                .answerNum(0L)
                .post(post)
                .user(user)
                .build();

        replyRepository.save(reply);
        parentReply.updateAnswerNum(parentReply.getAnswerNum() + 1);
    }

    //댓글 삭제
    @Transactional
    public void delete(Long replyId){
        Reply reply = getReply(replyId);
        reply.updateDeletedReply("삭제된 댓글입니다.", "비밀^8^");
    }

    private Long getRefOrder(Reply parentReply){
        Long parentRef = parentReply.getRef();
        Long parentRefOrder = parentReply.getRefOrder();
        Long parentStep = parentReply.getStep();

        Long refOrder = replyRepository.findRefOrder(parentRef, parentStep, parentRefOrder);

        if (refOrder == null) {
            refOrder = replyRepository.findMaxRefOrderByRef(parentRef) + 1;
        } else {
            replyRepository.updateRefOrder(parentRef, refOrder);
        }

        return refOrder;
    }

    private Long getMaxRef(Long postId) {
        Long maxRef = replyRepository.findMaxRefByPostId(postId);
        maxRef = (maxRef != null) ? maxRef + 1L : 0L;
        return maxRef;
    }

    private static void validateMaxReplyCount(Reply parentReply) {
        if(parentReply.getStep() >= MAX_REPLY_COUNT){
            throw new Exception400("더 이상 답글을 생성할 수 없습니다.");
        }
    }

    private Reply getReply(Long replyId) {
        return replyRepository.findById(replyId).orElseThrow(
                () -> new Exception404("해당 댓글을 찾을 수 없습니다.")
        );
    }

    private Post getPost(Long postId) {
        return postRepository.findById(postId).orElseThrow(
                () -> new Exception404("해당 게시글을 찾을 수 없습니다.")
        );
    }

    private User getUser(Long userId) {
        return userRepository.findById(userId).orElseThrow(
                () -> new Exception404("해당 사용자를 찾을 수 없습니다.")
        );
    }
}