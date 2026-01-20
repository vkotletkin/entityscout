package ru.kotletkin.entityscout.common.config;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LuceneConfiguration {

    @Bean
    public StandardAnalyzer standardAnalyzer() {
        return new StandardAnalyzer();
    }
}
