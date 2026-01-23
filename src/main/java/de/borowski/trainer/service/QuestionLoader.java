package de.borowski.trainer.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.borowski.trainer.model.Question;
import de.borowski.trainer.model.QuestionType;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/**
 * Service responsible for loading and caching question catalogs from JSON files.
 */
@Service
public class QuestionLoader {
    
    private static final Logger logger = LoggerFactory.getLogger(QuestionLoader.class);
    
    private final Map<QuestionType, List<Question>> questionsCache = new EnumMap<>(QuestionType.class);
    private final ObjectMapper objectMapper;
    
    public QuestionLoader(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }
    
    /**
     * Loads all question catalogs at application startup.
     */
    @PostConstruct
    public void loadQuestions() {
        logger.info("Loading question catalogs...");
        
        for (QuestionType type : QuestionType.values()) {
            try {
                List<Question> questions = loadQuestionFile(type.getFilename());
                questionsCache.put(type, questions);
                logger.info("Loaded {} questions for catalog: {}", questions.size(), type);
            } catch (IOException e) {
                logger.error("Failed to load question catalog: {}", type.getFilename(), e);
                throw new RuntimeException("Failed to load question catalog: " + type.getFilename(), e);
            }
        }
        
        logger.info("All question catalogs loaded successfully");
    }
    
    /**
     * Loads questions from a JSON file.
     */
    private List<Question> loadQuestionFile(String filename) throws IOException {
        System.out.println("filename : " + filename);
        // ClassPathResource resource = new ClassPathResource(filename);
        
        // try (InputStream inputStream = resource.getInputStream()) {
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream(filename)) {
            return objectMapper.readValue(inputStream, new TypeReference<List<Question>>() {});
        }
    }
    
    /**
     * Returns the cached list of questions for the specified type.
     * 
     * @param type the question catalog type
     * @return list of questions
     */
    public List<Question> getQuestions(QuestionType type) {
        return questionsCache.get(type);
    }
}
