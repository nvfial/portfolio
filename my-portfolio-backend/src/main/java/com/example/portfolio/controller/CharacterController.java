package com.example.portfolio.controller;

import com.example.portfolio.model.dto.CharacterDTO;
import com.example.portfolio.service.CharacterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/characters")
@CrossOrigin(origins = "*")
public class CharacterController {
    
    @Autowired
    private CharacterService characterService;
    
    @GetMapping
    public ResponseEntity<List<CharacterDTO>> getAllCharacters() {
        return ResponseEntity.ok(characterService.getAllCharacters());
    }
    
    @GetMapping("/active")
    public ResponseEntity<List<CharacterDTO>> getActiveCharacters() {
        return ResponseEntity.ok(characterService.getActiveCharacters());
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<CharacterDTO> getCharacterById(@PathVariable Long id) {
        return ResponseEntity.ok(characterService.getCharacterById(id));
    }
    
    @PostMapping
    public ResponseEntity<CharacterDTO> createCharacter(@RequestBody CharacterDTO characterDTO) {
        return ResponseEntity.ok(characterService.createCharacter(characterDTO));
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<CharacterDTO> updateCharacter(@PathVariable Long id, @RequestBody CharacterDTO characterDTO) {
        return ResponseEntity.ok(characterService.updateCharacter(id, characterDTO));
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCharacter(@PathVariable Long id) {
        characterService.deleteCharacter(id);
        return ResponseEntity.ok().build();
    }
}