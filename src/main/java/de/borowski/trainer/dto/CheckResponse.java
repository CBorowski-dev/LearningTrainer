package de.borowski.trainer.dto;

/**
 * Response DTO for answer validation.
 * 
 * @param correct whether the answer was correct
 * @param completed whether all questions in the catalog have been answered
 * @param message optional message to display to the user
 */
public record CheckResponse(
    boolean correct,
    boolean completed,
    String message
) {}
