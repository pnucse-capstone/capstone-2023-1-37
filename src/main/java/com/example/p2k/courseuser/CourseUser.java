package com.example.p2k.courseuser;

import com.example.p2k.course.Course;
import com.example.p2k.user.BaseTimeEntity;
import com.example.p2k.user.User;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "course_user_tb")
public class CourseUser extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "course_id")
    private Course course;

    private Boolean accept;

    @Builder
    public CourseUser(Long id, User user, Course course, Boolean accept) {
        this.id = id;
        this.user = user;
        this.course = course;
        this.accept = accept;
    }

    public void updateAccept(Boolean accept){
        this.accept = accept;
    }
}
