package ru.kotletkin.entityscout.search;

import lombok.RequiredArgsConstructor;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.memory.MemoryIndex;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.Query;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SearchService {

    private static final String CONTENT_FIELD_NAME = "content";

    private final StandardAnalyzer standardAnalyzer;

    public boolean test(String text) {

        QueryParser queryParser = new QueryParser(CONTENT_FIELD_NAME, standardAnalyzer);
        try {
            Query query = queryParser.parse(text);
            MemoryIndex memoryIndex = new MemoryIndex();
            memoryIndex.addField(CONTENT_FIELD_NAME, text, standardAnalyzer);
            float score = memoryIndex.search(query);
            return score > 0.0f ? true : false;
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }
}
