package com.tmyx.backend.controller;


import com.tmyx.backend.entity.Article;
import com.tmyx.backend.mapper.ArticleMapper;
import com.tmyx.backend.util.FileUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/article")
@CrossOrigin
@Transactional
public class ArticleController {

    @Autowired
    private ArticleMapper articleMapper;

    @Value("${file.resource-path}")
    private String resourcePath;

    // 获取所有文章
    @GetMapping("/all")
    public List<Article> getAllArticles() {
        return articleMapper.findAll();
    }

    // 获取当前文章
    @GetMapping("/{id}")
    public Article getArticle(@PathVariable Integer id) { return articleMapper.findById(id); }

    // 上传文章
    @PostMapping("/upload")
    public String uploadMarkdown(@RequestParam("file") MultipartFile file) throws IOException {
        // 保存文件
        File uploadDir = new File(resourcePath, "articles/");
        if (!uploadDir.exists()) {
            uploadDir.mkdirs();
        }

        String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
        File dest = new File(uploadDir, fileName);
        file.transferTo(dest);

        Article article = new Article();
        article.setTitle(file.getOriginalFilename());
        article.setContentUrl("/articles/" + fileName);
        articleMapper.insert(article);

        return "/articles/" + fileName;
    }

    // 删除文章
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteArticle(@PathVariable Integer id) {
        Article article = articleMapper.findById(id);

        if (article == null) {
            return ResponseEntity.status(404).body("文章不存在");
        }

        FileUtil.checkAndDeleteFile(resourcePath, article.getContentUrl());
        articleMapper.delete(id);

        return ResponseEntity.ok("删除成功");
    }

//    1
}
