package com.example.p2k.reply;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ReplyRepository extends JpaRepository<Reply, Long> {

    int countByUserId(Long userId);

    @Query("select r from Reply r where r.post.id = :postId order by r.ref, r.refOrder")
    Page<Reply> findByPostId(Pageable pageable, @Param("postId") Long postId);

    @Query("select max(r.ref) from Reply r where r.post.id = :postId")
    Long findMaxRefByPostId(@Param("postId") Long postId);

    @Query("select max(r.refOrder) from Reply r where r.ref = :ref")
    Long findMaxRefOrderByRef(@Param("ref") Long ref);

    @Query("select min(r.refOrder) from Reply r where r.ref = :ref and r.step <= :step and r.refOrder > :refOrder")
    Long findRefOrder(@Param("ref") Long ref, @Param("step") Long step, @Param("refOrder") Long refOrder);

    @Modifying
    @Query("update Reply r SET r.refOrder = r.refOrder + 1 where r.ref = :ref and r.refOrder >= :refOrder")
    void updateRefOrder(@Param("ref") Long ref, @Param("refOrder") Long refOrder);
}
