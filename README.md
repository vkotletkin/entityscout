# Entity Scout Project

## Overview

Entity Scout is a Java-based application designed to scout and monitor URLs associated with popular video conferencing and meeting platforms using Lucene-based pattern matching. It enables users to perform various Lucene queries for searching text, detect languages in provided content, and extract data from documents using Apache Tika for parsing various file formats. The project includes RESTful API endpoints for these functionalities and is containerized for easy deployment using Docker, built with Maven for dependency management and reproducible builds.

## Features

- **Lucene Query Support**: Allows performing various Lucene queries on text inputs, including URL pattern matching for detection of links from multiple platforms such as:
    - Zoom (`https://zoom.us*`)
    - Microsoft Teams (`https://teams.microsoft.com*`)
    - Google Meet (`https://meet.google.com*`)
    - Webex (`https://webex.com*`)
    - Jitsi (`https://meet.jitsi*`)
    - Zoho (`https://meeting.zoho.com*`)
    - Livestorm (`https://app.livestorm.co*`)
    - Airmeet (`https://airmeet.com*`)
    - ClickMeeting (`https://clickmeeting.com*`)
    - BlueJeans (`https://bluejeans.com*`)
    - GoToMeeting (`https://global.gotomeeting.com*`)
    - Join.me (`https://join.me*`)
    - Whereby (`https://whereby.com*`)
    - Adobe Connect (`https://adobeconnect.com*`)
    - Pexip (`https://pexip.com*`)

- **Language Detection**: Detects the language of input text with confidence levels and details.
- **Document Extraction**: Uses Apache Tika to extract information (e.g., text, metadata, language, title) and attachments from various document formats, including support for recursive extraction and ZIP output for attachments.
- **API Endpoints**: Provides REST APIs for single Lucene searches, language detection, and document extraction/attachment handling.
- **Docker Support**: Easy deployment via Docker images and Compose for orchestration.
- **Maven Build**: Uses Maven wrapper for consistent builds across environments.
- **Lombok Integration**: Reduces boilerplate code in Java classes for cleaner development.

## Technologies Used

- **Language**: Java (primary)
- **Build Tool**: Maven (with wrapper)
- **Annotations**: Lombok
- **Search Engine**: Apache Lucene
- **Document Parsing**: Apache Tika
- **API Framework**: Spring Boot (inferred from OpenAPI paths and controllers)
- **Containerization**: Docker and Docker Compose

## Prerequisites

- Java JDK 11 or higher (recommended for compatibility with modern Maven projects)
- Maven (or use the provided wrapper)
- Docker and Docker Compose for containerized deployment

## Installation

### Using Docker

1. Pull the Docker image:

   ```bash
   docker pull nkhzrd/entityscout
   ```

2. Use Docker Compose to start the application:

   ```bash
   docker-compose up -d
   ```

   This will launch the service based on the configuration in `docker-compose.yaml`. The API will be available at `http://localhost:8080`.

### Building from Source

1. Clone the repository:

   ```bash
   git clone https://github.com/vkotletkin/entityscout.git
   cd entityscout
   ```

2. Build the project using the Maven wrapper:

   ```bash
   ./mvnw clean install
   ```

   This will compile the code, run tests (if any), and package the application (typically as a JAR file in the `target/` directory).

