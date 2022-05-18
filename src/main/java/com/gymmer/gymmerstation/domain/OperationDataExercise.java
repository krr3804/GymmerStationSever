package com.gymmer.gymmerstation.domain;

import java.io.Serializable;

public class OperationDataExercise implements Serializable {
    private static final long serialVersionUID = 1234567893L;

    private String name;
    private Long currentSet;
    private Long rep;
    private String weight;
    private String restTime;
    private String timeConsumed;


    public OperationDataExercise(String name, Long currentSet, Long rep, String weight, String restTime, String timeConsumed) {
        this.name = name;
        this.currentSet = currentSet;
        this.rep = rep;
        this.weight = weight;
        this.restTime = restTime;
        this.timeConsumed = timeConsumed;
    }

    public String getName() {
        return name;
    }

    public Long getRep() {
        return rep;
    }

    public String getWeight() {
        return weight;
    }

    public String getRestTime() {
        return restTime;
    }

    public Long getCurrentSet() {
        return currentSet;
    }

    public String getTimeConsumed() {
        return timeConsumed;
    }
}
