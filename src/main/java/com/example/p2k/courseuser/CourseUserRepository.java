package com.example.p2k.courseuser;

import com.example.p2k.course.Course;
import com.example.p2k.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface CourseUserRepository extends JpaRepository<CourseUser, Long> {

    Optional<CourseUser> findByCourseIdAndUserId(Long courseId, Long userId);

    int countByUserId(Long userId);

    int countByCourseId(Long courseId);

    @Query("select cu.user from CourseUser cu where cu.course.id = :courseId and cu.user.id != :userId and cu.accept = true")
    List<User> findByCourseIdAndAcceptIsTrueAndUserIdNot(@Param("courseId") Long courseId, @Param("userId") Long userId);

    @Query("select cu.user from CourseUser cu where cu.course.id = :courseId and cu.accept = false")
    List<User> findByCourseIdAndAcceptIsFalse(@Param("courseId") Long courseId);

    @Query("select cu.course from CourseUser cu where cu.user.id = :userId and cu.accept = true")
    List<Course> findByUserIdAndAcceptIsTrue(@Param("userId")Long userId);

    @Query("select cu.course from CourseUser cu where cu.user.id = :userId and cu.accept = true")
    Page<Course> findByUserIdAndAcceptIsTrue(Pageable pageable, @Param("userId") Long userId);

    @Transactional
    @Modifying
    void deleteByCourseId(Long courseId);

    @Transactional
    @Modifying
    void deleteByUserId(Long userId);
}
