package com.example.portfolio.service.impl;

import com.example.portfolio.model.dto.GalleryCollectionDTO;
import com.example.portfolio.model.dto.GalleryArtworkDTO;
import com.example.portfolio.model.entity.GalleryCollection;
import com.example.portfolio.model.entity.GalleryArtwork;
import com.example.portfolio.repository.GalleryCollectionRepository;
import com.example.portfolio.repository.GalleryArtworkRepository;
import com.example.portfolio.service.GalleryService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class GalleryServiceImpl implements GalleryService {

    @Autowired
    private GalleryCollectionRepository collectionRepository;

    @Autowired
    private GalleryArtworkRepository artworkRepository;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public List<GalleryCollectionDTO> getAllCollections() {
        List<GalleryCollection> collections = collectionRepository.findByIsActiveTrueOrderBySortOrder();
        return collections.stream().map(this::convertCollectionToDTO).collect(Collectors.toList());
    }

    @Override
    public List<GalleryCollectionDTO> getCollectionsByCategory(String category) {
        List<GalleryCollection> collections = collectionRepository
            .findByCategoryAndIsActiveTrueOrderBySortOrder(category);
        return collections.stream().map(this::convertCollectionToDTO).collect(Collectors.toList());
    }

    @Override
    public GalleryCollectionDTO getCollectionBySlug(String slug) {
        GalleryCollection collection = collectionRepository.findBySlug(slug)
            .orElseThrow(() -> new RuntimeException("Collection not found: " + slug));
        return convertCollectionToDTO(collection);
    }

    @Override
    public GalleryCollectionDTO createCollection(GalleryCollection collection) {
        GalleryCollection saved = collectionRepository.save(collection);
        return convertCollectionToDTO(saved);
    }

    @Override
    public GalleryCollectionDTO updateCollection(Long id, GalleryCollection collection) {
        GalleryCollection existing = collectionRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Collection not found: " + id));

        existing.setName(collection.getName());
        existing.setSlug(collection.getSlug());
        existing.setDescription(collection.getDescription());
        existing.setCoverImage(collection.getCoverImage());
        existing.setCategory(collection.getCategory());
        existing.setSortOrder(collection.getSortOrder());
        existing.setIsActive(collection.getIsActive());

        GalleryCollection saved = collectionRepository.save(existing);
        return convertCollectionToDTO(saved);
    }

    @Override
    public void deleteCollection(Long id) {
        collectionRepository.deleteById(id);
    }

    @Override
    public List<GalleryArtworkDTO> getArtworksByCollection(Long collectionId) {
        List<GalleryArtwork> artworks = artworkRepository
            .findByCollectionIdAndIsActiveTrueOrderBySortOrder(collectionId);
        return artworks.stream().map(this::convertArtworkToDTO).collect(Collectors.toList());
    }

    @Override
    public GalleryArtworkDTO getArtworkById(Long id) {
        GalleryArtwork artwork = artworkRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Artwork not found: " + id));
        return convertArtworkToDTO(artwork);
    }

    @Override
    public GalleryArtworkDTO createArtwork(GalleryArtwork artwork) {
        GalleryArtwork saved = artworkRepository.save(artwork);
        return convertArtworkToDTO(saved);
    }

    @Override
    public GalleryArtworkDTO updateArtwork(Long id, GalleryArtwork artwork) {
        GalleryArtwork existing = artworkRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Artwork not found: " + id));

        existing.setTitle(artwork.getTitle());
        existing.setDescription(artwork.getDescription());
        existing.setImageUrl(artwork.getImageUrl());
        existing.setThumbnailUrl(artwork.getThumbnailUrl());
        existing.setArtist(artwork.getArtist());
        existing.setYear(artwork.getYear());
        existing.setMedium(artwork.getMedium());
        existing.setDimensions(artwork.getDimensions());
        existing.setTags(artwork.getTags());
        existing.setSortOrder(artwork.getSortOrder());
        existing.setIsActive(artwork.getIsActive());

        GalleryArtwork saved = artworkRepository.save(existing);
        return convertArtworkToDTO(saved);
    }

    @Override
    public void deleteArtwork(Long id) {
        artworkRepository.deleteById(id);
    }

    @Override
    public void incrementArtworkView(Long id) {
        GalleryArtwork artwork = artworkRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Artwork not found: " + id));
        artwork.setViewCount(artwork.getViewCount() + 1);
        artworkRepository.save(artwork);
    }

    @Override
    public void incrementArtworkLike(Long id) {
        GalleryArtwork artwork = artworkRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Artwork not found: " + id));
        artwork.setLikeCount(artwork.getLikeCount() + 1);
        artworkRepository.save(artwork);
    }

    private GalleryCollectionDTO convertCollectionToDTO(GalleryCollection collection) {
        GalleryCollectionDTO dto = new GalleryCollectionDTO();
        dto.setId(collection.getId());
        dto.setName(collection.getName());
        dto.setSlug(collection.getSlug());
        dto.setDescription(collection.getDescription());
        dto.setCoverImage(collection.getCoverImage());
        dto.setCategory(collection.getCategory());
        dto.setSortOrder(collection.getSortOrder());
        dto.setIsActive(collection.getIsActive());

        List<GalleryArtwork> artworks = artworkRepository
            .findByCollectionIdAndIsActiveTrue(collection.getId());
        dto.setArtworkCount(artworks.size());

        return dto;
    }

    private GalleryArtworkDTO convertArtworkToDTO(GalleryArtwork artwork) {
        GalleryArtworkDTO dto = new GalleryArtworkDTO();
        dto.setId(artwork.getId());
        dto.setCollectionId(artwork.getCollection().getId());
        dto.setCollectionName(artwork.getCollection().getName());
        dto.setTitle(artwork.getTitle());
        dto.setDescription(artwork.getDescription());
        dto.setImageUrl(artwork.getImageUrl());
        dto.setThumbnailUrl(artwork.getThumbnailUrl());
        dto.setArtist(artwork.getArtist());
        dto.setYear(artwork.getYear());
        dto.setMedium(artwork.getMedium());
        dto.setDimensions(artwork.getDimensions());
        dto.setViewCount(artwork.getViewCount());
        dto.setLikeCount(artwork.getLikeCount());
        dto.setSortOrder(artwork.getSortOrder());
        dto.setIsActive(artwork.getIsActive());
        dto.setCreatedAt(artwork.getCreatedAt());

        if (artwork.getTags() != null) {
            try {
                List<String> tags = objectMapper.readValue(artwork.getTags(), new TypeReference<List<String>>() {});
                dto.setTags(tags);
            } catch (Exception e) {
                dto.setTags(new ArrayList<>());
            }
        }

        return dto;
    }
}
