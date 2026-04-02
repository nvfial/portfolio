package com.example.portfolio.service.impl;

import com.example.portfolio.model.dto.ProjectDTO;
import com.example.portfolio.model.entity.Project;
import com.example.portfolio.model.entity.ProjectImage;
import com.example.portfolio.repository.ProjectRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveInputStream;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;
import java.util.zip.GZIPInputStream;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProjectAnalysisServiceImpl implements com.example.portfolio.service.ProjectAnalysisService {

    private final ProjectRepository projectRepository;
    private final ObjectMapper objectMapper;
    private final WebClient.Builder webClientBuilder;

    private static final String GITHUB_API_BASE = "https://api.github.com";
    private static final String GITHUB_RAW_BASE = "https://raw.githubusercontent.com";

    @Override
    public ProjectDTO analyzeFromGitHub(String githubUrl) {
        log.info("开始分析GitHub仓库: {}", githubUrl);

        String ownerRepo = parseGitHubOwnerRepo(githubUrl);
        if (ownerRepo == null) {
            throw new IllegalArgumentException("无效的GitHub仓库URL");
        }

        String[] parts = ownerRepo.split("/");
        String owner = parts[0];
        String repo = parts[1];

        Map<String, Object> repoInfo = fetchGitHubRepoInfo(owner, repo);
        if (repoInfo == null) {
            throw new RuntimeException("无法获取GitHub仓库信息");
        }

        Project project = new Project();
        project.setTitle((String) repoInfo.getOrDefault("name", ""));
        project.setDescription((String) repoInfo.getOrDefault("description", ""));
        project.setGithub(githubUrl);
        project.setGithubRepo(ownerRepo);
        project.setGithubRepoUrl("https://github.com/" + ownerRepo);
        project.setStars((Integer) repoInfo.get("stargazers_count"));
        project.setForks((Integer) repoInfo.get("forks_count"));
        project.setWatchers((Integer) repoInfo.get("watchers_count"));
        project.setLanguage((String) repoInfo.get("language"));
        project.setLicense((String) ((Map<?, ?>) repoInfo.get("license"))?.get("name"));
        project.setOpenIssues((Integer) repoInfo.get("open_issues_count"));
        project.setAnalysisStatus("SUCCESS");
        project.setAnalyzedAt(LocalDateTime.now());

        Map<String, Object> languageStats = fetchGitHubLanguages(owner, repo);
        if (languageStats != null) {
            try {
                project.setLanguages(objectMapper.writeValueAsString(languageStats));
                if (!languageStats.isEmpty()) {
                    project.setPrimaryLanguage(
                        languageStats.entrySet().stream()
                            .max(Map.Entry.comparingByValue())
                            .map(Map.Entry::getKey)
                            .orElse(null)
                    );
                }
            } catch (Exception e) {
                log.warn("解析语言统计失败", e);
            }
        }

        String readmeContent = fetchReadmeContent(owner, repo);
        if (readmeContent != null) {
            project.setReadmeContent(readmeContent);
            if (project.getDescription() == null || project.getDescription().isEmpty()) {
                project.setDescription(extractDescriptionFromReadme(readmeContent));
            }
        }

        List<String> topics = (List<String>) repoInfo.get("topics");
        if (topics != null && !topics.isEmpty()) {
            project.setTags(topics);
            try {
                project.setTopics(objectMapper.writeValueAsString(topics));
            } catch (Exception e) {
                log.warn("序列化topics失败", e);
            }
        }

        String pushedAt = (String) repoInfo.get("pushed_at");
        if (pushedAt != null) {
            project.setLastCommit(LocalDateTime.parse(pushedAt.replace("Z", "")));
        }

        project.setImageUrl((String) ((Map<?, ?>) repoInfo.get("owner"))?.get("avatar_url"));

        Project saved = projectRepository.save(project);
        log.info("项目分析完成，ID: {}", saved.getId());

        return convertToDTO(saved);
    }

    @Override
    public ProjectDTO analyzeFromUpload(byte[] zipContent, String fileName) {
        log.info("开始分析上传的项目文件: {}", fileName);

        Path tempDir = null;
        try {
            tempDir = Files.createTempDirectory("project-analysis-");

            extractZip(zipContent, tempDir);

            Project project = analyzeExtractedProject(tempDir, fileName);
            project.setAnalysisStatus("SUCCESS");
            project.setAnalyzedAt(LocalDateTime.now());

            Project saved = projectRepository.save(project);
            log.info("项目分析完成，ID: {}", saved.getId());

            return convertToDTO(saved);

        } catch (Exception e) {
            log.error("项目分析失败", e);
            throw new RuntimeException("项目分析失败: " + e.getMessage(), e);
        } finally {
            if (tempDir != null) {
                deleteDirectory(tempDir);
            }
        }
    }

    @Override
    public ProjectDTO reAnalyze(Long projectId) {
        Project project = projectRepository.findById(projectId)
            .orElseThrow(() -> new IllegalArgumentException("项目不存在: " + projectId));

        if (project.getGithubRepo() != null && !project.getGithubRepo().isEmpty()) {
            return analyzeFromGitHub("https://github.com/" + project.getGithubRepo());
        }

        throw new IllegalArgumentException("项目没有关联GitHub仓库，无法重新分析");
    }

    private String parseGitHubOwnerRepo(String url) {
        url = url.trim();
        if (url.startsWith("https://github.com/")) {
            String path = url.replace("https://github.com/", "");
            String[] parts = path.split("/");
            if (parts.length >= 2) {
                return parts[0] + "/" + parts[1].replace(".git", "");
            }
        } else if (url.startsWith("git@github.com:")) {
            String path = url.replace("git@github.com:", "");
            String[] parts = path.split("/");
            if (parts.length >= 2) {
                return parts[0] + "/" + parts[1].replace(".git", "");
            }
        } else if (url.contains("github.com") && url.contains("/")) {
            String[] parts = url.split("github.com")[1].split("/");
            if (parts.length >= 3) {
                return parts[1] + "/" + parts[2].replace(".git", "");
            }
        }
        return null;
    }

    private Map<String, Object> fetchGitHubRepoInfo(String owner, String repo) {
        try {
            WebClient webClient = webClientBuilder.baseUrl(GITHUB_API_BASE).build();
            String response = webClient.get()
                .uri("/repos/{owner}/{repo}", owner, repo)
                .header("Accept", "application/vnd.github.v3+json")
                .retrieve()
                .bodyToMono(String.class)
                .block();

            return objectMapper.readValue(response, Map.class);
        } catch (Exception e) {
            log.error("获取GitHub仓库信息失败: {}/{}", owner, repo, e);
            return null;
        }
    }

    private Map<String, Object> fetchGitHubLanguages(String owner, String repo) {
        try {
            WebClient webClient = webClientBuilder.baseUrl(GITHUB_API_BASE).build();
            String response = webClient.get()
                .uri("/repos/{owner}/{repo}/languages", owner, repo)
                .header("Accept", "application/vnd.github.v3+json")
                .retrieve()
                .bodyToMono(String.class)
                .block();

            return objectMapper.readValue(response, Map.class);
        } catch (Exception e) {
            log.warn("获取语言统计失败", e);
            return null;
        }
    }

    private String fetchReadmeContent(String owner, String repo) {
        String[] readmeNames = {"README.md", "README.MD", "Readme.md", "readme.md", "README.rst", "README.txt"};
        for (String name : readmeNames) {
            try {
                WebClient webClient = webClientBuilder.baseUrl(GITHUB_RAW_BASE).build();
                String content = webClient.get()
                    .uri("/{owner}/{repo}/main/{name}", owner, repo, name)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
                if (content != null) {
                    return content;
                }
            } catch (Exception ignored) {
            }
            try {
                WebClient webClient = webClientBuilder.baseUrl(GITHUB_RAW_BASE).build();
                String content = webClient.get()
                    .uri("/{owner}/{repo}/master/{name}", owner, repo, name)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
                if (content != null) {
                    return content;
                }
            } catch (Exception ignored) {
            }
        }
        return null;
    }

    private String extractDescriptionFromReadme(String readme) {
        if (readme == null) return null;
        String[] lines = readme.split("\n");
        for (String line : lines) {
            line = line.trim();
            if (line.startsWith("# ")) {
                continue;
            }
            if (!line.isEmpty() && !line.startsWith("!") && !line.startsWith("[")) {
                return line.length() > 500 ? line.substring(0, 500) : line;
            }
        }
        return null;
    }

    private void extractZip(byte[] zipContent, Path destDir) throws IOException {
        try (ByteArrayInputStream bais = new ByteArrayInputStream(zipContent);
             ZipArchiveInputStream zais = new ZipArchiveInputStream(bais)) {
            ZipArchiveEntry entry;
            while ((entry = zais.getNextZipEntry()) != null) {
                Path outPath = destDir.resolve(entry.getName());
                if (entry.isDirectory()) {
                    Files.createDirectories(outPath);
                } else {
                    Files.createDirectories(outPath.getParent());
                    Files.copy(zais, outPath);
                }
            }
        }
    }

    private Project analyzeExtractedProject(Path projectDir, String fileName) throws IOException {
        Project project = new Project();
        project.setTitle(extractProjectName(fileName));

        Map<String, Object> projectInfo = new HashMap<>();
        List<String> detectedTech = new ArrayList<>();

        Path packageJson = projectDir.resolve("package.json");
        if (Files.exists(packageJson)) {
            try {
                String content = Files.readString(packageJson);
                Map<String, Object> pkg = objectMapper.readValue(content, Map.class);
                project.setTitle((String) pkg.getOrDefault("name", project.getTitle()));
                project.setDescription((String) pkg.getOrDefault("description", ""));
                project.setPrimaryLanguage("JavaScript/TypeScript");
                detectedTech.add("Node.js");
                if (pkg.get("dependencies") != null) {
                    Map<String, Object> deps = (Map<String, Object>) pkg.get("dependencies");
                    detectedTech.addAll(detectTechFromDeps(deps));
                }
                projectInfo.put("version", pkg.get("version"));
                projectInfo.put("main", pkg.get("main"));
            } catch (Exception e) {
                log.warn("解析package.json失败", e);
            }
        }

        Path pomXml = projectDir.resolve("pom.xml");
        if (Files.exists(pomXml)) {
            project.setPrimaryLanguage("Java");
            detectedTech.add("Java");
            detectedTech.add("Maven");
        }

        Path requirementsTxt = projectDir.resolve("requirements.txt");
        if (Files.exists(requirementsTxt)) {
            project.setPrimaryLanguage("Python");
            detectedTech.add("Python");
            List<String> pyDeps = Files.readAllLines(requirementsTxt);
            detectedTech.addAll(detectPythonDeps(pyDeps));
        }

        Path goMod = projectDir.resolve("go.mod");
        if (Files.exists(goMod)) {
            project.setPrimaryLanguage("Go");
            detectedTech.add("Go");
        }

        Path composerJson = projectDir.resolve("composer.json");
        if (Files.exists(composerJson)) {
            project.setPrimaryLanguage("PHP");
            detectedTech.add("PHP");
            detectedTech.add("Composer");
        }

        Path readmeFile = findReadmeFile(projectDir);
        if (readmeFile != null) {
            String readmeContent = Files.readString(readmeFile);
            project.setReadmeContent(readmeContent);
            if (project.getDescription() == null || project.getDescription().isEmpty()) {
                project.setDescription(extractDescriptionFromReadme(readmeContent));
            }
        }

        project.setTags(detectedTech);

        Map<String, Integer> stats = analyzeCodeStructure(projectDir);
        project.setFileCount(stats.get("files"));
        project.setTotalLines(stats.get("lines"));

        try {
            project.setTechStack(objectMapper.writeValueAsString(detectedTech));
        } catch (Exception ignored) {}

        if (project.getDescription() == null || project.getDescription().isEmpty()) {
            project.setDescription("通过本地上传的项目: " + fileName);
        }

        return project;
    }

    private String extractProjectName(String fileName) {
        String name = fileName.replace(".zip", "").replace(".tar.gz", "");
        return name.replaceAll("[-_]", " ");
    }

    private Path findReadmeFile(Path dir) {
        String[] names = {"README.md", "README.MD", "Readme.md", "readme.md", "README.rst", "README.txt"};
        for (String name : names) {
            Path p = dir.resolve(name);
            if (Files.exists(p)) {
                return p;
            }
        }
        return null;
    }

    private List<String> detectTechFromDeps(Map<String, Object> deps) {
        List<String> tech = new ArrayList<>();
        for (String dep : deps.keySet()) {
            String lower = dep.toLowerCase();
            if (lower.contains("react")) tech.add("React");
            if (lower.contains("vue")) tech.add("Vue");
            if (lower.contains("angular")) tech.add("Angular");
            if (lower.contains("express")) tech.add("Express");
            if (lower.contains("nest")) tech.add("NestJS");
            if (lower.contains("next")) tech.add("Next.js");
            if (lower.contains("nuxt")) tech.add("Nuxt.js");
            if (lower.contains("tailwind")) tech.add("Tailwind CSS");
            if (lower.contains("bootstrap")) tech.add("Bootstrap");
            if (lower.contains("mongoose")) tech.add("MongoDB");
            if (lower.contains("pg") && lower.contains("sequelize")) tech.add("PostgreSQL");
            if (lower.contains("redis")) tech.add("Redis");
        }
        return tech;
    }

    private List<String> detectPythonDeps(List<String> lines) {
        List<String> tech = new ArrayList<>();
        for (String line : lines) {
            String dep = line.split("[=<>]")[0].trim().toLowerCase();
            if (dep.contains("django")) tech.add("Django");
            if (dep.contains("flask")) tech.add("Flask");
            if (dep.contains("fastapi")) tech.add("FastAPI");
            if (dep.contains("requests")) tech.add("Requests");
            if (dep.contains("numpy")) tech.add("NumPy");
            if (dep.contains("pandas")) tech.add("Pandas");
        }
        return tech;
    }

    private Map<String, Integer> analyzeCodeStructure(Path projectDir) {
        Map<String, Integer> stats = new HashMap<>();
        AtomicInteger fileCount = new AtomicInteger(0);
        AtomicInteger lineCount = new AtomicInteger(0);

        try (Stream<Path> paths = Files.walk(projectDir)) {
            paths.filter(Files::isRegularFile)
                .filter(p -> !p.toString().contains(".git") && !p.toString().contains("node_modules"))
                .forEach(p -> {
                    fileCount.incrementAndGet();
                    try {
                        List<String> lines = Files.readAllLines(p);
                        lineCount.addAndGet(lines.size());
                    } catch (Exception ignored) {}
                });
        } catch (Exception e) {
            log.warn("分析代码结构失败", e);
        }

        stats.put("files", fileCount.get());
        stats.put("lines", lineCount.get());
        return stats;
    }

    private void deleteDirectory(Path dir) {
        try (Stream<Path> paths = Files.walk(dir)) {
            paths.sorted(Comparator.reverseOrder())
                .forEach(p -> {
                    try {
                        Files.delete(p);
                    } catch (IOException ignored) {}
                });
        } catch (Exception ignored) {}
    }

    private ProjectDTO convertToDTO(Project project) {
        ProjectDTO dto = new ProjectDTO();
        dto.setId(project.getId());
        dto.setTitle(project.getTitle());
        dto.setDescription(project.getDescription());
        dto.setImageUrl(project.getImageUrl());
        dto.setCategory(project.getCategory());
        dto.setTags(project.getTags());
        dto.setLink(project.getLink());
        dto.setGithub(project.getGithub());
        dto.setGithubRepo(project.getGithubRepo());
        dto.setGithubRepoUrl(project.getGithubRepoUrl());
        dto.setStars(project.getStars());
        dto.setForks(project.getForks());
        dto.setWatchers(project.getWatchers());
        dto.setLanguage(project.getLanguage());
        dto.setPrimaryLanguage(project.getPrimaryLanguage());
        dto.setLanguages(project.getLanguages());
        dto.setLicense(project.getLicense());
        dto.setOpenIssues(project.getOpenIssues());
        dto.setLastCommit(project.getLastCommit());
        dto.setDescriptionExtended(project.getDescriptionExtended());
        dto.setTopics(project.getTopics());
        dto.setAnalysisStatus(project.getAnalysisStatus());
        dto.setAnalysisError(project.getAnalysisError());
        dto.setAnalyzedAt(project.getAnalyzedAt());
        dto.setFileCount(project.getFileCount());
        dto.setTotalLines(project.getTotalLines());
        dto.setReadmeContent(project.getReadmeContent());
        dto.setTechStack(project.getTechStack());
        dto.setSortOrder(project.getSortOrder());
        dto.setIsFeatured(project.getIsFeatured());
        dto.setIsPublished(project.getIsPublished());
        dto.setCreatedAt(project.getCreatedAt());
        dto.setUpdatedAt(project.getUpdatedAt());

        if (project.getImages() != null) {
            dto.setImages(project.getImages().stream().map(img -> {
                ProjectDTO.ProjectImageDTO imgDto = new ProjectDTO.ProjectImageDTO();
                imgDto.setId(img.getId());
                imgDto.setUrl(img.getUrl());
                imgDto.setAltText(img.getAltText());
                imgDto.setCaption(img.getCaption());
                imgDto.setSortOrder(img.getSortOrder());
                imgDto.setIsCover(img.getIsCover());
                return imgDto;
            }).toList());
        }

        return dto;
    }
}