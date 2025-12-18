package com.example.portfolio.service.impl;

import com.example.portfolio.model.dto.TestimonialDTO;
import com.example.portfolio.model.entity.Testimonial;
import com.example.portfolio.mapper.TestimonialMapper;
import com.example.portfolio.repository.TestimonialRepository;
import com.example.portfolio.service.TestimonialService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TestimonialServiceImpl implements TestimonialService {

    @Autowired
    private TestimonialRepository repository;

    @Autowired
    private TestimonialMapper mapper;

    @Override
    @Cacheable("testimonials")
    public List<TestimonialDTO> getAll() {
        return repository.findAll().stream()
                .map(mapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Cacheable(value = "testimonials", key = "#id")
    public TestimonialDTO getById(Long id) {
        Testimonial testimonial = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Testimonial not found with id: " + id));
        return mapper.toDTO(testimonial);
    }

    @Override
    @Transactional
    @CacheEvict(value = "testimonials", allEntries = true)
    public TestimonialDTO save(TestimonialDTO dto) {
        Testimonial testimonial = mapper.toEntity(dto);
        Testimonial saved = repository.save(testimonial);
        return mapper.toDTO(saved);
    }

    @Override
    @Transactional
    @CacheEvict(value = "testimonials", allEntries = true)
    public TestimonialDTO update(Long id, TestimonialDTO dto) {
        Testimonial testimonial = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Testimonial not found with id: " + id));
        
        testimonial.setAuthor(dto.getAuthor());
        testimonial.setContent(dto.getContent());
        
        Testimonial updated = repository.save(testimonial);
        return mapper.toDTO(updated);
    }

    @Override
    @Transactional
    @CacheEvict(value = "testimonials", allEntries = true)
    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new RuntimeException("Testimonial not found with id: " + id);
        }
        repository.deleteById(id);
    }
}

