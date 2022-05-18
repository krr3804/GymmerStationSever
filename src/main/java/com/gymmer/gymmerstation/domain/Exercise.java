package com.gymmer.gymmerstation.domain;
import java.io.Serializable;
import java.util.Objects;

public class Exercise implements Serializable {
    private static final long serialVersionUID = 1234567891L;

    private String name;
    private Long set;
    private String weightType;
    private String restTime;
    private Long division;

    public Exercise(String name, Long set, String weightType, String restTime, Long division) {
        this.name = name;
        this.set = set;
        this.weightType = weightType;
        this.restTime = restTime;
        this.division = division;
    }

    public String getName() {
        return name;
    }

    public Long getSet() {
        return set;
    }

    public String getWeightType() {
        return weightType;
    }

    public String getRestTime() {
        return restTime;
    }

    public Long getDivision() {
        return division;
    }

    @Override
    public boolean equals(Object o) {
        if(this == o) {
            return true;
        }
        if(o == null || getClass() != o.getClass()) {
            return false;
        }
        Exercise other = (Exercise) o;

        return Objects.equals(other.name,name) && Objects.equals(other.set,set) &&
                Objects.equals(other.weightType,weightType) && Objects.equals(other.restTime,restTime) && Objects.equals(other.division,division);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name,set,weightType,restTime,division);
    }

    public void decreaseDivisionSequence(Long division) {
        this.division = division-1;
    }
}
