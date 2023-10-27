package com.example.p2k.reply;

import com.example.p2k.post.Post;
import com.example.p2k._core.util.BaseTimeEntity;
import com.example.p2k.user.User;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "reply_tb")
public class Reply extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 256, nullable = false)
    private String content;

    @Column(length = 256, nullable = false)
    private String author;

    private Long ref; //댓글 그룹 번호
    private Long refOrder; //그룹에서 순서
    private Long step; //댓글의 하위 레벨 정도
    private Long parentNum; //부모 댓글 PK
    private Long answerNum; //자식 댓글 개수

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "post_id")
    private Post post;

    @Builder
    public Reply(Long id, String content, String author, Long ref, Long refOrder, Long step, Long parentNum, Long answerNum, User user, Post post) {
        this.id = id;
        this.content = content;
        this.author = author;
        this.ref = ref;
        this.refOrder = refOrder;
        this.step = step;
        this.parentNum = parentNum;
        this.answerNum = answerNum;
        this.user = user;
        this.post = post;
    }

    public void updateAnswerNum(Long answerNum){
        this.answerNum = answerNum;
    }

    public void updateDeletedReply(String content, String author){
        this.content = content;
        this.author = author;
        this.user = null;
    }
}
