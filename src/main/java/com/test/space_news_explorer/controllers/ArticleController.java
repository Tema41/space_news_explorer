package com.test.space_news_explorer.controllers;

import com.test.space_news_explorer.model.Article;
import com.test.space_news_explorer.services.ArticleService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class ArticleController {

    private final ArticleService articleService;

    public ArticleController(ArticleService articleService) {
        this.articleService = articleService;
    }

    @PostMapping("/download")
    public ResponseEntity<String> downloadAndSaveArticles() {
        List<Article> downloadedArticle = articleService.downloadArticles();
        articleService.saveArticles(downloadedArticle);
        return ResponseEntity.ok("Статьи успешно загружены");
    }

    @GetMapping("/articles")
    public ResponseEntity<List<Article>> getAllArticles() {
        List<Article> articles = articleService.getAllArticles();
        return ResponseEntity.ok(articles);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Article> getArticleById(@PathVariable Long id) {
        Article article = articleService.getArticleById(id);
        if (article != null) {
            return ResponseEntity.ok(article);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @GetMapping("/site/{newsSite}")
    public ResponseEntity<List<Article>> getArticlesByNewsSite(@PathVariable String newsSite) {
        List<Article> articles = articleService.getArticlesByNewsSite(newsSite);
        return ResponseEntity.ok(articles);
    }
}
