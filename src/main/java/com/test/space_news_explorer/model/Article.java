package com.test.space_news_explorer.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter @Setter
@Table(name = "ARTICLES")
@NoArgsConstructor
@AllArgsConstructor
public class Article {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "title")
    private String title;

    @Column(name = "news_site")
    private String newsSite;

    @Column(name = "published_date")
    private LocalDateTime publishedAt;

    private String url;

    private String summary;

    @Column(name = "article")
    private String article;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Article article = (Article) o;
        return id.equals(article.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

}
