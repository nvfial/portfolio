package com.example.portfolio.controller;

import com.example.portfolio.model.dto.TestimonialDTO;
import com.example.portfolio.service.TestimonialService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/testimonials")
@Tag(name = "Testimonials", description = "API for managing testimonials")
public class TestimonialController {

    @Autowired
    private TestimonialService service;

    @GetMapping
    @Operation(summary = "Get all testimonials")
    public ResponseEntity<List<TestimonialDTO>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get testimonial by id")
    public ResponseEntity<TestimonialDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @PostMapping
    @Operation(summary = "Create a new testimonial")
    public ResponseEntity<TestimonialDTO> create(@RequestBody TestimonialDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.save(dto));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a testimonial")
    public ResponseEntity<TestimonialDTO> update(@PathVariable Long id, @RequestBody TestimonialDTO dto) {
        return ResponseEntity.ok(service.update(id, dto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a testimonial")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
