package com.gymmer.gymmerstation.database;

import com.gymmer.gymmerstation.domain.OperationDataExercise;
import com.gymmer.gymmerstation.domain.OperationDataProgram;
import com.gymmer.gymmerstation.domain.Program;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import static com.gymmer.gymmerstation.database.DBConnection.*;

public class DBPerformanceData {
    public static int save(OperationDataProgram dataProgram) {
        Connection conn = getConnection();
        PreparedStatement psmt = null;
        Long program_id = dataProgram.getProgram().getId();
        Long week = dataProgram.getWeek();
        Long division = dataProgram.getDivision();
        int result = 0;
        try {
            String query = "INSERT INTO performance_data_exercise VALUES (?,?,?,?,?,?,?,?,?)";
            psmt = conn.prepareStatement(query);
            for(OperationDataExercise dataExercise : dataProgram.getOdExerciseList()) {
                psmt.setString(1,dataExercise.getName());
                psmt.setLong(2,dataExercise.getCurrentSet());
                psmt.setLong(3,dataExercise.getRep());
                psmt.setString(4,dataExercise.getWeight());
                psmt.setString(5,dataExercise.getRestTime());
                psmt.setLong(6,week);
                psmt.setLong(7,division);
                psmt.setString(8,dataExercise.getTimeConsumed());
                psmt.setLong(9,program_id);
                psmt.addBatch();
                psmt.clearParameters();
            }
            result = psmt.executeBatch().length;
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        } finally {
            closePreparedStatement(psmt);
            closeConnection(conn);
        }
        return result;
    }

    public static int delete(Long programId) {
        Connection conn = getConnection();
        PreparedStatement psmt = null;
        String query;
        int result = 0;
        try {
            query = "DELETE FROM performance_data_exercise USING program LEFT JOIN " +
                    "performance_data_exercise ON program_id = performance_data_exercise.program where program.program_id = ?";
            psmt = conn.prepareStatement(query);
            psmt.setLong(1,programId);
            result += psmt.executeUpdate();

            query = "DELETE FROM program WHERE termination_status = true AND program_id = ?;";
            psmt = conn.prepareStatement(query);
            psmt.setLong(1,programId);
            result += psmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        } finally {
            closePreparedStatement(psmt);
            closeConnection(conn);
        }
        return result;
    }

    public static Long terminateProgram(Long programId) {
        Connection conn = getConnection();
        PreparedStatement psmt = null;
        long key = 0L;
        try {
            String query = "INSERT INTO program SELECT null, name, purpose, length, divisionQty, true, user FROM program where program_id = ?";
            psmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            psmt.setLong(1,programId);
            psmt.executeUpdate();

            ResultSet rs = psmt.getGeneratedKeys();
            if(rs.next()) {
                key = rs.getLong(1);
            }
            rs.close();

            query =  "UPDATE performance_data_exercise SET program = ? WHERE program = ?";
            psmt = conn.prepareStatement(query);
            psmt.setLong(1,key);
            psmt.setLong(2,programId);
            psmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        } finally {
            closePreparedStatement(psmt);
            closeConnection(conn);
        }
        return key;
    }

    public static List<OperationDataProgram> getPerformanceDataList(Program program) {
        Connection conn = getConnection();
        PreparedStatement psmt = null;
        ResultSet rs;
        List<OperationDataProgram> list = new ArrayList<>();
        try {
            String query = "SELECT * FROM performance_data_exercise where program = ?";
            psmt = conn.prepareStatement(query);
            psmt.setLong(1,program.getId());
            rs = psmt.executeQuery();
            OperationDataProgram currentDataProgram = null;
            while (rs.next()) {
                Long week = rs.getLong("week");
                Long division = rs.getLong("division");
                if (currentDataProgram == null) {
                    currentDataProgram = mapOperationDataProgram(program,week,division);
                } else if(!currentDataProgram.getWeek().equals(week) || !currentDataProgram.getDivision().equals(division)) {
                    list.add(currentDataProgram);
                    currentDataProgram = mapOperationDataProgram(program,week,division);
                }
                currentDataProgram.getOdExerciseList().add(mapOperationDataExercise(rs));
            }
            if (currentDataProgram != null) {
                list.add(currentDataProgram);
            }
            rs.close();

        } catch (SQLException e) {
            System.err.println(e.getMessage());
        } finally {
            closePreparedStatement(psmt);
            closeConnection(conn);
        }
        return list;
    }

    private static OperationDataProgram mapOperationDataProgram(Program program, Long week, Long division) {
        OperationDataProgram dataProgram = new OperationDataProgram(
                program, week, division, new ArrayList<>()
        );
        return dataProgram;
    }

    private static OperationDataExercise mapOperationDataExercise(ResultSet rs) throws SQLException {
        OperationDataExercise dataExercise = new OperationDataExercise(
                rs.getString("name"), rs.getLong("c_set"), rs.getLong("reps"), rs.getString("weight"),
                rs.getString("rest"),rs.getString("time_consumed")
        );
        return dataExercise;
    }

    public static int getProgress(Long programId) {
        Connection conn = getConnection();
        PreparedStatement psmt = null;
        int progress = 0;
        try {
            String query = "SELECT COUNT(DISTINCT(CONCAT(week,'-',division))) AS PROGRESS FROM performance_data_exercise WHERE program = ?;";
            psmt = conn.prepareStatement(query);
            psmt.setLong(1,programId);
            ResultSet rs = psmt.executeQuery();
            while (rs.next()) {
                progress = rs.getInt("progress");
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        } finally {
            closePreparedStatement(psmt);
            closeConnection(conn);
        }
        return progress;
    }

    public static List<Program> getPrograms(String userId, boolean status) {
        Connection conn = getConnection();
        PreparedStatement psmt = null;
        ResultSet rs = null;
        List<Program> programList = new ArrayList<>();
        try {
            String query = "SELECT program.program_id, program.name, program.purpose, program.length, program.divisionQty FROM performance_data_exercise " +
                    "INNER JOIN program ON performance_data_exercise.program = program.program_id WHERE program.user = ? AND program.termination_status = ? " +
                    "GROUP BY performance_data_exercise.program ;";
            psmt = conn.prepareStatement(query);
            psmt.setString(1,userId);
            psmt.setBoolean(2,status);
            rs = psmt.executeQuery();
            while (rs.next()) {
                programList.add(mapProgram(rs));
            }
            rs.close();;
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        } finally {
            closePreparedStatement(psmt);
            closeConnection(conn);
        }
        return programList;
    }

    private static Program mapProgram(ResultSet rs) throws SQLException {
        Program program = new Program(
                rs.getLong("program.program_id"),
                rs.getString("program.name"),
                rs.getString("program.purpose"),
                rs.getLong("program.length"),
                rs.getLong("program.divisionQty")
        );
        return program;
    }
}
