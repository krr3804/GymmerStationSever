package com.gymmer.gymmerstation.domain;

import java.io.Serializable;
import java.util.List;

public class OperationDataProgram implements Serializable {
    private static final long serialVersionUID = 1234567892L;

    private Program program;
    private Long week;
    private Long division;
    private List<OperationDataExercise> odExerciseList;

    public OperationDataProgram(Program program, Long week, Long division, List<OperationDataExercise> odExerciseList) {
        this.program = program;
        this.week = week;
        this.division = division;
        this.odExerciseList = odExerciseList;
    }

    public Program getProgram() {
        return program;
    }

    public Long getWeek() {
        return week;
    }

    public Long getDivision() {
        return division;
    }

    public List<OperationDataExercise> getOdExerciseList() {
        return odExerciseList;
    }
}
