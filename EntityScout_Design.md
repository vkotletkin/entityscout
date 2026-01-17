# EntityScout — Многоязычный NLP Pipeline для Извлечения и Поиска Сущностей

## 1. Концепция проекта

**EntityScout** — это Java-приложение для обработки многоязычных документов с целью извлечения структурированной информации (Named Entities), индексирования их в памяти и реализации быстрого семантического поиска.

### Ключевая цель
Создать универсальный pipeline, который:
1. Принимает документы в любом формате (PDF, DOCX, HTML и т.д.)
2. Автоматически определяет язык текста
3. Извлекает именованные сущности (люди, места, организации и т.д.)
4. Индексирует найденные сущности в памяти для быстрого поиска
5. Позволяет выполнять сложные поисковые запросы по извлеченным данным

---

## 2. Технологический стек

```
┌─────────────────────────────────────────────────────────┐
│                    Input Layer                           │
│         Spring Tika DocumentReader (любой формат)       │
└────────────────────┬────────────────────────────────────┘
                     │ (raw text + metadata)
┌────────────────────▼────────────────────────────────────┐
│              Language Detection                          │
│         OpenNLP LanguageDetectorME (103 языка)          │
└────────────────────┬────────────────────────────────────┘
                     │ (detected language code)
┌────────────────────▼────────────────────────────────────┐
│             Named Entity Recognition                     │
│    Stanford CoreNLP (8 языков) или OpenNLP fallback     │
└────────────────────┬────────────────────────────────────┘
                     │ (entities: {text, type, confidence})
┌────────────────────▼────────────────────────────────────┐
│              In-Memory Indexing                          │
│    Apache Lucene MemoryIndex (real-time search)         │
└────────────────────┬────────────────────────────────────┘
                     │
┌────────────────────▼────────────────────────────────────┐
│             Query Interface                              │
│      REST API / Command Line / Programmatic API         │
└─────────────────────────────────────────────────────────┘
```

**Компоненты:**
- **Spring Tika Document Reader** — детекция формата, извлечение текста
- **Apache Tika 3.1.0** — парсинг 30+ форматов документов
- **OpenNLP 2.5.6** — определение языка из 103 языков (Apache 2.0)
- **Stanford CoreNLP 4.5+** — точное извлечение сущностей (GPL v3+)
- **Apache Lucene** — in-memory индексирование и поиск
- **Spring Boot 3.x** — REST API и управление жизненным циклом
- **Spring Data** — опционально для персистентности результатов

---

## 3. Архитектура системы

### 3.1 Слои приложения

```
┌───────────────────────────────┐
│   REST Controller Layer        │
│  (/api/documents, /api/search) │
└───────────────────────────────┘
         ▲          │
         │          ▼
┌───────────────────────────────┐
│   Service Layer               │
│  - DocumentProcessingService  │
│  - EntityExtractionService    │
│  - SearchService              │
└───────────────────────────────┘
         ▲          │
         │          ▼
┌───────────────────────────────┐
│   NLP Processing Layer        │
│  - LanguageDetector           │
│  - EntityRecognizer           │
│  - IndexManager               │
└───────────────────────────────┘
         ▲          │
         │          ▼
┌───────────────────────────────┐
│   External Libraries          │
│  - Spring Tika Reader         │
│  - OpenNLP Models             │
│  - Stanford CoreNLP Pipeline  │
│  - Lucene IndexWriter         │
└───────────────────────────────┘
```

### 3.2 Data Flow

```
Document (PDF/DOCX/HTML)
    │
    ▼ Spring Tika DocumentReader
Raw Text + Metadata
    │
    ▼ OpenNLP LanguageDetectorME
Language Code (e.g., "en", "ru", "es")
    │
    ▼ Stanford CoreNLP Pipeline
    │   (или OpenNLP fallback для unsupported языков)
Entities: [
    {type: "PERSON", text: "John Smith", confidence: 0.95},
    {type: "ORGANIZATION", text: "Google", confidence: 0.98},
    {type: "LOCATION", text: "Mountain View", confidence: 0.92}
]
    │
    ▼ Lucene MemoryIndex
Document Index with searchable fields:
    - entity_text
    - entity_type
    - source_document
    - language
    - confidence_score
    │
    ▼ Query Results (JSON)
```

