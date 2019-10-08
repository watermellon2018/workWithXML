package interaction;

import commands.AddCommand;
import commands.Command;
import commands.LoadCommand;
import commands.SaveCommand;
import dataBase.DBConnection;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.awt.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.sql.SQLException;
import java.util.Scanner;

public class DialogUser {
    private static final Logger dialogLogger = LogManager.getLogger(DialogUser.class);

    public void beganSpeak() {

        Command prototypeSave, prototypeLoad, prototAdd;

        try (DBConnection con = new DBConnection()) {
            con.createTable();

            prototypeLoad = new LoadCommand(con);
            prototypeSave = new SaveCommand(con);
            prototAdd = new AddCommand(con);

            BufferedReader buf = new BufferedReader(new InputStreamReader(System.in));
            while (true) {
                System.out.println("What should i do?\n1.save\n2.synh\n3.add\n4.end");
                String command = buf.readLine();
                command = command.replaceAll(" ", "");

                if (command.equals("end")) {
                    System.out.println("Goodbye");
                    break;
                }

                dialogLogger.debug("Получили команду от пользователя. Провели не выход ли это. Это не выход");

                switch (command) {
                    case "add":
                        Command addCom = prototAdd.clone();
                        addCom.execute();
                        System.out.println("The record was added");
                        dialogLogger.info("Добавлена запись");

                        break;

                    case "save":
                        Command save = prototypeSave.clone();
                        save.execute();
                        System.out.println("Data was saved in file");
                        dialogLogger.info("Сохранили данные");

                        break;

                    case "synh":
                        Command load = prototypeLoad.clone();

                        if(load.execute()) {
                            System.out.println("Data was synhronized");
                            dialogLogger.info("Синхронизировали данные");
                        }else
                            dialogLogger.debug("Что-то пошло не так, данные не синхронизировались");
                        break;
                }
            }

        } catch (SQLException e) {
           dialogLogger.error(e);
        } catch (Exception e) {
            dialogLogger.error(e);
        }
    }
}