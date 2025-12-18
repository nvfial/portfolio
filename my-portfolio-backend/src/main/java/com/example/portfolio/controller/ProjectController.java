package com.example.portfolio.controller;

import com.example.portfolio.model.dto.ProjectDTO;
import com.example.portfolio.service.ProjectService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/projects")
@Tag(name = "Projects", description = "API for managing projects")
public class ProjectController {

    @Autowired
    private ProjectService service;

    @GetMapping
    @Operation(summary = "Get all projects")
    public ResponseEntity<List<ProjectDTO>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get project by id")
    public ResponseEntity<ProjectDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @GetMapping("/category/{category}")
    @Operation(summary = "Get projects by category")
    public ResponseEntity<List<ProjectDTO>> getByCategory(@PathVariable String category) {
        return ResponseEntity.ok(service.getByCategory(category));
    }

    @PostMapping
    @Operation(summary = "Create a new project")
    public CompletableFuture<ResponseEntity<ProjectDTO>> create(@RequestBody ProjectDTO dto) {
        return service.save(dto).thenApply(ResponseEntity::ok);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a project")
    public ResponseEntity<ProjectDTO> update(@PathVariable Long id, @RequestBody ProjectDTO dto) {
        return ResponseEntity.ok(service.update(id, dto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a project")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}


