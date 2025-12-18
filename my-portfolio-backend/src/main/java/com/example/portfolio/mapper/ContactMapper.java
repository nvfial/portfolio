package com.example.portfolio.mapper;

import com.example.portfolio.model.dto.ContactDTO;
import com.example.portfolio.model.entity.Contact;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ContactMapper {

    ContactDTO toDTO(Contact contact);

    Contact toEntity(ContactDTO dto);
}

