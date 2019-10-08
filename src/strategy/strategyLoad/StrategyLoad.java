package strategy.strategyLoad;

import data.RecordDepartament;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface StrategyLoad {
    Map<Integer, Set<RecordDepartament>> load(Set<RecordDepartament> data, File file);
}
