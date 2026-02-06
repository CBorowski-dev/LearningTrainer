package de.borowski.trainer.model;

import java.util.List;

/**
 * Represents a quiz question with multiple answer options.
 * 
 * @param id unique identifier for the question
 * @param question the question text
 * @param answers list of possible answers
 */
public record Question(
    String id,
    String question,
    List<Answer> answers
) {}