3. Run the application (assuming it's an executable JAR):

   ```bash
   java -jar target/entityscout-<version>.jar
   ```

   *Note: Replace `<version>` with the actual version from `pom.xml`. Adjust command-line arguments or configuration as needed based on the application's requirements. The API will be available at `http://localhost:8080` by default.*

## Usage

The application can be used to perform Lucene queries on text for matching patterns, detect languages, and extract data from documents. For example:

- Submit text and a Lucene query to search for matches, such as identifying conferencing platform URLs.
- Detect the language of a given text snippet.
- Upload documents to extract metadata, content, and attachments using Tika.
- Integrate as a service in larger systems for text analysis, monitoring, or data processing.

Detailed usage examples depend on the specific implementation in the source code. Refer to the Java classes in `src/main/java` for core logic.

### Example Lucene Query for URLs

The following pattern can be used in searches or filters:

```
url:(https://zoom.us* OR https://teams.microsoft.com* OR https://meet.google.com* OR https://webex.com* OR https://meet.jitsi* OR https://meeting.zoho.com* OR https://app.livestorm.co* OR https://airmeet.com* OR https://clickmeeting.com* OR https://bluejeans.com* OR https://global.gotomeeting.com* OR https://join.me* OR https://whereby.com* OR https://adobeconnect.com* OR https://pexip.com*)
```

## API Documentation

The application exposes a REST API defined by an OpenAPI 3.1.0 specification. The server URL is `http://localhost:8080` (configurable). Below is a summary of the available endpoints. For full details, refer to the OpenAPI JSON specification provided in the project or generate it via Springdoc (if integrated).

### Endpoints

- **POST /api/search/single**
    - **Description**: Performs a single search operation based on the provided Lucene query.
    - **Tags**: search-controller
    - **Parameters**:
        - `query` (query, required): The Lucene query string.
    - **Request Body**: JSON object of type `BaseRequest` (required), containing `text` (string, min length 1).
    - **Responses**:
        - 200 OK: Returns `SearchSingleDTO` with `result` (boolean) and `score` (float).

- **POST /api/language**
    - **Description**: Detects the language of the provided text.
    - **Tags**: language-controller
    - **Request Body**: JSON object of type `BaseRequest` (required), containing `text` (string, min length 1).
    - **Responses**:
        - 200 OK: Returns `LanguageDetectionDTO` with `language` (string) and `details` (LanguageResult object, including confidence, rawScore, etc.).

- **POST /api/documents/extract**
    - **Description**: Extracts information from a document file using Tika, such as language, title, content, and metadata.
    - **Tags**: document-controller
    - **Parameters**:
        - `documentType` (query, optional, default: AUTO): Enum [AUTO, RFC822].
        - `includeAttachments` (query, optional, default: true): Boolean to include attachments.
    - **Request Body**: Multipart form data with `file` (binary, required).
    - **Responses**:
        - 200 OK: Returns an array of `DocumentInfo` objects, each containing `resourceName`, `language`, `title`, `contentType`, `text`, `isEncrypted`, and `metadata` (map of strings).

- **POST /api/documents/extract/attachments**
    - **Description**: Extracts attachments from a document using Tika and returns them as a ZIP file.
    - **Tags**: document-controller
    - **Parameters**:
        - `documentType` (query, optional, default: AUTO): Enum [AUTO, RFC822].
        - `maximumDepth` (query, optional, default: 10, min: 1): Integer for recursion depth.
    - **Request Body**: Multipart form data with `file` (binary, required).
    - **Responses**:
        - 200 OK: Returns octet-stream (byte array) as ZIP file.

### Schemas

- **BaseRequest**: Object with required `text` (string).
- **SearchSingleDTO**: Object with `result` (boolean) and `score` (float).
- **LanguageDetectionDTO**: Object with `language` (string) and `details` (LanguageResult).
- **LanguageResult**: Object with `language` (string), `confidence` (enum: HIGH, MEDIUM, LOW, NONE), `rawScore` (float), `unknown` (boolean), `reasonablyCertain` (boolean).
- **DocumentInfo**: Object with `resourceName`, `language`, `title`, `contentType`, `text`, `isEncrypted` (all strings), and `metadata` (map).

For the full OpenAPI specification in JSON format, see below:

```json
{"openapi":"3.1.0","info":{"title":"OpenAPI definition","version":"v0"},"servers":[{"url":"http://localhost:8080","description":"Generated server url"}],"paths":{"/api/search/single":{"post":{"tags":["search-controller"],"operationId":"searchSingle","parameters":[{"name":"query","in":"query","required":true,"schema":{"type":"string"}}],"requestBody":{"content":{"application/json":{"schema":{"$ref":"#/components/schemas/BaseRequest"}}},"required":true},"responses":{"200":{"description":"OK","content":{"*/*":{"schema":{"$ref":"#/components/schemas/SearchSingleDTO"}}}}}}},"/api/language":{"post":{"tags":["language-controller"],"operationId":"detectLanguage","requestBody":{"content":{"application/json":{"schema":{"$ref":"#/components/schemas/BaseRequest"}}},"required":true},"responses":{"200":{"description":"OK","content":{"*/*":{"schema":{"$ref":"#/components/schemas/LanguageDetectionDTO"}}}}}}},"/api/documents/extract":{"post":{"tags":["document-controller"],"operationId":"extractAutoInfoAboutDocument","parameters":[{"name":"documentType","in":"query","required":false,"schema":{"type":"string","default":"AUTO","enum":["AUTO","RFC822"]}},{"name":"includeAttachments","in":"query","required":false,"schema":{"type":"boolean","default":true}}],"requestBody":{"content":{"multipart/form-data":{"schema":{"type":"object","properties":{"file":{"type":"string","format":"binary"}},"required":["file"]}}}},"responses":{"200":{"description":"OK","content":{"*/*":{"schema":{"type":"array","items":{"$ref":"#/components/schemas/DocumentInfo"}}}}}}}},"/api/documents/extract/attachments":{"post":{"tags":["document-controller"],"operationId":"extractAttachmentToZip","parameters":[{"name":"documentType","in":"query","required":false,"schema":{"type":"string","default":"AUTO","enum":["AUTO","RFC822"]}},{"name":"maximumDepth","in":"query","required":false,"schema":{"type":"integer","format":"int32","default":10,"minimum":1}}],"requestBody":{"content":{"multipart/form-data":{"schema":{"type":"object","properties":{"file":{"type":"string","format":"binary"}},"required":["file"]}}}},"responses":{"200":{"description":"OK","content":{"application/octet-stream":{"schema":{"type":"string","format":"byte"}}}}}}}},"components":{"schemas":{"BaseRequest":{"type":"object","properties":{"text":{"type":"string","minLength":1}},"required":["text"]},"SearchSingleDTO":{"type":"object","properties":{"result":{"type":"boolean"},"score":{"type":"number","format":"float"}}},"LanguageDetectionDTO":{"type":"object","properties":{"language":{"type":"string"},"details":{"$ref":"#/components/schemas/LanguageResult"}}},"LanguageResult":{"type":"object","properties":{"language":{"type":"string"},"confidence":{"type":"string","enum":["HIGH","MEDIUM","LOW","NONE"]},"rawScore":{"type":"number","format":"float"},"unknown":{"type":"boolean"},"reasonablyCertain":{"type":"boolean"}}},"DocumentInfo":{"type":"object","properties":{"resourceName":{"type":"string"},"language":{"type":"string"},"title":{"type":"string"},"contentType":{"type":"string"},"text":{"type":"string"},"isEncrypted":{"type":"string"},"metadata":{"type":"object","additionalProperties":{"type":"string"}}}}}}}
```

## Configuration

- **Lombok**: Configured via `lombok.config`. Ensure your IDE supports Lombok (e.g., install the Lombok plugin in IntelliJ IDEA).
- **Dockerfile**: Customizes the container build. Modify as needed for production environments.
- **API Configuration**: Server port and other settings can be configured in `application.properties` or `application.yml`.

## Contributing

Contributions are welcome! To contribute:

1. Fork the repository.
2. Create a new branch for your feature or bug fix.
3. Commit your changes with clear messages.
4. Push to your fork and submit a pull request.

Please ensure code follows Java best practices, includes tests where applicable, and updates documentation.

## License

This project is open-source and available under the [MIT License](LICENSE). (Note: Add a LICENSE file to the repository if not already present.)

## Contact

For questions or suggestions, open an issue on GitHub or contact the maintainer at [your-email@example.com].