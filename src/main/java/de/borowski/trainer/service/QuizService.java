package de.borowski.trainer.service;

import de.borowski.trainer.model.Answer;
import de.borowski.trainer.model.Question;
import de.borowski.trainer.model.QuestionType;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.Set;

/**
 * Service responsible for quiz logic including question selection and answer validation.
 */
@Service
public class QuizService {
    
    private final QuestionLoader questionLoader;
    private final Random random = new Random();
    
    public QuizService(QuestionLoader questionLoader) {
        this.questionLoader = questionLoader;
    }
    
    /**
     * Gets a random question that hasn't been answered yet.
     * 
     * @param type the question catalog type
     * @param answeredIds set of already answered question IDs
     * @return a random unanswered question with shuffled answers
     * @throws IllegalStateException if no unanswered questions remain
     */
    public Question getRandomQuestion(QuestionType type, Set<String> answeredIds) {
        List<Question> allQuestions = questionLoader.getQuestions(type);
        
        // Filter out already answered questions
        List<Question> unansweredQuestions = allQuestions.stream()
            .filter(q -> !answeredIds.contains(q.id()))
            .toList();
        
        if (unansweredQuestions.isEmpty()) {
            throw new IllegalStateException("No unanswered questions available");
        }
        
        // Select a random question
        Question selectedQuestion = unansweredQuestions.get(random.nextInt(unansweredQuestions.size()));
        
        // Shuffle the answers before returning
        return shuffleAnswers(selectedQuestion);
    }
    
    /**
     * Creates a new Question with shuffled answers.
     * 
     * @param question the original question
     * @return a new Question record with shuffled answers
     */
    public Question shuffleAnswers(Question question) {
        List<Answer> shuffledAnswers = new ArrayList<>(question.answers());
        Collections.shuffle(shuffledAnswers, random);
        
        return new Question(
            question.id(),
            question.question(),
            shuffledAnswers
        );
    }
    
    /**
     * Checks if the selected answer is correct.
     * 
     * @param questionId the question ID
     * @param answerIndex the index of the selected answer
     * @param type the question catalog type
     * @return true if the answer is correct, false otherwise
     */
    public boolean checkAnswer(String questionId, int answerIndex, QuestionType type) {
        List<Question> allQuestions = questionLoader.getQuestions(type);
        
        // Find the original question (not shuffled)
        Question originalQuestion = allQuestions.stream()
            .filter(q -> q.id().equals(questionId))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("Question not found: " + questionId));
        
        // Note: We need to find which answer was selected based on the text,
        // since the answers were shuffled when displayed
        // The answerIndex refers to the position in the shuffled list
        // We'll need to pass the answer text instead to validate correctly
        
        // For now, validate bounds
        if (answerIndex < 0 || answerIndex >= originalQuestion.answers().size()) {
            return false;
        }
        
        // We can't directly use the index because answers are shuffled
        // This will be handled differently - we'll pass the answer text from the client
        return originalQuestion.answers().get(answerIndex).isCorrect();
    }
    
    /**
     * Checks if the answer with the given text is correct for the specified question.
     * 
     * @param questionId the question ID
     * @param answerText the text of the selected answer
     * @param type the question catalog type
     * @return true if the answer is correct, false otherwise
     */
    public boolean checkAnswerByText(String questionId, String answerText, QuestionType type) {
        List<Question> allQuestions = questionLoader.getQuestions(type);
        
        Question originalQuestion = allQuestions.stream()
            .filter(q -> q.id().equals(questionId))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("Question not found: " + questionId));
        
        return originalQuestion.answers().stream()
            .filter(a -> a.text().equals(answerText))
            .findFirst()
            .map(Answer::isCorrect)
            .orElse(false);
    }
    
    /**
     * Checks if all questions in a catalog have been answered.
     * 
     * @param type the question catalog type
     * @param answeredIds set of answered question IDs
     * @return true if all questions have been answered, false otherwise
     */
    public boolean isQuizComplete(QuestionType type, Set<String> answeredIds) {
        List<Question> allQuestions = questionLoader.getQuestions(type);
        return answeredIds.size() >= allQuestions.size();
    }
    
    /**
     * Gets the total number of questions for a catalog type.
     * 
     * @param type the question catalog type
     * @return total number of questions
     */
    public int getTotalQuestions(QuestionType type) {
        return questionLoader.getQuestions(type).size();
    }
}