---

## 4. Компоненты реализации

### 4.1 DocumentProcessingService

**Функции:**
- Принимает InputStream или Resource
- Использует Spring Tika DocumentReader для извлечения текста
- Возвращает структурированный объект Document

```java
@Service
public class DocumentProcessingService {
    
    public ProcessedDocument processDocument(Resource resource) 
            throws IOException {
        // 1. Extract text using Spring Tika
        TikaDocumentReader reader = new TikaDocumentReader(resource);
        List<Document> documents = reader.get();
        
        // 2. Combine content
        String fullText = documents.stream()
            .map(Document::getContent)
            .collect(Collectors.joining("\n"));
        
        // 3. Preserve metadata
        String filename = resource.getFilename();
        String mimeType = detectMimeType(resource);
        
        return new ProcessedDocument(
            fullText, 
            filename, 
            mimeType, 
            fullText.length()
        );
    }
    
    private String detectMimeType(Resource resource) {
        try (InputStream is = resource.getInputStream()) {
            Tika tika = new Tika();
            return tika.detect(is);
        } catch (IOException e) {
            return "application/octet-stream";
        }
    }
}
```

### 4.2 LanguageDetectionService

**Функции:**
- Загружает модель OpenNLP один раз при старте (кэширование)
- Определяет язык текста из 103 языков
- Возвращает коды ISO 639-3

```java
@Service
public class LanguageDetectionService {
    
    private LanguageDetectorME detector;
    private static final Logger log = LoggerFactory.getLogger(
        LanguageDetectionService.class
    );
    
    @PostConstruct
    public void init() throws IOException {
        // Load model once and cache it
        try (InputStream modelIn = this.getClass()
                .getResourceAsStream("/models/langdetect-183.bin")) {
            LanguageDetectorModel model = 
                new LanguageDetectorModel(modelIn);
            this.detector = new LanguageDetectorME(model);
            log.info("Language detection model loaded");
        }
    }
    
    public LanguageDetectionResult detectLanguage(String text) {
        // Return array of detected languages with confidence scores
        Language[] languages = detector.predictLanguages(text);
        
        if (languages.length == 0) {
            return new LanguageDetectionResult("unknown", 0.0);
        }
        
        Language primary = languages[0];
        return new LanguageDetectionResult(
            primary.getLang(), 
            primary.getConfidence()
        );
    }
    
    /**
     * Получить список поддерживаемых языков CoreNLP
     */
    public static final Set<String> CORENLP_SUPPORTED_LANGUAGES = 
        Set.of("en", "es", "fr", "de", "it", "hu", "zh", "ar");
    
    public boolean isSupportedByCorenlp(String languageCode) {
        return CORENLP_SUPPORTED_LANGUAGES.contains(languageCode);
    }
}
```

### 4.3 EntityExtractionService

**Функции:**
- Использует Stanford CoreNLP для поддерживаемых языков
- Fallback на OpenNLP для других языков
- Возвращает структурированный список сущностей

```java
@Service
public class EntityExtractionService {
    
    private final LanguageDetectionService langService;
    private final StanfordCoreNLPProvider stanfordProvider;
    private final OpenNLPProvider opennlpProvider;
    
    private static final Logger log = LoggerFactory.getLogger(
        EntityExtractionService.class
    );
    
    @Autowired
    public EntityExtractionService(
            LanguageDetectionService langService,
            StanfordCoreNLPProvider stanfordProvider,
            OpenNLPProvider opennlpProvider) {
        this.langService = langService;
        this.stanfordProvider = stanfordProvider;
        this.opennlpProvider = opennlpProvider;
    }
    
    public ExtractionResult extractEntities(
            String text, 
            String languageCode) {
        
        try {
            if (langService.isSupportedByCorenlp(languageCode)) {
                log.debug("Using Stanford CoreNLP for language: {}", 
                    languageCode);
                return stanfordProvider.extractEntities(text, languageCode);
            } else {
                log.debug("Using OpenNLP fallback for language: {}", 
                    languageCode);
                return opennlpProvider.extractEntities(text, languageCode);
            }
        } catch (Exception e) {
            log.error("Failed to extract entities", e);
            return new ExtractionResult(List.of(), languageCode, false, 
                e.getMessage());
        }
    }
}

// Data classes
public record Entity(
    String text,
    String type,  // PERSON, ORGANIZATION, LOCATION, MISC
    double confidence,
    int startOffset,
    int endOffset
) {}

public record ExtractionResult(
    List<Entity> entities,
    String language,
    boolean successful,
    String errorMessage
) {}
```

