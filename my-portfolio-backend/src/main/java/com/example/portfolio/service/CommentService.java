package com.example.portfolio.service;

import com.example.portfolio.model.dto.CommentDTO;
import com.example.portfolio.model.entity.Comment;
import org.springframework.data.domain.Page;

import java.util.List;

public interface CommentService {
    
    Page<CommentDTO> getComments(String targetType, Long targetId, int page, int size, String sortBy);
    
    Page<CommentDTO> getAllComments(int page, int size, String sortBy);
    
    CommentDTO getCommentById(Long id);
    
    CommentDTO createComment(Comment comment);
    
    CommentDTO updateComment(Long id, Comment comment);
    
    void deleteComment(Long id);
    
    CommentDTO approveComment(Long id);
    
    CommentDTO likeComment(Long id);
    
    Long countComments();
}
