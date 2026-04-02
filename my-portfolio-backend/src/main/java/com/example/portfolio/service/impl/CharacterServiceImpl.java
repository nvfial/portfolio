package com.example.portfolio.service.impl;

import com.example.portfolio.model.dto.*;
import com.example.portfolio.model.entity.*;
import com.example.portfolio.repository.*;
import com.example.portfolio.service.CharacterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CharacterServiceImpl implements CharacterService {
    
    @Autowired
    private CharacterRepository characterRepository;
    
    @Autowired
    private StoryRepository storyRepository;
    
    @Autowired
    private TimelineEventRepository timelineEventRepository;
    
    @Autowired
    private QuoteRepository quoteRepository;
    
    @Autowired
    private MediaRepository mediaRepository;
    
    @Override
    public List<CharacterDTO> getAllCharacters() {
        List<Character> characters = characterRepository.findAll();
        return characters.stream().map(this::toDTO).collect(Collectors.toList());
    }
    
    @Override
    public CharacterDTO getCharacterById(Long id) {
        Character character = characterRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Character not found with id: " + id));
        return toDTOWithRelations(character);
    }
    
    @Override
    public List<CharacterDTO> getActiveCharacters() {
        List<Character> characters = characterRepository.findByIsActiveTrueOrderBySortOrderAsc();
        return characters.stream().map(this::toDTO).collect(Collectors.toList());
    }
    
    @Override
    @Transactional
    public CharacterDTO createCharacter(CharacterDTO characterDTO) {
        Character character = toEntity(characterDTO);
        Character saved = characterRepository.save(character);
        return toDTO(saved);
    }
    
    @Override
    @Transactional
    public CharacterDTO updateCharacter(Long id, CharacterDTO characterDTO) {
        Character character = characterRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Character not found with id: " + id));
        
        character.setName(characterDTO.getName());
        character.setTitle(characterDTO.getTitle());
        character.setCv(characterDTO.getCv());
        character.setShortDesc(characterDTO.getShortDesc());
        character.setPortraitUrl(characterDTO.getPortraitUrl());
        character.setBgImageUrl(characterDTO.getBgImageUrl());
        character.setVoiceUrl(characterDTO.getVoiceUrl());
        character.setFullBodyUrl(characterDTO.getFullBodyUrl());
        character.setSceneUrl(characterDTO.getSceneUrl());
        character.setBio(characterDTO.getBio());
        character.setSortOrder(characterDTO.getSortOrder());
        character.setIsActive(characterDTO.getIsActive());
        
        character.setThemeColor(characterDTO.getThemeColor());
        character.setThemeColorSecondary(characterDTO.getThemeColorSecondary());
        character.setBackgroundType(characterDTO.getBackgroundType());
        character.setParticleEffect(characterDTO.getParticleEffect());
        character.setFrameStyle(characterDTO.getFrameStyle());
        character.setFontFamily(characterDTO.getFontFamily());
        character.setEntranceAnimation(characterDTO.getEntranceAnimation());
        character.setTitleAnimation(characterDTO.getTitleAnimation());
        character.setThemePreset(characterDTO.getThemePreset());
        character.setCustomThemeConfig(characterDTO.getCustomThemeConfig());
        
        Character saved = characterRepository.save(character);
        return toDTO(saved);
    }
    
    @Override
    @Transactional
    public void deleteCharacter(Long id) {
        characterRepository.deleteById(id);
    }
    
    private CharacterDTO toDTO(Character character) {
        CharacterDTO dto = new CharacterDTO();
        dto.setId(character.getId());
        dto.setName(character.getName());
        dto.setTitle(character.getTitle());
        dto.setCv(character.getCv());
        dto.setShortDesc(character.getShortDesc());
        dto.setPortraitUrl(character.getPortraitUrl());
        dto.setBgImageUrl(character.getBgImageUrl());
        dto.setVoiceUrl(character.getVoiceUrl());
        dto.setFullBodyUrl(character.getFullBodyUrl());
        dto.setSceneUrl(character.getSceneUrl());
        dto.setBio(character.getBio());
        dto.setSortOrder(character.getSortOrder());
        dto.setIsActive(character.getIsActive());
        dto.setCreatedAt(character.getCreatedAt());
        dto.setUpdatedAt(character.getUpdatedAt());
        
        dto.setThemeColor(character.getThemeColor());
        dto.setThemeColorSecondary(character.getThemeColorSecondary());
        dto.setBackgroundType(character.getBackgroundType());
        dto.setParticleEffect(character.getParticleEffect());
        dto.setFrameStyle(character.getFrameStyle());
        dto.setFontFamily(character.getFontFamily());
        dto.setEntranceAnimation(character.getEntranceAnimation());
        dto.setTitleAnimation(character.getTitleAnimation());
        dto.setThemePreset(character.getThemePreset());
        dto.setCustomThemeConfig(character.getCustomThemeConfig());
        
        return dto;
    }
    
    private CharacterDTO toDTOWithRelations(Character character) {
        CharacterDTO dto = toDTO(character);
        
        List<Story> stories = storyRepository.findByCharacterIdOrderByChapterOrderAsc(character.getId());
        dto.setStories(stories.stream().map(this::toStoryDTO).collect(Collectors.toList()));
        
        List<TimelineEvent> events = timelineEventRepository.findByCharacterIdOrderBySortOrderAsc(character.getId());
        dto.setTimelineEvents(events.stream().map(this::toTimelineEventDTO).collect(Collectors.toList()));
        
        List<Quote> quotes = quoteRepository.findByCharacterId(character.getId());
        dto.setQuotes(quotes.stream().map(this::toQuoteDTO).collect(Collectors.toList()));
        
        List<Media> media = mediaRepository.findByCharacterIdOrderBySortOrderAsc(character.getId());
        dto.setMediaList(media.stream().map(this::toMediaDTO).collect(Collectors.toList()));
        
        return dto;
    }
    
    private StoryDTO toStoryDTO(Story story) {
        StoryDTO dto = new StoryDTO();
        dto.setId(story.getId());
        if (story.getCharacter() != null) {
            dto.setCharacterId(story.getCharacter().getId());
        }
        dto.setChapterTitle(story.getChapterTitle());
        dto.setChapterOrder(story.getChapterOrder());
        dto.setContentType(story.getContentType());
        dto.setContent(story.getContent());
        dto.setMediaUrls(story.getMediaUrls());
        dto.setBgmUrl(story.getBgmUrl());
        dto.setCoverImageUrl(story.getCoverImageUrl());
        dto.setCreatedAt(story.getCreatedAt());
        return dto;
    }
    
    private TimelineEventDTO toTimelineEventDTO(TimelineEvent event) {
        TimelineEventDTO dto = new TimelineEventDTO();
        dto.setId(event.getId());
        if (event.getCharacter() != null) {
            dto.setCharacterId(event.getCharacter().getId());
        }
        dto.setEventYear(event.getEventYear());
        dto.setEventTitle(event.getEventTitle());
        dto.setEventDesc(event.getEventDesc());
        dto.setEventImageUrl(event.getEventImageUrl());
        dto.setEventType(event.getEventType());
        dto.setSortOrder(event.getSortOrder());
        return dto;
    }
    
    private QuoteDTO toQuoteDTO(Quote quote) {
        QuoteDTO dto = new QuoteDTO();
        dto.setId(quote.getId());
        if (quote.getCharacter() != null) {
            dto.setCharacterId(quote.getCharacter().getId());
        }
        dto.setQuoteText(quote.getQuoteText());
        dto.setQuoteAuthor(quote.getQuoteAuthor());
        dto.setContext(quote.getContext());
        dto.setIsFeatured(quote.getIsFeatured());
        return dto;
    }
    
    private MediaDTO toMediaDTO(Media media) {
        MediaDTO dto = new MediaDTO();
        dto.setId(media.getId());
        if (media.getCharacter() != null) {
            dto.setCharacterId(media.getCharacter().getId());
        }
        dto.setMediaType(media.getMediaType());
        dto.setUrl(media.getUrl());
        dto.setTitle(media.getTitle());
        dto.setDescription(media.getDescription());
        dto.setSortOrder(media.getSortOrder());
        return dto;
    }
    
    private Character toEntity(CharacterDTO dto) {
        Character character = new Character();
        character.setName(dto.getName());
        character.setTitle(dto.getTitle());
        character.setCv(dto.getCv());
        character.setShortDesc(dto.getShortDesc());
        character.setPortraitUrl(dto.getPortraitUrl());
        character.setBgImageUrl(dto.getBgImageUrl());
        character.setVoiceUrl(dto.getVoiceUrl());
        character.setFullBodyUrl(dto.getFullBodyUrl());
        character.setSceneUrl(dto.getSceneUrl());
        character.setBio(dto.getBio());
        character.setSortOrder(dto.getSortOrder());
        character.setIsActive(dto.getIsActive());
        
        character.setThemeColor(dto.getThemeColor());
        character.setThemeColorSecondary(dto.getThemeColorSecondary());
        character.setBackgroundType(dto.getBackgroundType());
        character.setParticleEffect(dto.getParticleEffect());
        character.setFrameStyle(dto.getFrameStyle());
        character.setFontFamily(dto.getFontFamily());
        character.setEntranceAnimation(dto.getEntranceAnimation());
        character.setTitleAnimation(dto.getTitleAnimation());
        character.setThemePreset(dto.getThemePreset());
        character.setCustomThemeConfig(dto.getCustomThemeConfig());
        
        return character;
    }
}