### 4.4 SearchIndexService

**Функции:**
- Создает и управляет Lucene MemoryIndex
- Индексирует извлеченные сущности
- Реализует поиск по различным критериям

```java
@Service
public class SearchIndexService {
    
    private IndexWriter indexWriter;
    private SearcherManager searcherManager;
    private final Analyzer analyzer = new StandardAnalyzer();
    private static final Logger log = LoggerFactory.getLogger(
        SearchIndexService.class
    );
    
    @PostConstruct
    public void init() throws IOException {
        // Initialize in-memory index
        RAMDirectory directory = new RAMDirectory();
        IndexWriterConfig config = new IndexWriterConfig(analyzer);
        this.indexWriter = new IndexWriter(directory, config);
        this.searcherManager = new SearcherManager(
            indexWriter, 
            true, 
            null
        );
        log.info("In-memory Lucene index initialized");
    }
    
    public void indexDocument(
            String documentId,
            String filename,
            List<Entity> entities,
            String language) throws IOException {
        
        for (Entity entity : entities) {
            Document doc = new Document();
            
            // Indexed fields
            doc.add(new StringField("documentId", documentId, 
                Field.Store.YES));
            doc.add(new StringField("filename", filename, 
                Field.Store.YES));
            doc.add(new TextField("entityText", entity.text(), 
                Field.Store.YES));
            doc.add(new StringField("entityType", entity.type(), 
                Field.Store.YES));
            doc.add(new StringField("language", language, 
                Field.Store.YES));
            
            // Searchable but not stored
            doc.add(new FloatPoint("confidence", 
                (float) entity.confidence()));
            doc.add(new IntPoint("offset", entity.startOffset()));
            
            indexWriter.addDocument(doc);
        }
        
        indexWriter.flush();
        searcherManager.maybeRefresh();
        log.debug("Indexed {} entities from document: {}", 
            entities.size(), filename);
    }
    
    public List<SearchResult> search(SearchQuery query) 
            throws IOException {
        
        IndexSearcher searcher = searcherManager.acquire();
        try {
            Query luceneQuery = buildQuery(query);
            TopDocs topDocs = searcher.search(luceneQuery, 100);
            
            List<SearchResult> results = new ArrayList<>();
            for (ScoreDoc scoreDoc : topDocs.scoreDocs) {
                Document doc = searcher.doc(scoreDoc.doc);
                results.add(new SearchResult(
                    doc.get("documentId"),
                    doc.get("filename"),
                    doc.get("entityText"),
                    doc.get("entityType"),
                    doc.get("language"),
                    scoreDoc.score
                ));
            }
            return results;
        } finally {
            searcherManager.release(searcher);
        }
    }
    
    private Query buildQuery(SearchQuery query) {
        BooleanQuery.Builder builder = new BooleanQuery.Builder();
        
        if (query.entityText() != null) {
            builder.add(
                new TermQuery(new Term("entityText", 
                    query.entityText().toLowerCase())),
                BooleanClause.Occur.MUST
            );
        }
        
        if (query.entityType() != null) {
            builder.add(
                new TermQuery(new Term("entityType", query.entityType())),
                BooleanClause.Occur.MUST
            );
        }
        
        if (query.language() != null) {
            builder.add(
                new TermQuery(new Term("language", query.language())),
                BooleanClause.Occur.MUST
            );
        }
        
        if (query.minConfidence() > 0) {
            builder.add(
                FloatPoint.newRangeQuery("confidence", 
                    (float) query.minConfidence(), Float.MAX_VALUE),
                BooleanClause.Occur.MUST
            );
        }
        
        return builder.build();
    }
}

public record SearchQuery(
    String entityText,
    String entityType,
    String language,
    double minConfidence
) {}

public record SearchResult(
    String documentId,
    String filename,
    String entityText,
    String entityType,
    String language,
    float score
) {}
```

