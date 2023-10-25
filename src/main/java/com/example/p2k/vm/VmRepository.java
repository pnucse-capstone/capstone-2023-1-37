package com.example.p2k.vm;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VmRepository extends JpaRepository<Vm, Long> {

    @Query("select v from Vm v where v.scope=:scope")
    List<Vm> findAll(@Param("scope") Boolean scope);

    @Query("select v from Vm v where v.vmname=:keyword")
    List<Vm> findAllByKeyword(@Param("keyword") String keyword);

    @Query("select v from Vm v where v.user.id= :id")
    List<Vm> findAllByUserId(@Param("id") Long id);

    List<Vm> findByUserIdAndCourseId(Long userId, Long courseId);

    List<Vm> findByUserIdAndCourseIdAndScopeIsTrue(Long userId, Long courseId);

    int countByUserId(Long userId);

    @Query("select v from Vm v join fetch v.course")
    List<Vm> findByCourseId(Long courseId);

    @Modifying
    @Query("delete from Vm v where v.user.id = :userId")
    void deleteByUserId(@Param("userId") Long userId);
}
