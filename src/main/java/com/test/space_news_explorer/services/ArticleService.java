package com.test.space_news_explorer.services;

import com.test.space_news_explorer.model.Article;

import java.util.List;

public interface ArticleService {
    List<Article> downloadArticles();

    void saveArticles(List<Article> articles);

    List<Article> getAllArticles();

    Article getArticleById(Long id);

    List<Article> getArticlesByNewsSite(String newSite);
}
