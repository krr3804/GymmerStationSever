package com.gymmer.gymmerstation.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DBUser {

    public static synchronized String login(String userId) {
        Connection conn = DBConnection.getConnection();
        PreparedStatement psmt = null;
        String passwordDB = "";
        try {
            String query = "SELECT password from user where user_id = ?;";
            psmt = conn.prepareStatement(query);
            psmt.setString(1,userId);
            ResultSet rs = psmt.executeQuery();
            while (rs.next()) {
                passwordDB = rs.getString("password");
            }
            rs.close();
        } catch (SQLException e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
        }  finally {
            DBConnection.closePreparedStatement(psmt);
            DBConnection.closeConnection(conn);
        }
        return passwordDB;
    }

    public static synchronized int registerNewUser(String userId, String password) {
        Connection conn = DBConnection.getConnection();
        PreparedStatement psmt = null;
        int success = 0;
        try {
            String query = "INSERT INTO user VALUES (?,?)";
            psmt = conn.prepareStatement(query);
            psmt.setString(1,userId);
            psmt.setString(2,password);
            success = psmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
        }  finally {
            DBConnection.closePreparedStatement(psmt);
            DBConnection.closeConnection(conn);
        }
        return success;
    }

    public static synchronized int checkExistID(String userId) {
        Connection conn = DBConnection.getConnection();
        PreparedStatement psmt = null;
        int checkNum = 0;
        try {
            String query = "SELECT EXISTS (SELECT * FROM user WHERE user_id = ?)";
            psmt = conn.prepareStatement(query);
            psmt.setString(1,userId);
            ResultSet rs = psmt.executeQuery();
            while (rs.next()) {
                checkNum = rs.getInt(1);
            }
            rs.close();
        } catch (SQLException e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
        }  finally {
            DBConnection.closePreparedStatement(psmt);
            DBConnection.closeConnection(conn);
        }
        return checkNum;
    }
}
