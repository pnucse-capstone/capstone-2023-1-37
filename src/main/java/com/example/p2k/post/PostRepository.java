package com.example.p2k.post;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface PostRepository extends JpaRepository<Post, Long> {

    int countByUserId(Long userId);

    @Query("select p from Post p where p.course.id = :courseId and p.category = :category and " +
            "(p.scope = :scope or p.user.id = :userId or p.course.instructorId = :userId)")
    Page<Post> findByCourseIdAndCategoryAndUserIdOrScope(Pageable pageable, @Param("courseId") Long courseId, @Param("category") Category category,
                                                         @Param("userId") Long userId, @Param("scope") Scope scope);

    @Transactional
    @Modifying
    void deleteByCourseId(Long courseId);
}
