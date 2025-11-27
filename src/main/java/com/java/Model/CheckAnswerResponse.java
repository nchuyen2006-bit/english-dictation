package com.java.Model;

import java.math.BigDecimal;
import java.util.List;

public class CheckAnswerResponse {
    private boolean isCorrect;
    private BigDecimal score;
    private String correctAnswer;
    private String userAnswer;
    private List<WordComparison> differences;
    private String feedback;

    public CheckAnswerResponse() {
    }

    public boolean isCorrect() {
        return isCorrect;
    }

    public void setCorrect(boolean correct) {
        isCorrect = correct;
    }

    public BigDecimal getScore() {
        return score;
    }

    public void setScore(BigDecimal score) {
        this.score = score;
    }

    public String getCorrectAnswer() {
        return correctAnswer;
    }

    public void setCorrectAnswer(String correctAnswer) {
        this.correctAnswer = correctAnswer;
    }

    public String getUserAnswer() {
        return userAnswer;
    }

    public void setUserAnswer(String userAnswer) {
        this.userAnswer = userAnswer;
    }

    public List<WordComparison> getDifferences() {
        return differences;
    }

    public void setDifferences(List<WordComparison> differences) {
        this.differences = differences;
    }

    public String getFeedback() {
        return feedback;
    }

    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }

    // Inner class để so sánh từng từ
    public static class WordComparison {
        private String word;
        private String status; // "correct", "wrong", "missing", "extra"

        public WordComparison(String word, String status) {
            this.word = word;
            this.status = status;
        }

        public String getWord() {
            return word;
        }

        public void setWord(String word) {
            this.word = word;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }
    }
}