### 4.5 REST Controller

**Endpoints:**

```java
@RestController
@RequestMapping("/api")
public class EntityScoutController {
    
    private final DocumentProcessingService docService;
    private final LanguageDetectionService langService;
    private final EntityExtractionService extractionService;
    private final SearchIndexService searchService;
    
    @PostMapping("/documents/process")
    public ResponseEntity<ProcessingResponse> processDocument(
            @RequestParam("file") MultipartFile file) 
            throws IOException {
        
        Resource resource = new ByteArrayResource(file.getBytes());
        
        // 1. Extract text
        ProcessedDocument processed = docService.processDocument(resource);
        
        // 2. Detect language
        LanguageDetectionResult langResult = 
            langService.detectLanguage(processed.text());
        
        // 3. Extract entities
        ExtractionResult extraction = extractionService.extractEntities(
            processed.text(),
            langResult.languageCode()
        );
        
        // 4. Index entities
        String docId = UUID.randomUUID().toString();
        searchService.indexDocument(
            docId,
            processed.filename(),
            extraction.entities(),
            langResult.languageCode()
        );
        
        return ResponseEntity.ok(new ProcessingResponse(
            docId,
            processed.filename(),
            langResult.languageCode(),
            extraction.entities().size(),
            extraction.entities()
        ));
    }
    
    @PostMapping("/search")
    public ResponseEntity<List<SearchResult>> search(
            @RequestBody SearchQuery query) throws IOException {
        
        List<SearchResult> results = searchService.search(query);
        return ResponseEntity.ok(results);
    }
    
    @GetMapping("/stats")
    public ResponseEntity<StatsResponse> getStats() {
        return ResponseEntity.ok(new StatsResponse(
            // Статистика по обработанным документам
        ));
    }
}

public record ProcessingResponse(
    String documentId,
    String filename,
    String detectedLanguage,
    int entitiesExtracted,
    List<Entity> entities
) {}
```

---

## 5. Зависимости (pom.xml)

```xml
<dependencies>
    <!-- Spring Boot -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
        <version>3.2.0</version>
    </dependency>
    
    <!-- Spring AI with Tika -->
    <dependency>
        <groupId>org.springframework.ai</groupId>
        <artifactId>spring-ai-tika-document-reader</artifactId>
        <version>1.1.2</version>
    </dependency>
    
    <!-- Apache Tika (уже включена в spring-ai-tika) -->
    <dependency>
        <groupId>org.apache.tika</groupId>
        <artifactId>tika-core</artifactId>
        <version>3.1.0</version>
    </dependency>
    <dependency>
        <groupId>org.apache.tika</groupId>
        <artifactId>tika-parsers-standard-package</artifactId>
        <version>3.1.0</version>
    </dependency>
    
    <!-- OpenNLP -->
    <dependency>
        <groupId>org.apache.opennlp</groupId>
        <artifactId>opennlp-tools</artifactId>
        <version>2.5.6</version>
    </dependency>
    
    <!-- Stanford CoreNLP -->
    <dependency>
        <groupId>edu.stanford.nlp</groupId>
        <artifactId>stanford-corenlp</artifactId>
        <version>4.5.2</version>
    </dependency>
    <dependency>
        <groupId>edu.stanford.nlp</groupId>
        <artifactId>stanford-corenlp</artifactId>
        <version>4.5.2</version>
        <classifier>models-english</classifier>
    </dependency>
    <dependency>
        <groupId>edu.stanford.nlp</groupId>
        <artifactId>stanford-corenlp</artifactId>
        <version>4.5.2</version>
        <classifier>models-spanish</classifier>
    </dependency>
    
    <!-- Apache Lucene -->
    <dependency>
        <groupId>org.apache.lucene</groupId>
        <artifactId>lucene-core</artifactId>
        <version>9.9.1</version>
    </dependency>
    <dependency>
        <groupId>org.apache.lucene</groupId>
        <artifactId>lucene-queryparser</artifactId>
        <version>9.9.1</version>
    </dependency>
    
    <!-- Logging -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-logging</artifactId>
    </dependency>
</dependencies>
```

