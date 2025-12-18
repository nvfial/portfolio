package com.example.portfolio.service.impl;

import com.example.portfolio.model.dto.ContactDTO;
import com.example.portfolio.model.entity.Contact;
import com.example.portfolio.mapper.ContactMapper;
import com.example.portfolio.repository.ContactRepository;
import com.example.portfolio.service.ContactService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ContactServiceImpl implements ContactService {

    @Autowired
    private ContactRepository repository;

    @Autowired
    private ContactMapper mapper;

    @Override
    @Cacheable("contacts")
    public List<ContactDTO> getAll() {
        return repository.findAll().stream()
                .map(mapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Cacheable(value = "contacts", key = "#id")
    public ContactDTO getById(Long id) {
        Contact contact = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Contact not found with id: " + id));
        return mapper.toDTO(contact);
    }

    @Override
    @Transactional
    @CacheEvict(value = "contacts", allEntries = true)
    public ContactDTO save(ContactDTO dto) {
        Contact contact = mapper.toEntity(dto);
        Contact saved = repository.save(contact);
        return mapper.toDTO(saved);
    }

    @Override
    @Transactional
    @CacheEvict(value = "contacts", allEntries = true)
    public ContactDTO update(Long id, ContactDTO dto) {
        Contact contact = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Contact not found with id: " + id));
        
        contact.setName(dto.getName());
        contact.setEmail(dto.getEmail());
        contact.setSubject(dto.getSubject());
        contact.setMessage(dto.getMessage());
        
        Contact updated = repository.save(contact);
        return mapper.toDTO(updated);
    }

    @Override
    @Transactional
    @CacheEvict(value = "contacts", allEntries = true)
    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new RuntimeException("Contact not found with id: " + id);
        }
        repository.deleteById(id);
    }
}

