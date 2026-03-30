package com.example.portfolio.repository;

import com.example.portfolio.model.entity.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    List<Comment> findByParentIdAndStatusOrderByCreatedAtDesc(Long parentId, String status);

    Page<Comment> findByStatusAndTargetTypeAndTargetIdOrderByCreatedAtDesc(
        String status, String targetType, Long targetId, Pageable pageable);

    Page<Comment> findByStatusOrderByCreatedAtDesc(String status, Pageable pageable);

    List<Comment> findByUserNameAndStatusOrderByCreatedAtDesc(String userName, String status);

    @Query("SELECT COUNT(c) FROM Comment c WHERE c.status = 'approved'")
    Long countApprovedComments();

    @Query("SELECT c FROM Comment c WHERE c.parent IS NULL AND c.status = :status ORDER BY c.createdAt DESC")
    Page<Comment> findRootComments(String status, Pageable pageable);
}