---

## 6. Примеры использования

### 6.1 Через REST API

```bash
# Upload and process document
curl -X POST \
  -F "file=@document.pdf" \
  http://localhost:8080/api/documents/process

# Response:
{
  "documentId": "550e8400-e29b-41d4-a716-446655440000",
  "filename": "document.pdf",
  "detectedLanguage": "en",
  "entitiesExtracted": 12,
  "entities": [
    {
      "text": "John Smith",
      "type": "PERSON",
      "confidence": 0.95,
      "startOffset": 125,
      "endOffset": 135
    },
    {
      "text": "Google",
      "type": "ORGANIZATION",
      "confidence": 0.98,
      "startOffset": 200,
      "endOffset": 206
    }
  ]
}

# Search for entities
curl -X POST http://localhost:8080/api/search \
  -H "Content-Type: application/json" \
  -d '{
    "entityText": "Google",
    "entityType": "ORGANIZATION",
    "language": "en",
    "minConfidence": 0.9
  }'

# Response:
[
  {
    "documentId": "550e8400-e29b-41d4-a716-446655440000",
    "filename": "document.pdf",
    "entityText": "Google",
    "entityType": "ORGANIZATION",
    "language": "en",
    "score": 0.95
  }
]
```

### 6.2 Программно

```java
@SpringBootTest
public class EntityScoutIntegrationTest {
    
    @Autowired
    private DocumentProcessingService docService;
    
    @Autowired
    private EntityExtractionService extractionService;
    
    @Autowired
    private SearchIndexService searchService;
    
    @Test
    public void testFullPipeline() throws IOException {
        // 1. Load document
        Resource resource = new ClassPathResource("test-document.pdf");
        ProcessedDocument doc = docService.processDocument(resource);
        
        // 2. Extract entities
        ExtractionResult result = extractionService.extractEntities(
            doc.text(),
            "en"
        );
        
        // 3. Verify entities extracted
        assertThat(result.entities()).isNotEmpty();
        assertThat(result.entities())
            .anySatisfy(e -> e.type().equals("PERSON"));
        
        // 4. Index and search
        searchService.indexDocument(
            "test-doc-1",
            "test.pdf",
            result.entities(),
            "en"
        );
        
        SearchQuery query = new SearchQuery(
            null,
            "PERSON",
            "en",
            0.8
        );
        List<SearchResult> searchResults = searchService.search(query);
        assertThat(searchResults).isNotEmpty();
    }
}
```

---

## 7. Производительность и оптимизация

### 7.1 Кэширование моделей

```java
@Configuration
public class NLPModelCache {
    
    @Bean
    public Map<String, LanguageDetectorModel> languageDetectorCache() {
        return Collections.singletonMap(
            "langdetect",
            loadLanguageDetectorModel()
        );
    }
    
    @Bean
    public Map<String, StanfordCoreNLP> stanfordPipelineCache() {
        Map<String, StanfordCoreNLP> cache = new ConcurrentHashMap<>();
        
        for (String lang : Set.of("en", "es", "fr", "de", "it")) {
            Properties props = new Properties();
            props.setProperty("annotators", 
                "tokenize,ssplit,pos,lemma,ner");
            props.setProperty("ner.model", 
                String.format("edu/stanford/nlp/models/ner/english.all" +
                    ".3class.distsim.crf.ser.gz"));
            cache.put(lang, new StanfordCoreNLP(props));
        }
        
        return cache;
    }
}
```

### 7.2 Асинхронная обработка

```java
@Service
public class AsyncDocumentProcessor {
    
    @Async
    public CompletableFuture<ProcessingResponse> processAsync(
            MultipartFile file) {
        try {
            // Обработка в отдельном потоке
            return CompletableFuture.completedFuture(
                processDocument(file)
            );
        } catch (Exception e) {
            return CompletableFuture.failedFuture(e);
        }
    }
}
```

### 7.3 Batch обработка

