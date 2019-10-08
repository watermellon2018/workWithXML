package commands;

import java.io.IOException;
import java.sql.SQLException;

public interface Command {
    boolean execute() throws SQLException, IOException;
    Command clone();
}
