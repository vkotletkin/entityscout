package ru.kotletkin.entityscout.config;


import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Properties;

@Configuration
public class CoreNLPConfiguration {
//
//    @Bean
//    public StanfordCoreNLP coreNLP() {
//        Properties properties = new Properties();
//        properties.setProperty("annotators", "tokenize,ssplit,pos,lemma,ner");
//        properties.setProperty("ner.applyFineGrained", "false");
//        properties.setProperty("ner.applyNumericClassifiers", "false");
//        properties.setProperty("ner.useSUTime", "false");
//        return new StanfordCoreNLP(properties);
//    }
}
