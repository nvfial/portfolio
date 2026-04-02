package com.example.portfolio.service;

import com.example.portfolio.model.dto.CharacterDTO;
import java.util.List;

public interface CharacterService {
    
    List<CharacterDTO> getAllCharacters();
    
    CharacterDTO getCharacterById(Long id);
    
    List<CharacterDTO> getActiveCharacters();
    
    CharacterDTO createCharacter(CharacterDTO characterDTO);
    
    CharacterDTO updateCharacter(Long id, CharacterDTO characterDTO);
    
    void deleteCharacter(Long id);
}