package com.example.portfolio.mapper;

import com.example.portfolio.model.dto.TestimonialDTO;
import com.example.portfolio.model.entity.Testimonial;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TestimonialMapper {

    TestimonialDTO toDTO(Testimonial testimonial);

    Testimonial toEntity(TestimonialDTO dto);
}

