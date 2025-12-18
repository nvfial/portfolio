package com.example.portfolio.controller;

import com.example.portfolio.model.dto.ContactDTO;
import com.example.portfolio.service.ContactService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/contacts")
@Tag(name = "Contacts", description = "API for managing contact messages")
public class ContactController {

    @Autowired
    private ContactService service;

    @GetMapping
    @Operation(summary = "Get all contacts")
    public ResponseEntity<List<ContactDTO>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get contact by id")
    public ResponseEntity<ContactDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @PostMapping
    @Operation(summary = "Create a new contact message")
    public ResponseEntity<ContactDTO> create(@RequestBody ContactDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.save(dto));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a contact message")
    public ResponseEntity<ContactDTO> update(@PathVariable Long id, @RequestBody ContactDTO dto) {
        return ResponseEntity.ok(service.update(id, dto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a contact message")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}

