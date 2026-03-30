package com.example.portfolio.service;

import com.example.portfolio.model.dto.GalleryCollectionDTO;
import com.example.portfolio.model.dto.GalleryArtworkDTO;
import com.example.portfolio.model.entity.GalleryCollection;
import com.example.portfolio.model.entity.GalleryArtwork;

import java.util.List;

public interface GalleryService {
    
    List<GalleryCollectionDTO> getAllCollections();
    
    List<GalleryCollectionDTO> getCollectionsByCategory(String category);
    
    GalleryCollectionDTO getCollectionBySlug(String slug);
    
    GalleryCollectionDTO createCollection(GalleryCollection collection);
    
    GalleryCollectionDTO updateCollection(Long id, GalleryCollection collection);
    
    void deleteCollection(Long id);
    
    List<GalleryArtworkDTO> getArtworksByCollection(Long collectionId);
    
    GalleryArtworkDTO getArtworkById(Long id);
    
    GalleryArtworkDTO createArtwork(GalleryArtwork artwork);
    
    GalleryArtworkDTO updateArtwork(Long id, GalleryArtwork artwork);
    
    void deleteArtwork(Long id);
    
    void incrementArtworkView(Long id);
    
    void incrementArtworkLike(Long id);
}
