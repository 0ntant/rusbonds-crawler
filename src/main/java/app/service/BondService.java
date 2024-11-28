package app.service;

import app.model.Bond;

import java.time.LocalDate;
import java.util.List;

public interface BondService {

    String getSheetName();

    List<Bond> getAll();

    List<Bond> getBondsToUpdate();

    Bond getBond(int row);

    int getMaxRow();

    void writeBond(Bond bond);

    void writeBonds(List<Bond> bonds);

    void formatTable(List<Bond> bonds);

    void sortByYieldNowRev(List<Bond> bonds);

    void numerateBonds(List<Bond> bonds);

    void writeModifyDate();

    LocalDate getModifyDate();

    String exportData();

    void importData(String dumpFile);

    String getModifyDateRange();
}
