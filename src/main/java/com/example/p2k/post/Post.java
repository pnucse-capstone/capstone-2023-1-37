package com.example.p2k.post;

import com.example.p2k.reply.Reply;
import com.example.p2k.course.Course;
import com.example.p2k.user.BaseTimeEntity;
import com.example.p2k.user.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name="post_tb")
public class Post extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_id")
    private Long id;

    @Column(length = 100, nullable = false)
    private String title;

    @Column(length = 20, nullable = false)
    private String author;

    @Column(length = 500, nullable = false)
    private String content;

    @Enumerated(EnumType.STRING)
    private Scope scope;

    @Enumerated(EnumType.STRING)
    private Category category;

    @ManyToOne
    @JoinColumn(name = "course_id")
    private Course course;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "post", fetch = FetchType.EAGER, cascade = CascadeType.REMOVE)
    private List<Reply> replies;

    @Builder
    public Post(Long id, String title, String author, String content, Scope scope, Category category, Course course, User user, List<Reply> replies) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.content = content;
        this.scope = scope;
        this.category = category;
        this.course = course;
        this.user = user;
        this.replies = replies;
    }

    public void updatePost(String title, String content, Scope scope){
        this.title = title;
        this.content = content;
        this.scope = scope;
    }
}
