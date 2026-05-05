package com.example.ai_study_assistant_android.model;

public class GenerateRequest {
    private String text;
    private String type;
    private String subject;

    public GenerateRequest(String text, String type, String subject) {
        this.text = text;
        this.type = type;
        this.subject = subject;
    }

    public String getText() { return text; }
    public String getType() { return type; }
    public String getSubject() { return subject; }
}
