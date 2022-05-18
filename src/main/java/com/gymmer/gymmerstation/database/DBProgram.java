package com.gymmer.gymmerstation.database;

import com.gymmer.gymmerstation.domain.Exercise;
import com.gymmer.gymmerstation.domain.Program;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DBProgram {
    public static Long addProgram(String userId, Program program){
        Long key = 0L;
        Connection conn = DBConnection.getConnection();
        PreparedStatement psmt = null;
        try {
            String query = "INSERT INTO program values (null,?,?,?,?,false,?);";
            psmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            psmt.setString(1, program.getName());
            psmt.setString(2, program.getPurpose());
            psmt.setLong(3, program.getLength());
            psmt.setLong(4, program.getDivisionQty());
            psmt.setString(5,userId);
            psmt.executeUpdate();

            ResultSet rs = psmt.getGeneratedKeys();
            if(rs.next()) {
                key = rs.getLong(1);
            }
            rs.close();
            psmt.clearParameters();
            query = "INSERT INTO exercise values (?,?,?,?,?,?);";
            psmt = conn.prepareStatement(query);
            psmt = addExercises(psmt,key,program.getExerciseList());
            psmt.executeBatch();
        } catch (SQLException e) {
            System.err.println("SQL error : " + e.getMessage());
        } finally {
            DBConnection.closePreparedStatement(psmt);
            DBConnection.closeConnection(conn);
        }
        return key;
    }

    public static void editProgram(Program program) {
        Connection conn = DBConnection.getConnection();
        PreparedStatement psmt = null;
        String query;
        try {
            query = "UPDATE program SET name = ?, purpose = ?, length = ?, divisionQty = ? WHERE program_id = ?;";
            psmt = conn.prepareStatement(query);
            psmt.setString(1, program.getName());
            psmt.setString(2, program.getPurpose());
            psmt.setLong(3, program.getLength());
            psmt.setLong(4,program.getDivisionQty());
            psmt.setLong(5, program.getId());
            psmt.executeUpdate();
            psmt.clearParameters();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        finally {
            DBConnection.closePreparedStatement(psmt);
            DBConnection.closeConnection(conn);
        }
    }

    public static void replaceExercises(Long programId, List<Exercise> exerciseList) {
        Connection conn = DBConnection.getConnection();
        PreparedStatement psmt = null;
        String query;
        try {
            query = "DELETE FROM exercise where program = ?;";
            psmt = conn.prepareStatement(query);
            psmt.setLong(1,programId);
            psmt.executeUpdate();
            psmt.clearParameters();

            query = "INSERT INTO exercise values (?,?,?,?,?,?);";
            psmt = conn.prepareStatement(query);
            psmt = addExercises(psmt,programId,exerciseList);
            psmt.executeBatch();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        finally {
            DBConnection.closePreparedStatement(psmt);
            DBConnection.closeConnection(conn);
        }
    }

    private static PreparedStatement addExercises(PreparedStatement psmt, Long programId, List<Exercise> exerciseList) {
        try {
            for (Exercise exercise : exerciseList) {
                psmt.setString(1, exercise.getName());
                psmt.setLong(2, exercise.getSet());
                psmt.setString(3, exercise.getWeightType());
                psmt.setString(4, exercise.getRestTime());
                psmt.setLong(5, exercise.getDivision());
                psmt.setLong(6, programId);
                psmt.addBatch();
                psmt.clearParameters();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return psmt;
    }

    public static List<Program> getProgramList(String userId) {
        List<Program> list = new ArrayList<>();
        Connection conn = DBConnection.getConnection();
        PreparedStatement psmt = null;
        ResultSet rs;
        try {
            String query = "select * from program inner join exercise on program.program_id = exercise.program where program.termination_status = false AND program.user = ?;";
            psmt = conn.prepareStatement(query);
            psmt.setString(1,userId);
            rs = psmt.executeQuery();
            Program currentProgram = null;
            while (rs.next()) {
                Long id = rs.getLong("program_id");
                if(currentProgram == null) {
                    currentProgram = mapProgram(rs);
                }
                if(!id.equals(currentProgram.getId())) {
                    list.add(currentProgram);
                    currentProgram = mapProgram(rs);
                }
                currentProgram.getExerciseList().add(mapExercise(rs));
            }
            if(currentProgram != null) {
                list.add(currentProgram);
            }
            rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBConnection.closePreparedStatement(psmt);
            DBConnection.closeConnection(conn);
        }

        return list;
    }

    private static Program mapProgram(ResultSet rs) throws SQLException {
        Program program = new Program(
                rs.getLong("program_id"),
                rs.getString("name"),
                rs.getString("purpose"),
                rs.getLong("length"),
                rs.getLong("divisionQty"),
                new ArrayList<>());
        return program;
    }

    private static Exercise mapExercise(ResultSet rs) throws SQLException {
        Exercise exercise = new Exercise(
                rs.getString("exercise_name"),
                rs.getLong("sets"),
                rs.getString("weight_type"),
                rs.getString("rest"),
                rs.getLong("division"));
        return exercise;
    }

    public static int deleteProgram(Long programId) {
        Connection conn = DBConnection.getConnection();
        PreparedStatement psmt = null;
        int success = 0;
        try {
            String query = "delete from exercise where program = ?;";
            psmt = conn.prepareStatement(query);
            psmt.setLong(1,programId);
            success += psmt.executeUpdate();
            psmt.clearParameters();
            query = "delete from program where program_id = ?;";
            psmt = conn.prepareStatement(query);
            psmt.setLong(1,programId);
            success += psmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("sql : " + e.getMessage());
        } finally {
            DBConnection.closePreparedStatement(psmt);
            DBConnection.closeConnection(conn);
        }
        return success;
    }
}