```java
@Service
public class BatchProcessor {
    
    public void processBatch(List<Resource> documents) 
            throws IOException {
        documents.parallelStream()
            .forEach(doc -> {
                try {
                    ProcessedDocument processed = 
                        docService.processDocument(doc);
                    LanguageDetectionResult lang = 
                        langService.detectLanguage(processed.text());
                    ExtractionResult entities = 
                        extractionService.extractEntities(
                            processed.text(),
                            lang.languageCode()
                        );
                    searchService.indexDocument(
                        UUID.randomUUID().toString(),
                        processed.filename(),
                        entities.entities(),
                        lang.languageCode()
                    );
                } catch (IOException e) {
                    log.error("Error processing document", e);
                }
            });
    }
}
```

---

## 8. Лицензионные соображения

### Комбинированная лицензия:

| Компонент | Лицензия | Примечание |
|-----------|----------|-----------|
| Spring Boot | Apache 2.0 | ✅ Коммерческое использование |
| Spring AI | Apache 2.0 | ✅ Коммерческое использование |
| Apache Tika | Apache 2.0 | ✅ Коммерческое использование |
| OpenNLP | Apache 2.0 | ✅ Коммерческое использование |
| Lucene | Apache 2.0 | ✅ Коммерческое использование |
| **Stanford CoreNLP** | **GPL v3+** | ⚠️ **Требует публикации исходного кода** |

**Вариант без GPL:**
Если лицензия критична, замените Stanford CoreNLP на:
- OpenNLP NER (ниже точность, но Apache 2.0)
- Apache UIMA с OpenNLP (Apache 2.0)
- Custom ML модели (напр., на основе Deeplearning4j)

---

## 9. Возможные расширения

1. **Sentiment Analysis** — добавить анализ тональности текста
2. **Entity Linking** — связывание сущностей с Knowledge Graphs (DBpedia, Wikidata)
3. **Co-reference Resolution** — определение ссылок на одни и те же сущности
4. **Persistent Storage** — сохранение результатов в PostgreSQL/MongoDB
5. **REST API документация** — Swagger/OpenAPI 3.0
6. **Multi-language Pipeline** — параллельная обработка на нескольких языках
7. **Web UI** — интерфейс для загрузки документов и просмотра результатов
8. **Export Formats** — экспорт в JSON-LD, RDF, CSV
9. **Performance Metrics** — мониторинг производительности через Micrometer
10. **Domain-specific Models** — переобучение на специализированных корпусах

---

## 10. Структура проекта

```
entityscout/
├── src/main/java/com/example/entityscout/
│   ├── config/
│   │   ├── NLPModelCache.java
│   │   └── LuceneConfiguration.java
│   ├── service/
│   │   ├── DocumentProcessingService.java
│   │   ├── LanguageDetectionService.java
│   │   ├── EntityExtractionService.java
│   │   ├── StanfordCoreNLPProvider.java
│   │   ├── OpenNLPProvider.java
│   │   ├── SearchIndexService.java
│   │   └── AsyncDocumentProcessor.java
│   ├── controller/
│   │   └── EntityScoutController.java
│   ├── model/
│   │   ├── ProcessedDocument.java
│   │   ├── LanguageDetectionResult.java
│   │   ├── Entity.java
│   │   ├── ExtractionResult.java
│   │   └── SearchQuery.java
│   └── EntityscoutApplication.java
├── src/main/resources/
│   ├── application.yml
│   └── models/
│       └── langdetect-183.bin
├── src/test/
│   └── EntityScoutIntegrationTest.java
├── pom.xml
├── README.md
└── docker-compose.yml
```

---

## 11. Метрики успеха

1. ✅ Обработка документов любого формата (Tika)
2. ✅ Определение 103 языков (OpenNLP)
3. ✅ Точное извлечение сущностей для 8 языков (Stanford CoreNLP)
4. ✅ Быстрый поиск через Lucene MemoryIndex (<100ms для запроса)
5. ✅ REST API с документацией
6. ✅ Фолбэк на OpenNLP для unsupported языков
7. ✅ Обработка документов >100MB
8. ✅ Параллельная обработка batch-операций

---

**Разработано для: Java 17+, Spring Boot 3.x, Maven**
