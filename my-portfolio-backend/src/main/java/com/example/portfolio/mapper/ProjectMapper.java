package com.example.portfolio.mapper;

import com.example.portfolio.model.dto.ProjectDTO;
import com.example.portfolio.model.entity.Project;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ProjectMapper {

    ProjectDTO toDTO(Project project);

    Project toEntity(ProjectDTO dto);
}


