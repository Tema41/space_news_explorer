package com.test.space_news_explorer.services;

import com.test.space_news_explorer.model.Article;
import com.test.space_news_explorer.config.AppProperties;
import com.test.space_news_explorer.repositories.ArticleRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ArticleServiceImpl implements ArticleService {

    private final RestTemplate restTemplate;
    private final int articlesLimit;
    private final int threadsCount;
    private final int bufferLimit;
    private final List<String> blackListWords;
    private final ArticleRepository articleRepository;
    private final List<Article> articleBuffer = new ArrayList<>();

    private static final int DEFAULT_ARTICLES_LIMIT = 10;
    private static final int DEFAULT_THREADS_COUNT = 5;

    public ArticleServiceImpl(RestTemplate restTemplate, AppProperties appProperties, ArticleRepository articleRepository) {
        this.restTemplate = restTemplate;
        this.articlesLimit = appProperties.getArticlesLimit() != 0 ? appProperties.getArticlesLimit() : DEFAULT_ARTICLES_LIMIT;
        this.threadsCount = appProperties.getThreadCount() != 0 ? appProperties.getThreadCount() : DEFAULT_THREADS_COUNT;
        this.blackListWords = appProperties.getBlacklistWords();
        this.bufferLimit = appProperties.getBufferLimit();
        this.articleRepository = articleRepository;
    }

    @Override
    public List<Article> downloadArticles() {

        List<Article> downloadedArticles = new ArrayList<>();

        int articlesPerThread = articlesLimit / threadsCount;

        ExecutorService executorService = Executors.newFixedThreadPool(threadsCount);

        try {
            List<Future<List<Article>>> futures = new ArrayList<>();
            for (int i = 0; i < threadsCount; i++) {
                int start = i * articlesPerThread;
                futures.add(executorService.submit(() -> downloadArticlesInRange(start, articlesPerThread)));
            }

            for (Future<List<Article>> future : futures) {
                downloadedArticles.addAll(future.get());
            }
        } catch (InterruptedException | ExecutionException exception) {
            exception.printStackTrace();
        } finally {
            executorService.shutdown();
        }

        downloadedArticles = downloadedArticles.stream()
                .filter(article -> !containsBlacklistedWord(article.getTitle()))
                .collect(Collectors.toList());

        downloadedArticles.sort(Comparator.comparing(Article::getPublishedAt));

        Map<String, List<Article>> articleByNewSite = downloadedArticles.stream()
                .collect(Collectors.groupingBy(Article::getNewsSite));

        List<Article> finalArticle = new ArrayList<>();
        for (List<Article> siteArticles : articleByNewSite.values()) {
            int siteBufferLimit = Math.min(bufferLimit, siteArticles.size());
            finalArticle.addAll(siteArticles.subList(0, siteBufferLimit));
        }

        articleBuffer.addAll(finalArticle);

        if (articleBuffer.size() >= bufferLimit) {
            saveArticles(articleBuffer);
            articleBuffer.clear();
        }

        return finalArticle;
    }


    private List<Article> downloadArticlesInRange(int start, int limit) {

        String appUrl = "https://api.spaceflightnewsapi.net/v3/articles";
        ResponseEntity<Article[]> responseEntity = restTemplate.getForEntity(
                appUrl + "?_start=" + start + "&_limit=" + limit, Article[].class
        );
        return Arrays.asList(Objects.requireNonNull(responseEntity.getBody()));
    }

    private boolean containsBlacklistedWord(String title) {
        return blackListWords.stream().anyMatch(title::contains);
    }

    @Override
    public void saveArticles(List<Article> articles) {
        if (articles != null && !articles.isEmpty()) {
            articleRepository.saveAll(articles);
        } else {
            log.info("Нет статей для сохранения.");
        }
    }

    @Override
    public List<Article> getAllArticles() {
        return (List<Article>) articleRepository.findAll();
    }

    @Override
    public Article getArticleById(Long id) {
        Optional<Article> optionalArticle = articleRepository.findById(id);
        return optionalArticle.orElse(null);
    }

    @Override
    public List<Article> getArticlesByNewsSite(String newSite) {
        return articleRepository.findByNewsSite(newSite);
    }
}
