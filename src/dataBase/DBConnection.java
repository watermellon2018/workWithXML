package dataBase;

import data.RecordDepartament;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.*;
import java.util.*;


public class DBConnection implements AutoCloseable {
    public static final int DELETE = 2, ADD = 1, UPDATE = 3;
    private static final String TABLE_NAME = "Departament";
    private static final Logger dataBaseLogger = LogManager.getLogger(DBConnection.class);

    private Connection connection;
    private Statement statement;

    public DBConnection() throws SQLException {
        /**Устонавливаем связь**/

        Properties properties = new Properties();
        try {
            properties.load(new FileReader(new File("setting/properties")));
        } catch (IOException e) {
            dataBaseLogger.error(e);
        }

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            PropertyConfigurator.configure(properties.getProperty("nameFileLog"));
            dataBaseLogger.info("Драйвер подключен");

        } catch (ClassNotFoundException e) {
            dataBaseLogger.error(e);
        }


        String url = properties.getProperty("url");
        String userName = properties.getProperty("userName");
        String password = properties.getProperty("password");


        connection = DriverManager.getConnection(url, userName, password);
        dataBaseLogger.info("База данных подключена");
        statement = connection.createStatement();
    }

    public boolean isExistRow(String code, String position) throws SQLException {
        ResultSet res = statement.executeQuery("select DepCode, DepJob from " + TABLE_NAME + " where DepCode = '"+code+
                "' and DepJob = '"+position+"';");
        res.last();
        int size = res.getRow();
        res.close();

        if(size>=1)
            return false;
        return true;
    }

    public Set<RecordDepartament> getData() throws SQLException {
        ResultSet data = statement.executeQuery("select * from " + TABLE_NAME + ";");
        Set<RecordDepartament> listData = new HashSet<>();

        dataBaseLogger.debug("Получили все данные из базы");

        while (data.next()){
            String code = data.getString(2);
            String job = data.getString(3);
            String comment = data.getString(4);
            listData.add(new RecordDepartament(code, job, comment));
        }
        data.close();

        dataBaseLogger.debug("Перебрали данные и загнали в hashset");
        dataBaseLogger.info("Достали все данные из базы");
        return listData;
    }

    public boolean addRecord(String code, String position, String comment) throws SQLException {

        if(!isExistRow(code, position)) {
            System.out.println("Not added, existed");
            return false;
        }

        dataBaseLogger.debug("Запись еще не существует, готовимся вставлять данные в базу");
        statement.executeUpdate("insert into " + TABLE_NAME + " (DepCode, DepJob, Description) values('" +
                code+"', '"+ position+"', '"+comment+"');");

        dataBaseLogger.info("Добавили запись в таблицу");
        return true;
    }

    public boolean createTable() throws SQLException {

        statement.executeUpdate("create table if not exists " + TABLE_NAME + "(ID int AUTO_INCREMENT, DepCode CHAR(20) NOT NULL, DepJob" +
                " CHAR(100) NOT NULL, Description CHAR(255), PRIMARY KEY(id));");

        dataBaseLogger.debug("Создали таблицу, если ее не было");

//            statCreate.executeUpdate("insert into Departament (DepCode, DepJob, Description) values('S', 'Guard', 'Very dangerous');");
//            statCreate.executeUpdate("insert into Departament (DepCode, DepJob, Description) values('P', 'Programmer', 'Write code');");
//            statCreate.executeUpdate("insert into Departament (DepCode, DepJob, Description) values('C', 'Cleaner', 'Keeps it clean');");

        return true;
    }

    private void delete(String code, String job) throws SQLException {
        statement.executeUpdate("DELETE FROM " + TABLE_NAME + " WHERE DepCode = '"+code+"' and DepJob = '"+job+"';");
    }

    private void update(String code, String job, String comment) throws SQLException {
        statement.executeUpdate("UPDATE " + TABLE_NAME + " SET Description = '"+comment+"' WHERE DepCode = '"+code+"' and DepJob = '"+job+"';");
    }

    public void upgrade(Map<Integer, Set<RecordDepartament>> result){

        try {
            connection.setAutoCommit(false);
            dataBaseLogger.debug("Начинаем менять базу данных в соответсвии с файлом");

            for (RecordDepartament x : result.get(DELETE)) {
                delete(x.getCode(), x.getJob());
                dataBaseLogger.info("Удалили запись " + x.toString() + " из базы");
            }

            for (RecordDepartament x : result.get(UPDATE)) {
                update(x.getCode(), x.getJob(), x.getComment());
                dataBaseLogger.info(" Обновили запись " + x.toString() + " в базе");
            }

            for (RecordDepartament x : result.get(ADD)) {
                statement.executeUpdate("insert into " + TABLE_NAME + " (DepCode, DepJob, Description) values('" +
                        x.getCode() + "', '" + x.getJob() + "', '" + x.getComment() + "');");
                dataBaseLogger.info(" Добавили запись " + x.toString() + " в базе");
            }

            connection.commit();
            dataBaseLogger.debug("Закончили менять базу в соответсвии с файлом");

        } catch (SQLException e) {
            dataBaseLogger.error(e);
        }
    }

    @Override
    public void close() throws Exception {
        connection.close();
        statement.close();
        dataBaseLogger.debug("Закрыли связь с базой данных");
    }
}