package com.example.portfolio.controller;

import com.example.portfolio.model.dto.CommentDTO;
import com.example.portfolio.model.entity.Comment;
import com.example.portfolio.service.CommentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/comments")
@Tag(name = "Comments", description = "留言评论管理API")
public class CommentController {

    @Autowired
    private CommentService commentService;

    @GetMapping
    @Operation(summary = "获取留言列表")
    public ResponseEntity<Page<CommentDTO>> getComments(
            @RequestParam(required = false) String targetType,
            @RequestParam(required = false) Long targetId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "newest") String sortBy) {
        return ResponseEntity.ok(commentService.getComments(targetType, targetId, page, size, sortBy));
    }

    @GetMapping("/all")
    @Operation(summary = "获取所有留言")
    public ResponseEntity<Page<CommentDTO>> getAllComments(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "newest") String sortBy) {
        return ResponseEntity.ok(commentService.getAllComments(page, size, sortBy));
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取单条留言")
    public ResponseEntity<CommentDTO> getCommentById(@PathVariable Long id) {
        return ResponseEntity.ok(commentService.getCommentById(id));
    }

    @PostMapping
    @Operation(summary = "创建留言")
    public ResponseEntity<CommentDTO> createComment(@RequestBody Comment comment) {
        return ResponseEntity.ok(commentService.createComment(comment));
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新留言")
    public ResponseEntity<CommentDTO> updateComment(@PathVariable Long id, @RequestBody Comment comment) {
        return ResponseEntity.ok(commentService.updateComment(id, comment));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除留言")
    public ResponseEntity<Void> deleteComment(@PathVariable Long id) {
        commentService.deleteComment(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/approve")
    @Operation(summary = "审核通过留言")
    public ResponseEntity<CommentDTO> approveComment(@PathVariable Long id) {
        return ResponseEntity.ok(commentService.approveComment(id));
    }

    @PostMapping("/{id}/like")
    @Operation(summary = "点赞留言")
    public ResponseEntity<CommentDTO> likeComment(@PathVariable Long id) {
        return ResponseEntity.ok(commentService.likeComment(id));
    }

    @GetMapping("/count")
    @Operation(summary = "获取留言总数")
    public ResponseEntity<Long> countComments() {
        return ResponseEntity.ok(commentService.countComments());
    }
}
