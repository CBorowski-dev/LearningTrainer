package de.borowski.trainer.dto;

import de.borowski.trainer.model.QuestionType;

/**
 * Request DTO for checking an answer.
 * 
 * @param questionId the ID of the question being answered
 * @param answerText the text of the selected answer
 * @param type the question catalog type
 */
public record CheckRequest(
    String questionId,
    String answerText,
    QuestionType type
) {}
