package com.example.p2k.course;

import com.example.p2k.user.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Entity
@Table(name="course_tb")
public class Course extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 100, nullable = false)
    private String name;

    @Column(length = 500, nullable = false)
    private String description;

    private Long instructorId;

    @Builder
    public Course(Long id, String name, String description, Long instructorId) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.instructorId = instructorId;
    }
}
