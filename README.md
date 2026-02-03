# Entity Scout

Entity Scout is a Java-based application designed to scout and monitor words from texts using Lucene pattern matching. It supports Lucene queries for advanced searching, language detection, and document extraction via Apache Tika. The application exposes a RESTful API and is containerized with Docker for easy deployment.

## Purpose

The primary goal of Entity Scout is to:
- Detect URLs from various video conferencing platforms (e.g., Zoom, Microsoft Teams, Google Meet, Webex, etc.).
- Perform text searches using Lucene queries.
- Detect languages in provided content with confidence levels.
- Extract structured information, including text, metadata, and attachments, from documents in various formats.

## Technologies Used

- **Programming Language**: Java
- **Build Tool**: Maven (with wrapper for consistent builds)
- **Annotations**: Lombok (to reduce boilerplate code)
- **Search Engine**: Apache Lucene
- **Document Parsing**: Apache Tika
- **API Framework**: Spring Boot (inferred from controllers and OpenAPI paths)
- **Containerization**: Docker and Docker Compose

## Features

- **URL Scouting**: Supports pattern matching for URLs from platforms like Zoom, Microsoft Teams, Google Meet, Webex, Jitsi, Zoho, Livestorm, Airmeet, ClickMeeting, BlueJeans, GoToMeeting, Join.me, Whereby, Adobe Connect, and Pexip using Lucene queries.
- **Language Detection**: Analyzes input text to detect languages, providing confidence levels and detailed results.
- **Document Extraction**: Uses Apache Tika to parse documents, extracting text, metadata, language, title, and attachments. Supports recursive extraction and output as ZIP files.
- **RESTful API**: Exposes endpoints for search, language detection, and document extraction.
- **Docker Support**: Easily deployable via Docker images and Compose for containerized environments.
- **Maven Builds**: Reproducible builds with Maven wrapper.

## Installation

### Using Docker

1. Pull the Docker image:
   ```
   docker pull nkhzrd/entityscout
   ```

2. Start the application using Docker Compose:
   ```
   docker-compose up -d
   ```

   The API will be available at `http://localhost:8080`.

### Building from Source

1. Clone the repository:
   ```
   git clone https://github.com/vkotletkin/entityscout.git
   ```

2. Navigate to the project directory:
   ```
   cd entityscout
   ```

3. Build the project using Maven wrapper:
   ```
   ./mvnw clean install
   ```

4. Run the JAR file:
   ```
   java -jar target/entityscout-<version>.jar
   ```

   The API will be available at `http://localhost:8080`.

## Usage

### API Endpoints

- **POST /api/search/single**: Perform a Lucene search on provided text.
    - Request Body: JSON with text and Lucene query.
    - Example Query: `url:(https://zoom.us* OR https://teams.microsoft.com* OR https://meet.google.com*)`

- **POST /api/language**: Detect the language of the input text.
    - Request Body: JSON with text to analyze.

- **POST /api/documents/extract**: Extract information from uploaded documents.
    - Supports text, metadata, language, title, and attachments extraction.

- **POST /api/documents/extract/attachments**: Extract attachments as a ZIP file.
    - Supports recursive extraction.

For detailed API documentation, refer to the OpenAPI specification (version 3.1.0) available in the project.

### Example Lucene Query for URL Detection

```
https://zoom.us* OR https://teams.microsoft.com* OR https://meet.google.com* OR https://webex.com* OR https://meet.jitsi* OR https://meeting.zoho.com* OR https://app.livestorm.co* OR https://airmeet.com* OR https://clickmeeting.com* OR https://bluejeans.com* OR https://global.gotomeeting.com* OR https://join.me* OR https://whereby.com* OR https://adobeconnect.com* OR https://pexip.com*
```

## Code Structure

- `src/`: Contains the main Java source code.
- `pom.xml`: Maven project configuration and dependencies.
- `Dockerfile`: Instructions for building the Docker image.
- `docker-compose.yaml`: Configuration for Docker Compose.
- `lombok.config`: Lombok configuration file.
- `mvnw` and `mvnw.cmd`: Maven wrapper scripts.
- `.mvn/wrapper/`: Maven wrapper files.
- `.gitattributes` and `.gitignore`: Git configuration files.

## Dependencies

Dependencies are managed via `pom.xml` and include:
- Spring Boot
- Apache Lucene
- Apache Tika
- Lombok

## Configuration

- API server URL and port can be configured in `application.properties` or `application.yml`.
- Lombok is configured via `lombok.config`.
- Docker image can be customized as needed.

## License

This project is licensed under the MIT License. See the LICENSE file for details (add if not present).

## Contributing

Contributions are welcome! Fork the repository, create a branch, commit your changes, and submit a pull request. For issues or suggestions, open a GitHub issue.

## Contact

For questions or support, open an issue on GitHub or contact via email (placeholder: your.email@example.com).