package com.project.user.global.exception;

public class SleepGoalNotFoundException extends RuntimeException {
    public SleepGoalNotFoundException() {
        super("수면 목표를 찾을 수 없습니다.");
    }
}