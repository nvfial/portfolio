package com.example.portfolio.controller;

import com.example.portfolio.model.dto.GalleryCollectionDTO;
import com.example.portfolio.model.dto.GalleryArtworkDTO;
import com.example.portfolio.model.entity.GalleryCollection;
import com.example.portfolio.model.entity.GalleryArtwork;
import com.example.portfolio.service.GalleryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/gallery")
@Tag(name = "Gallery", description = "艺术画廊管理API")
public class GalleryController {

    @Autowired
    private GalleryService galleryService;

    @GetMapping("/collections")
    @Operation(summary = "获取所有艺术收藏")
    public ResponseEntity<List<GalleryCollectionDTO>> getAllCollections() {
        return ResponseEntity.ok(galleryService.getAllCollections());
    }

    @GetMapping("/collections/category/{category}")
    @Operation(summary = "按分类获取艺术收藏")
    public ResponseEntity<List<GalleryCollectionDTO>> getCollectionsByCategory(@PathVariable String category) {
        return ResponseEntity.ok(galleryService.getCollectionsByCategory(category));
    }

    @GetMapping("/collections/{slug}")
    @Operation(summary = "通过slug获取艺术收藏详情")
    public ResponseEntity<GalleryCollectionDTO> getCollectionBySlug(@PathVariable String slug) {
        return ResponseEntity.ok(galleryService.getCollectionBySlug(slug));
    }

    @PostMapping("/collections")
    @Operation(summary = "创建艺术收藏")
    public ResponseEntity<GalleryCollectionDTO> createCollection(@RequestBody GalleryCollection collection) {
        return ResponseEntity.ok(galleryService.createCollection(collection));
    }

    @PutMapping("/collections/{id}")
    @Operation(summary = "更新艺术收藏")
    public ResponseEntity<GalleryCollectionDTO> updateCollection(
            @PathVariable Long id, 
            @RequestBody GalleryCollection collection) {
        return ResponseEntity.ok(galleryService.updateCollection(id, collection));
    }

    @DeleteMapping("/collections/{id}")
    @Operation(summary = "删除艺术收藏")
    public ResponseEntity<Void> deleteCollection(@PathVariable Long id) {
        galleryService.deleteCollection(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/collections/{collectionId}/artworks")
    @Operation(summary = "获取收藏的艺术作品")
    public ResponseEntity<List<GalleryArtworkDTO>> getArtworksByCollection(@PathVariable Long collectionId) {
        return ResponseEntity.ok(galleryService.getArtworksByCollection(collectionId));
    }

    @GetMapping("/artworks/{id}")
    @Operation(summary = "获取艺术作品详情")
    public ResponseEntity<GalleryArtworkDTO> getArtworkById(@PathVariable Long id) {
        return ResponseEntity.ok(galleryService.getArtworkById(id));
    }

    @PostMapping("/artworks")
    @Operation(summary = "创建艺术作品")
    public ResponseEntity<GalleryArtworkDTO> createArtwork(@RequestBody GalleryArtwork artwork) {
        return ResponseEntity.ok(galleryService.createArtwork(artwork));
    }

    @PutMapping("/artworks/{id}")
    @Operation(summary = "更新艺术作品")
    public ResponseEntity<GalleryArtworkDTO> updateArtwork(
            @PathVariable Long id, 
            @RequestBody GalleryArtwork artwork) {
        return ResponseEntity.ok(galleryService.updateArtwork(id, artwork));
    }

    @DeleteMapping("/artworks/{id}")
    @Operation(summary = "删除艺术作品")
    public ResponseEntity<Void> deleteArtwork(@PathVariable Long id) {
        galleryService.deleteArtwork(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/artworks/{id}/view")
    @Operation(summary = "增加作品浏览量")
    public ResponseEntity<Void> incrementView(@PathVariable Long id) {
        galleryService.incrementArtworkView(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/artworks/{id}/like")
    @Operation(summary = "增加作品点赞数")
    public ResponseEntity<Void> incrementLike(@PathVariable Long id) {
        galleryService.incrementArtworkLike(id);
        return ResponseEntity.ok().build();
    }
}
