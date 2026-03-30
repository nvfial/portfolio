package com.example.portfolio.service.impl;

import com.example.portfolio.model.dto.CommentDTO;
import com.example.portfolio.model.entity.Comment;
import com.example.portfolio.repository.CommentRepository;
import com.example.portfolio.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class CommentServiceImpl implements CommentService {

    @Autowired
    private CommentRepository commentRepository;

    @Override
    public Page<CommentDTO> getComments(String targetType, Long targetId, int page, int size, String sortBy) {
        Sort sort = "newest".equals(sortBy) 
            ? Sort.by(Sort.Direction.DESC, "createdAt")
            : Sort.by(Sort.Direction.ASC, "createdAt");
        
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<Comment> comments;
        if (targetType != null && targetId != null) {
            comments = commentRepository.findByStatusAndTargetTypeAndTargetIdOrderByCreatedAtDesc(
                "approved", targetType, targetId, pageable);
        } else {
            comments = commentRepository.findRootComments("approved", pageable);
        }
        
        return comments.map(this::convertToDTO);
    }

    @Override
    public Page<CommentDTO> getAllComments(int page, int size, String sortBy) {
        Sort sort = "newest".equals(sortBy)
            ? Sort.by(Sort.Direction.DESC, "createdAt")
            : Sort.by(Sort.Direction.ASC, "createdAt");
        
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Comment> comments = commentRepository.findRootComments("approved", pageable);
        
        return comments.map(this::convertToDTO);
    }

    @Override
    public CommentDTO getCommentById(Long id) {
        Comment comment = commentRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Comment not found: " + id));
        return convertToDTO(comment);
    }

    @Override
    public CommentDTO createComment(Comment comment) {
        comment.setStatus("pending");
        comment.setLikesCount(0);
        Comment saved = commentRepository.save(comment);
        return convertToDTO(saved);
    }

    @Override
    public CommentDTO updateComment(Long id, Comment comment) {
        Comment existing = commentRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Comment not found: " + id));
        
        existing.setContent(comment.getContent());
        existing.setUserName(comment.getUserName());
        existing.setUserEmail(comment.getUserEmail());
        existing.setUserWebsite(comment.getUserWebsite());
        
        Comment saved = commentRepository.save(existing);
        return convertToDTO(saved);
    }

    @Override
    public void deleteComment(Long id) {
        commentRepository.deleteById(id);
    }

    @Override
    public CommentDTO approveComment(Long id) {
        Comment comment = commentRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Comment not found: " + id));
        comment.setStatus("approved");
        Comment saved = commentRepository.save(comment);
        return convertToDTO(saved);
    }

    @Override
    public CommentDTO likeComment(Long id) {
        Comment comment = commentRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Comment not found: " + id));
        comment.setLikesCount(comment.getLikesCount() + 1);
        Comment saved = commentRepository.save(comment);
        return convertToDTO(saved);
    }

    @Override
    public Long countComments() {
        return commentRepository.countApprovedComments();
    }

    private CommentDTO convertToDTO(Comment comment) {
        CommentDTO dto = new CommentDTO();
        dto.setId(comment.getId());
        
        if (comment.getParent() != null) {
            dto.setParentId(comment.getParent().getId());
        }
        
        dto.setUserName(comment.getUserName());
        dto.setUserEmail(comment.getUserEmail());
        dto.setUserWebsite(comment.getUserWebsite());
        dto.setUserAvatar(comment.getUserAvatar());
        dto.setContent(comment.getContent());
        dto.setTargetType(comment.getTargetType());
        dto.setTargetId(comment.getTargetId());
        dto.setStatus(comment.getStatus());
        dto.setLikesCount(comment.getLikesCount());
        dto.setCreatedAt(comment.getCreatedAt());
        
        List<Comment> replies = commentRepository.findByParentIdAndStatusOrderByCreatedAtDesc(
            comment.getId(), "approved");
        dto.setReplies(replies.stream().map(this::convertToDTO).collect(Collectors.toList()));
        
        return dto;
    }
}
