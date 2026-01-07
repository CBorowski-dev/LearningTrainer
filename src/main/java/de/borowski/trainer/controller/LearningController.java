package de.borowski.trainer.controller;

import de.borowski.trainer.dto.CheckRequest;
import de.borowski.trainer.dto.CheckResponse;
import de.borowski.trainer.model.Question;
import de.borowski.trainer.model.QuestionType;
import de.borowski.trainer.service.QuizService;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.Set;

/**
 * Main controller for the Learning Trainer application.
 */
@Controller
public class LearningController {
    
    private static final Logger logger = LoggerFactory.getLogger(LearningController.class);
    private static final String UBI_ANSWERED_IDS = "ubi_answered";
    private static final String SRC_ANSWERED_IDS = "src_answered";
    
    private final QuizService quizService;
    
    public LearningController(QuizService quizService) {
        this.quizService = quizService;
    }
    
    /**
     * Displays the start page with catalog selection.
     */
    @GetMapping("/trainer/")
    public String start() {
        logger.info("Displaying start page");
        return "start";
    }
    
    /**
     * Displays a random question from the selected catalog.
     */
    @GetMapping("/trainer/quiz")
    public String quiz(
            @RequestParam QuestionType type,
            HttpSession session,
            Model model) {
        
        logger.info("Quiz requested for catalog: {}", type);
        
        // Get or create the set of answered question IDs for this catalog
        Set<String> answeredIds = getAnsweredIds(session, type);
        
        try {
            // Get a random unanswered question
            Question question = quizService.getRandomQuestion(type, answeredIds);
            
            // Add data to model
            model.addAttribute("question", question);
            model.addAttribute("type", type);
            model.addAttribute("answeredCount", answeredIds.size());
            model.addAttribute("totalCount", quizService.getTotalQuestions(type));
            
            logger.info("Displaying question {} for catalog {}", question.id(), type);
            
            return "query";
            
        } catch (IllegalStateException e) {
            // All questions answered - clear the set and redirect to start
            logger.info("All questions answered for catalog {}, clearing session", type);
            clearAnsweredIds(session, type);
            model.addAttribute("completionMessage", 
                "Congratulations! You've completed all " + quizService.getTotalQuestions(type) + 
                " questions in the " + type + " catalog!");
            return "start";
        }
    }
    
    /**
     * Checks if the submitted answer is correct.
     */
    @PostMapping("/trainer/quiz/check")
    @ResponseBody
    public ResponseEntity<CheckResponse> checkAnswer(
            @RequestBody CheckRequest request,
            HttpSession session) {
        
        logger.info("Checking answer for question {} in catalog {}", 
            request.questionId(), request.type());
        
        // Validate the answer
        boolean correct = quizService.checkAnswerByText(
            request.questionId(), 
            request.answerText(), 
            request.type()
        );
        
        if (correct) {
            // Add question ID to answered set
            Set<String> answeredIds = getAnsweredIds(session, request.type());
            answeredIds.add(request.questionId());
            
            // Check if quiz is complete
            boolean completed = quizService.isQuizComplete(request.type(), answeredIds);
            
            if (completed) {
                logger.info("Catalog {} completed!", request.type());
                return ResponseEntity.ok(new CheckResponse(
                    true, 
                    true, 
                    "Congratulations! You've completed all questions!"));
            } else {
                logger.info("Correct answer! Progress: {}/{}", 
                    answeredIds.size(), 
                    quizService.getTotalQuestions(request.type()));
                return ResponseEntity.ok(new CheckResponse(
                    true, 
                    false, 
                    "Correct!"));
            }
        } else {
            logger.info("Incorrect answer for question {}", request.questionId());
            return ResponseEntity.ok(new CheckResponse(
                false, 
                false, 
                "Wrong answer. Please try again."));
        }
    }
    
    /**
     * Gets the set of answered question IDs for the specified catalog type.
     */
    @SuppressWarnings("unchecked")
    private Set<String> getAnsweredIds(HttpSession session, QuestionType type) {
        String attributeName = getSessionAttributeName(type);
        Set<String> answeredIds = (Set<String>) session.getAttribute(attributeName);
        
        if (answeredIds == null) {
            answeredIds = new HashSet<>();
            session.setAttribute(attributeName, answeredIds);
        }
        
        return answeredIds;
    }
    
    /**
     * Clears the answered IDs for the specified catalog type.
     */
    private void clearAnsweredIds(HttpSession session, QuestionType type) {
        String attributeName = getSessionAttributeName(type);
        session.removeAttribute(attributeName);
    }
    
    /**
     * Gets the session attribute name for the specified catalog type.
     */
    private String getSessionAttributeName(QuestionType type) {
        return type == QuestionType.UBI ? UBI_ANSWERED_IDS : SRC_ANSWERED_IDS;
    }
}
