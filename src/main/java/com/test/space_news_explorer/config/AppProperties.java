package com.test.space_news_explorer.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Getter
public class AppProperties {

    @Value("${app.thread.count}")
    private int threadCount;

    @Value("${app.articles.limit}")
    private int articlesLimit;

    @Value("${app.blacklist.words}")
    private List<String> blacklistWords;

    @Value("${app.buffer.limit}")
    private int bufferLimit;
}
