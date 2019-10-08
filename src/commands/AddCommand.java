package commands;

import dataBase.DBConnection;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.SQLException;

public class AddCommand implements Command {

    private DBConnection con;

    public AddCommand(DBConnection connection){
        con = connection;
    }

    @Override
    public boolean execute() throws SQLException, IOException {
        BufferedReader bufRead = new BufferedReader(new InputStreamReader(System.in));

        System.out.println("Input the code: ");
        String code = bufRead.readLine();
        System.out.println("Input the position: ");
        String position = bufRead.readLine();
        System.out.println("Input the comment: ");
        String comment = bufRead.readLine();

        con.addRecord(code, position, comment);
        return true;
    }

    @Override
    public Command clone() {
        return new AddCommand(con);
    }
}
