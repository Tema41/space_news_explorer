package com.test.space_news_explorer.repositories;

import com.test.space_news_explorer.model.Article;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ArticleRepository extends CrudRepository<Article, Long> {
    List<Article> findByNewsSite(String newsSite);
}
