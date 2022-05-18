package com.gymmer.gymmerstation.server;

import com.gymmer.gymmerstation.database.DBPerformanceData;
import com.gymmer.gymmerstation.database.DBProgram;
import com.gymmer.gymmerstation.domain.OperationDataProgram;
import com.gymmer.gymmerstation.domain.Program;

import java.io.*;
import java.net.Socket;
import java.util.HashMap;
import java.util.List;

import static com.gymmer.gymmerstation.database.DBUser.*;
import static com.gymmer.gymmerstation.server.ServerMain.usersList;

public class ServerThread extends Thread {
    private Socket socket;
    private String id = null;

    public ServerThread(Socket socket) {
        this.socket = socket;
    }


    @Override
    public void run() {
        ObjectInputStream ois = null;
        ObjectOutputStream oos = null;
        try {
            while (true) {
                ois = new ObjectInputStream(socket.getInputStream());
                HashMap<String, Object> map = (HashMap<String, Object>) ois.readObject();

                if (map == null) {
                    System.out.println("Connection Lost!");
                    if(id != null && usersList.containsKey(id)) {
                        usersList.remove(id);
                        id = null;
                    }
                    break;
                }
                //Login & Registration process
                else if (map.containsKey("checkExistID")) {
                    String userId = (String) map.get("checkExistID");
                    int checkNum = checkExistID(userId);
                    oos = new ObjectOutputStream(socket.getOutputStream());
                    oos.writeObject(checkNum);
                    oos.flush();
                }

                else if (map.containsKey("register")) {
                    String[] token = ((String) map.get("register")).split(",");
                    int success = registerNewUser(token[0], token[1]);
                    oos = new ObjectOutputStream(socket.getOutputStream());
                    oos.writeObject(success);
                    oos.flush();
                }

                else if (map.containsKey("login")) {
                    String userId = (String) map.get("login");
                    String passwordDB = login(userId);
                    oos = new ObjectOutputStream(socket.getOutputStream());
                    oos.writeObject(passwordDB);
                    oos.flush();
                }

                else if (map.containsKey("checkAlreadyLoggedIn")) {
                    String userId = (String) map.get("checkAlreadyLoggedIn");
                    oos = new ObjectOutputStream(socket.getOutputStream());
                    int result = -1;
                    synchronized (this) {
                        if(usersList.containsKey(userId)) {
                            result = 0;
                        } else {
                            result = 1;
                            id = userId;
                            usersList.put(userId,socket);
                        }
                        oos.writeObject(result);
                        oos.flush();
                    }
                }

                else if (map.containsKey("logOut")) {
                    synchronized (this) {
                        String userId = (String) map.get("logOut");
                        usersList.remove(userId);
                        id = null;
                    }
                }
                //Program Management Process
                else if (map.containsKey("addProgram")) {
                    Program program = (Program) map.get("addProgram");
                    Long programId = DBProgram.addProgram(id, program);

                    oos = new ObjectOutputStream(socket.getOutputStream());
                    oos.writeObject(programId);
                    oos.flush();
                }
                else if (map.containsKey("getProgramList")) {
                    id = (String) map.get("getProgramList");
                    List<Program> programList = DBProgram.getProgramList(id);
                    oos = new ObjectOutputStream(socket.getOutputStream());
                    oos.writeObject(programList);
                    oos.flush();
                }
                else if (map.containsKey("deleteProgram")) {
                    Long programId = (Long) map.get("deleteProgram");
                    int success = DBProgram.deleteProgram(programId);
                    oos = new ObjectOutputStream(socket.getOutputStream());
                    oos.writeObject(success);
                    oos.flush();
                }
                else if (map.containsKey("editProgram")) {
                    Program program = (Program) map.get("editProgram");
                    DBProgram.editProgram(program);
                }
                else if (map.containsKey("replaceExercises")) {
                    Program program = (Program) map.get("replaceExercises");
                    DBProgram.replaceExercises(program.getId(),program.getExerciseList());
                }

                //Program Operation Process
                else if (map.containsKey("savePerformanceData")) {
                    OperationDataProgram odp = (OperationDataProgram) map.get("savePerformanceData");
                    int result = DBPerformanceData.save(odp);
                    oos = new ObjectOutputStream(socket.getOutputStream());
                    oos.writeObject(result);
                    oos.flush();
                } else if (map.containsKey("deletePerformanceData")) {
                    Long programId = (Long) map.get("deletePerformanceData");
                    int result = DBPerformanceData.delete(programId);
                    oos = new ObjectOutputStream(socket.getOutputStream());
                    oos.writeObject(result);
                    oos.flush();
                } else if (map.containsKey("terminateProgram")) {
                    Long programId = (Long) map.get("terminateProgram");
                    Long terminatedProgramId = DBPerformanceData.terminateProgram(programId);
                    oos = new ObjectOutputStream(socket.getOutputStream());
                    oos.writeObject(terminatedProgramId);
                    oos.flush();
                } else if (map.containsKey("getPerformanceDataList")) {
                    Program program = (Program) map.get("getPerformanceDataList");
                    List<OperationDataProgram> performanceDataList = DBPerformanceData.getPerformanceDataList(program);
                    oos = new ObjectOutputStream(socket.getOutputStream());
                    oos.writeObject(performanceDataList);
                    oos.flush();
                } else if (map.containsKey("getProgress")) {
                    Long programId = (Long) map.get("getProgress");
                    int progress = DBPerformanceData.getProgress(programId);
                    oos = new ObjectOutputStream(socket.getOutputStream());
                    oos.writeObject(progress);
                    oos.flush();
                } else if (map.containsKey("getProgramsInArchive")) {
                    boolean status = (boolean) map.get("getProgramsInArchive");
                    List<Program> programs = DBPerformanceData.getPrograms(id, status);
                    oos = new ObjectOutputStream(socket.getOutputStream());
                    oos.writeObject(programs);
                    oos.flush();
                }
            }
        } catch (EOFException e1) {

        } catch (IOException | ClassNotFoundException e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
        }

        if(id != null && usersList.containsKey(id)) {
            usersList.remove(id);
            id = null;
        }
    }
}
