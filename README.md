# Learning Trainer

A modern web-based quiz application built with Spring Boot 4.0.1 and Java 21 for training with multiple-choice questions from two catalogs: UBI (Binnenschifffahrtsfunk) and SRC (Seefunkdienst).

## Features

- 📚 Two question catalogs with 130 questions each
- 🎲 Randomized question selection and shuffled answers
- 📊 Session-based progress tracking
- ✅ Immediate answer validation
- 🎯 Completion tracking per catalog
- 📱 Responsive design for mobile and desktop
- 🐳 Docker support with multi-stage builds

## Technology Stack

- **Backend**: Spring Boot 4.0.1, Java 21
- **Frontend**: Thymeleaf, HTML5, CSS3, JavaScript
- **Build Tool**: Maven
- **Container**: Docker

## Project Structure

```
LearningTrainer/
├── src/
│   ├── main/
│   │   ├── java/de/borowski/trainer/
│   │   │   ├── LearningTrainerApplication.java
│   │   │   ├── controller/
│   │   │   │   └── LearningController.java
│   │   │   ├── service/
│   │   │   │   ├── QuestionLoader.java
│   │   │   │   └── QuizService.java
│   │   │   ├── model/
│   │   │   │   ├── Answer.java
│   │   │   │   ├── Question.java
│   │   │   │   └── QuestionType.java
│   │   │   └── dto/
│   │   │       ├── CheckRequest.java
│   │   │       └── CheckResponse.java
│   │   └── resources/
│   │       ├── application.properties
│   │       ├── UBI-Fragenkatalog.json (130 questions)
│   │       ├── SRC-Fragenkatalog.json (130 questions)
│   │       ├── templates/
│   │       │   ├── start.html
│   │       │   └── query.html
│   │       └── static/
│   │           ├── css/style.css
│   │           └── js/quiz.js
│   └── test/
│       └── java/de/borowski/trainer/
├── Dockerfile
├── .dockerignore
├── pom.xml
└── README.md
```

## Getting Started

### Prerequisites

- Java 21 or higher
- Maven 3.9.x or higher
- Docker (optional, for containerized deployment)

### Local Development

1. **Clone the repository**
   ```bash
   git clone <repository-url>
   cd LearningTrainer
   ```

2. **Run with Maven**
   ```bash
   ./mvnw spring-boot:run
   ```

3. **Or build and run the JAR**
   ```bash
   ./mvnw clean package
   java -jar target/trainer-0.0.1-SNAPSHOT.jar
   ```

4. **Access the application**
   
   Open your browser and navigate to: `http://localhost:8080`

### Docker Deployment

#### Build and run with Docker

```bash
# Build the Docker image
docker build -t learning-trainer:latest .

# Run the container
docker run -p 8080:8080 learning-trainer:latest
```

#### Using Docker Compose (if you have docker-compose.yml)

```bash
docker-compose up -d
```

## How It Works

### Application Flow

1. **Start Page**: User selects either UBI or SRC catalog
2. **Question Display**: Random unanswered question is shown with shuffled answers
3. **Answer Validation**: 
   - ✅ Correct answer → Next question loads automatically
   - ❌ Wrong answer → Alert shown, user can try again
4. **Progress Tracking**: Session tracks answered questions
5. **Completion**: After all questions are answered, congratulations message appears

### Key Features Implementation

#### Question Randomization
- Questions are shuffled randomly from the pool of unanswered questions
- Each question ID is tracked in the HTTP session to prevent repetition
- When all questions are answered, the session is cleared

#### Answer Shuffling
- Answer options are randomized for each question display
- The correct answer position changes every time
- Answer validation is done by text matching, not by index

#### Session Management
- Separate session tracking for UBI and SRC catalogs
- Session attributes: `ubi_answered` and `src_answered`
- Session timeout: 30 minutes (configurable)

## API Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/` | Display start page with catalog selection |
| GET | `/quiz?type={UBI\|SRC}` | Display a random question from selected catalog |
| POST | `/quiz/check` | Validate answer and return result |

### POST /quiz/check Request Format

```json
{
  "questionId": "1",
  "answerText": "Selected answer text",
  "type": "UBI"
}
```

### Response Format

```json
{
  "correct": true,
  "completed": false,
  "message": "Correct!"
}
```

## Configuration

### application.properties

```properties
# Application name
spring.application.name=LearningTrainer

# Server configuration
server.port=8080

# Thymeleaf configuration
spring.thymeleaf.cache=false

# Session configuration
server.servlet.session.timeout=30m

# Logging
logging.level.de.borowski.trainer=INFO
```

## Building for Production

### Maven Build

```bash
# Clean build with tests
./mvnw clean install

# Build without tests
./mvnw clean package -DskipTests
```

### Docker Build

```bash
# Build optimized production image
docker build -t learning-trainer:1.0.0 .

# Tag for registry
docker tag learning-trainer:1.0.0 your-registry/learning-trainer:1.0.0

# Push to registry
docker push your-registry/learning-trainer:1.0.0
```

## Performance Considerations

- **Question Caching**: All questions are loaded once at startup and cached in memory
- **Virtual Threads**: Java 21 virtual threads for efficient concurrent request handling
- **Session Storage**: In-memory sessions (consider Redis for production clustering)
- **Static Resources**: CSS and JS are cached by the browser

## Development Tips

### Live Reload

For development, Thymeleaf template caching is disabled. Changes to HTML templates will be reflected on page refresh.

### Testing

```bash
# Run all tests
./mvnw test

# Run with coverage
./mvnw test jacoco:report
```

### Debugging

```bash
# Run with debug enabled
./mvnw spring-boot:run -Dspring-boot.run.jvmArguments="-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005"
```

## Troubleshooting

### Common Issues

1. **Port 8080 already in use**
   ```bash
   # Change port in application.properties
   server.port=8081
   ```

2. **JSON parsing errors**
   - Verify JSON files are in `src/main/resources/`
   - Check JSON syntax is valid
   - Ensure `is_correct` field uses underscores

3. **Session lost between requests**
   - Check session timeout configuration
   - Verify cookies are enabled in browser
   - Check for HTTPS/HTTP mixed content issues

## License

This project is part of a training application for maritime radio communications certification.

## Author

Christoph Borowski

## Version History

- **1.0.0** (2026-01-06)
  - Initial release
  - UBI and SRC question catalogs
  - Session-based progress tracking
  - Docker support
  - Responsive UI design
