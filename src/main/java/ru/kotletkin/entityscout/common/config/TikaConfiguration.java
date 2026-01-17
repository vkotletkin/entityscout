package ru.kotletkin.entityscout.common.config;

import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.RecursiveParserWrapper;
import org.apache.tika.parser.mail.RFC822Parser;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TikaConfiguration {

    @Bean("recursiveAutoDetect")
    public RecursiveParserWrapper autoDetectParserRecursive() {
        return new RecursiveParserWrapper(new AutoDetectParser());
    }

    @Bean("recursive822Parser")
    public RecursiveParserWrapper rfc822ParserRecursive() {
        return new RecursiveParserWrapper(new RFC822Parser());
    }
}
