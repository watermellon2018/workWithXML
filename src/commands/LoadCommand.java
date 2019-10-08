package commands;

import data.RecordDepartament;
import dataBase.DBConnection;
import interaction.DialogUser;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import strategy.strategyLoad.StrategyLoad;
import strategy.strategyLoad.XMLLoadStrategy;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.SQLException;
import java.util.Map;
import java.util.Set;

public class LoadCommand implements Command {
    private DBConnection connection;
    private static final Logger loadLogger = LogManager.getLogger(LoadCommand.class);

    public LoadCommand(DBConnection con){
        connection = con;
    }

    /**По имени файла выполняем чтение, а затем изменяем базу данных**/

    @Override
    public boolean execute() throws SQLException, IOException {

        BufferedReader buf = new BufferedReader(new InputStreamReader(System.in));

        System.out.println("Input the name of the file for synh:");
        String nameFile = buf.readLine();

        StrategyLoad strategyLoad = new XMLLoadStrategy();

        try {
            loadLogger.debug("Пытаемся синхронизировать данные");
            Map<Integer, Set<RecordDepartament>> res = strategyLoad.load(connection.getData(), new File(nameFile));
            connection.upgrade(res);
        }catch (NullPointerException e){
            loadLogger.error(e);
            return false;
        }

        return true;

    }

    @Override
    public LoadCommand clone(){
        return new LoadCommand(connection);
    }
}