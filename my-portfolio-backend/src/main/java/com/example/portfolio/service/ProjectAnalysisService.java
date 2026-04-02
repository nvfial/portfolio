package com.example.portfolio.service;

import com.example.portfolio.model.dto.ProjectDTO;

public interface ProjectAnalysisService {

    ProjectDTO analyzeFromGitHub(String githubUrl);

    ProjectDTO analyzeFromUpload(byte[] zipContent, String fileName);

    ProjectDTO reAnalyze(Long projectId);
}