package de.borowski.trainer.model;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Represents a single answer option for a quiz question.
 * 
 * @param text the answer text
 * @param isCorrect whether this answer is the correct one
 */
public record Answer(
    String text,
    @JsonProperty("is_correct")
    boolean isCorrect
) {}
