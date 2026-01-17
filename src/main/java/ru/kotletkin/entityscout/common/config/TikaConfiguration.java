package ru.kotletkin.entityscout.common.config;

import org.apache.tika.langdetect.optimaize.OptimaizeLangDetector;
import org.apache.tika.language.detect.LanguageDetector;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.RecursiveParserWrapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

@Configuration
public class TikaConfiguration {

    @Bean("recursiveAutoDetect")
    public RecursiveParserWrapper autoDetectParserRecursive() {
        return new RecursiveParserWrapper(new AutoDetectParser());
    }

    @Bean
    public LanguageDetector languageDetector() throws IOException {
        LanguageDetector languageDetector = new OptimaizeLangDetector();
        languageDetector.loadModels();
        return languageDetector;
    }
}
