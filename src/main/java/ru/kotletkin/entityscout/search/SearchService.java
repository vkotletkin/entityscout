package ru.kotletkin.entityscout.search;

import lombok.RequiredArgsConstructor;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.memory.MemoryIndex;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.Query;
import org.springframework.stereotype.Service;
import ru.kotletkin.entityscout.search.dto.SearchSingleDTO;
import ru.kotletkin.entityscout.search.dto.SearchSingleRequest;

@Service
@RequiredArgsConstructor
public class SearchService {

    private static final String CONTENT_FIELD_NAME = "content";
    private static final float NOT_FOUND_SCORE_VALUE = 0.0f;

    private final StandardAnalyzer standardAnalyzer;

    public SearchSingleDTO searchBySingleRequest(SearchSingleRequest searchSingleRequest) {
        String text = searchSingleRequest.text();
        String query = searchSingleRequest.query();
        float score = findByTextInMemory(query, text);
        boolean result = score > NOT_FOUND_SCORE_VALUE;
        return new SearchSingleDTO(result, score);
    }

    private float findByTextInMemory(String queryText, String text) {
        QueryParser queryParser = new QueryParser(CONTENT_FIELD_NAME, standardAnalyzer);
        try {
            Query query = queryParser.parse(queryText);
            MemoryIndex memoryIndex = new MemoryIndex();
            memoryIndex.addField(CONTENT_FIELD_NAME, text, standardAnalyzer);
            return memoryIndex.search(query);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }
}
