package strategy.strategySave;

import data.RecordDepartament;

import java.io.File;
import java.util.List;
import java.util.Set;

public interface StrategySave {
    void save(Set<RecordDepartament> data, File file);

}
