package commands;

import dataBase.DBConnection;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import strategy.strategySave.StrategySave;
import strategy.strategySave.XMLSaveStrategy;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.SQLException;
import java.util.Scanner;

public class SaveCommand implements Command {
    private DBConnection connection;
    private static final Logger saveLogger = LogManager.getLogger(SaveCommand.class);


    public SaveCommand(DBConnection con){
        connection = con;
    }

    @Override
    public boolean execute() throws SQLException, IOException {
        BufferedReader buffRead = new BufferedReader(new InputStreamReader(System.in));
        System.out.println("Input the name of the file:");
        String nameFile = buffRead.readLine();

        nameFile = toCorrectName(nameFile);
        saveLogger.debug("Имя файла корректно");

        StrategySave saveXml = new XMLSaveStrategy();
        saveXml.save(connection.getData(), new File(nameFile));

        return true;
    }

    private String toCorrectName(String name){

        if(!name.contains(".xml")){
            name +=".xml";
        }

        return name;
    }

    @Override
    public SaveCommand clone(){
        return new SaveCommand(connection);
    }
}
