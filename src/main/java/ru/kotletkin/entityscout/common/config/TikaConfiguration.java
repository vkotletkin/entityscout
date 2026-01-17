package ru.kotletkin.entityscout.common.config;

import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.RecursiveParserWrapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TikaConfiguration {

    @Bean("recursiveAutoDetect")
    public RecursiveParserWrapper autoDetectParserRecursive() {
        return new RecursiveParserWrapper(new AutoDetectParser());
    }
}
