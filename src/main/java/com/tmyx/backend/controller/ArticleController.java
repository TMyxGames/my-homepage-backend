package com.tmyx.backend.controller;


import com.tmyx.backend.common.Result;
import com.tmyx.backend.entity.Article;
import com.tmyx.backend.dto.ArticleUpdateDTO;
import com.tmyx.backend.mapper.ArticleMapper;
import com.tmyx.backend.util.FileUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
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
    public Result getAllArticles(@RequestAttribute Integer userId) {
        List<Article> articles = articleMapper.findAll();
        return Result.success(articles);
    }

    // 获取所有已发布文章
    @GetMapping("/published")
    public Result getPublished() {
        return Result.success(articleMapper.findPublished());
    }

    // 获取所有草稿
    @GetMapping("/drafts")
    public Result getDrafts() {
        return Result.success(articleMapper.findDrafts());
    }

    // 创建新文章预处理
    @GetMapping("/preGenerateId")
    public Result preGenerateId() {
        // 生成文章id
        String articleId = UUID.randomUUID().toString().replace("-", "");
        // 获取绝对路径
        File baseDir = new File(resourcePath).getAbsoluteFile();
        String subPath = "/article/" + articleId + "/images/";
        File folder = new File(resourcePath + subPath);
        // 如果目录不存在则创建
        if (!folder.exists()) {
            boolean success = folder.mkdirs();
            if (!success) {
                return Result.error("文件夹初始化失败");
            }
        }
        return Result.success(articleId);
    }

    // 上传图片
    @PostMapping("/uploadImage")
    public Result uploadImage(@RequestAttribute Integer userId,
                              @RequestParam("file") MultipartFile file,
                              @RequestParam("articleId") String articleId) {
        if (file.isEmpty()) return Result.error("文件不能为空");
        // 获取绝对路径
        File baseDir = new File(resourcePath).getAbsoluteFile();

        try {
            // 确定文章的存储路径
            String subPath = "/article/" + articleId + "/images/";
            File folder = new File(baseDir + subPath);
            // 如果目录不存在则创建
            if (!folder.exists()) {
                folder.mkdirs();
            }

            String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
            File dest = new File(folder, fileName);
            file.transferTo(dest.getAbsoluteFile());

            // 拼接路径
            String fileUrl = "/files" + subPath + fileName;
            return Result.success(fileUrl);
        } catch (IOException e) {
            e.printStackTrace();
            return Result.error("图片上传失败");
        }
    }

    // 保存文章
    @PostMapping("/save")
    public Result saveArticle(@RequestAttribute Integer userId, @RequestBody Article article) {
        // 获取绝对路径
        File baseDir = new File(resourcePath).getAbsoluteFile();
        String absolutePath = baseDir.getAbsolutePath();
        // 确定文件存储相对路径以及文件名
        // article/{articleId}/index.md
        String subPath = "/article/" + article.getId();
        File folder = new File(baseDir + subPath);
        // 如果目录不存在则创建
        if (!folder.exists()) folder.mkdirs();
        // 清除无用图片
        if (article.getContent() != null) {
            FileUtil.cleanOrphanImages(article.getContent(), article.getId(), absolutePath);
        }
        // 将前端传来的content写入markdown文件中
        if (article.getContent() != null) {
            try {
                File mdFile = new File(folder, "index.md");
                Files.writeString(mdFile.toPath(), article.getContent(), StandardCharsets.UTF_8);
            } catch (IOException e) {
                e.printStackTrace();
                return Result.error("内容文件保存失败");
            }
        } else {
            System.out.println("跳过文件写入");
        }
        // 组装文章信息
        article.setId(article.getId());
        article.setContentUrl("/files" + subPath + "/index.md");
        article.setUploadTime(LocalDateTime.now());
        // 检查文章是否存在
        Article existArticle = articleMapper.findById(article.getId());
        if (existArticle == null) {
            articleMapper.insert(article);
        } else {
            articleMapper.update(article);
        }
        return Result.success();
    }

    // 编辑文章
    @GetMapping("/get/{id}")
    public Result getById(@PathVariable String id) {
        Article article = articleMapper.findById(id);
        if (article == null) return Result.error("文章不存在");

        String contentUrl = article.getContentUrl();
        // 获取绝对路径
        File baseDir = new File(resourcePath).getAbsoluteFile();
        String absolutePath = baseDir.getAbsolutePath();

        if (contentUrl != null && !contentUrl.isEmpty()) {
            String relativePath = contentUrl.replace("/files/", "");
            Path fullPath = Paths.get(absolutePath, relativePath);
            File mdFile = fullPath.toFile();

            if (mdFile.exists()) {
                try {
                    String content = Files.readString(mdFile.toPath());
                    article.setContent(content);
                } catch (IOException e) {
                    return Result.error("读取内容文件失败");
                }
            }

        }

        return Result.success(article);
    }

    // 修改文章标题
    @PutMapping("/updateTitle")
    public Result updateTitle(@RequestAttribute Integer userId, @RequestBody ArticleUpdateDTO updateDTO) {
        int rows = articleMapper.updateTitle(updateDTO.getId(), updateDTO.getTitle());

        if (rows == 0) {
            return Result.error("文章更新失败");
        }

        return Result.success();
    }

    // 删除文章（删除整个目录）
    @DeleteMapping("/delete/{id}")
    public Result deleteArticle(@RequestAttribute Integer userId, @PathVariable String id) {
        // 根据文章id获取文章信息
        Article article = articleMapper.findById(id);
        if (article == null) return Result.error("文章不存在");

        Path bassRoot = Paths.get(resourcePath).toAbsolutePath().normalize();
        // 获取文件路径
        String fileUrl = article.getContentUrl();
        // 去除映射路径的/files前缀，再去掉markdown文件的文件名，得到相对路径
        String relativeDirPath = fileUrl.replace("/files/", "").replace("index.md", "");
        // 获取绝对路径
        Path fullPath = bassRoot.resolve(relativeDirPath).normalize();
        // 拼接物理路径并删除
        File articleFolder = fullPath.toFile();
        if (articleFolder.exists() && articleFolder.isDirectory()) {
            FileUtil.deleteDirectory(articleFolder);
        } else {
            System.out.println("目录不存在");
        }
        // 删除数据库记录
        articleMapper.deleteById(id);
        return Result.success();
    }
}
