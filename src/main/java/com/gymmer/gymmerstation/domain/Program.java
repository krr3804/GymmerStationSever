package com.gymmer.gymmerstation.domain;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class Program implements Serializable {
    private static final long serialVersionUID = 1234567890L;

    private Long id;
    private String name;
    private String purpose;
    private Long length;
    private Long divisionQty;
    private List<Exercise> exerciseList;

    public Program(List<Exercise> exerciseList) {
        this.exerciseList = exerciseList;
    }

    public Program(Long id, String name, String purpose, Long length, Long divisionQty) {
        this.id = id;
        this.name = name;
        this.purpose = purpose;
        this.length = length;
        this.divisionQty = divisionQty;
    }

    public Program(Long id, String name, String purpose, Long length, Long divisionQty, List<Exercise> exerciseList) {
        this.id = id;
        this.name = name;
        this.purpose = purpose;
        this.length = length;
        this.divisionQty = divisionQty;
        this.exerciseList = exerciseList;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getPurpose() {
        return purpose;
    }

    public Long getLength() {
        return length;
    }

    public Exercise removeExercise(Long division, String name) {
        Exercise exercise = exerciseList.stream().filter(e -> e.getDivision().equals(division) && e.getName().equals(name)).findAny().get();
        exerciseList.remove(exercise);
        return exercise;
    }

    public List<Exercise> getExerciseList() {
        return exerciseList;
    }

    public Long getDivisionQty() {
        return divisionQty;
    }

    public Long countDivision() {
        return exerciseList.stream().map(Exercise::getDivision).distinct().count();
    }

    public List<Exercise> getExerciseByDivision(Long divisionNumber) {
        return exerciseList.stream().filter(exercise -> exercise.getDivision().equals(divisionNumber)).collect(Collectors.toList());
    }

    public void removeExerciseInDivision(Long division) {
        exerciseList.removeIf(exercise -> exercise.getDivision().equals(division));
        exerciseList.stream().filter(exercise -> exercise.getDivision()> division).forEach(exercise -> exercise.decreaseDivisionSequence(exercise.getDivision()));
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPurpose(String purpose) {
        this.purpose = purpose;
    }

    public void setLength(Long length) {
        this.length = length;
    }

    public void setDivisionQty(Long divisionQty) {
        this.divisionQty = divisionQty;
    }

    @Override
    public boolean equals(Object o) {
        if(this == o) {
            return true;
        }
        if(o == null || getClass() != o.getClass()) {
            return false;
        }
        Program other = (Program)o;

        return other.name == name && other.purpose == purpose && other.length == length && other.exerciseList == exerciseList;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name,purpose,length,exerciseList);
    }
}
