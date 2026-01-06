package de.borowski.trainer.model;

/**
 * Enum representing the available question catalogs.
 */
public enum QuestionType {
    UBI("UBI-Fragenkatalog.json"),
    SRC("SRC-Fragenkatalog.json");
    
    private final String filename;
    
    QuestionType(String filename) {
        this.filename = filename;
    }
    
    public String getFilename() {
        return filename;
    }
}
