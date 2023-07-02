package com.example.quiz;

public class UserProgress {
    private int score;
    private int levelIndex;
    private int currentQuestionIndex;

    public UserProgress() {}

    public UserProgress(int score, int currentQuestionIndex) {
        this.score = score;
        this.currentQuestionIndex = currentQuestionIndex;
    }


    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public int getLevelIndex() {
        return levelIndex;
    }

    public void setLevelIndex(int levelIndex) {
        this.levelIndex = levelIndex;
    }

    public int getCurrentQuestionIndex() {
        return currentQuestionIndex;
    }

    public void setCurrentQuestionIndex(int currentQuestionIndex) {
        this.currentQuestionIndex = currentQuestionIndex;
    }
}
