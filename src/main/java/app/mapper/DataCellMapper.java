package app.mapper;

import app.model.Bond;
import app.model.DataCell;

import java.util.ArrayList;
import java.util.List;

public class DataCellMapper
{
    public static List<List<DataCell>> fillBondCells(List<List<DataCell>> dataCells)
    {
        List<List<DataCell>> dataCellsSync = new ArrayList<>();
        for (List<DataCell> row : dataCells)
        {
            List<DataCell> rowSync = new ArrayList<>();
            fillBondRow(row);
            Bond bond = mapBond(row);
            List<DataCell> dataCellValues = map(bond);
            for (int k =0; k < dataCellValues.size() ; k++)
            {
                rowSync.add(DataCell.builder()
                        .value(dataCellValues.get(k).getValue())
                        .formula(row.get(k).getFormula())
                        .note(row.get(k).getNote())
                        .cellColor(row.get(k).getCellColor())
                        .build());
            }
            dataCellsSync.add(rowSync);
        }
        return dataCellsSync;
    }

    private static void fillBondRow(List<DataCell> row)
    {
        int objDelta = BondMapper.bonsFieldCount - row.size();
        for (int i = 0; i < objDelta; i++)
        {
            row.add(new DataCell());
        }
    }

    public static List<Bond> mapBonds(List<List<DataCell>> dataCells)
    {
        List<Bond> bonds = new ArrayList<>(dataCells.size());
        for(List<DataCell> row : dataCells)
        {
            bonds.add(
                   mapBond(row)
            );
        }
        return bonds;
    }

    public static Bond mapBond(List<DataCell> row)
    {
        return BondMapper.map(mapValues(row));
    }

    public static List<Object> mapValues(List<DataCell> row)
    {
        List<Object> objects = new ArrayList<>();
        for (DataCell dataCell : row )
        {
            if (dataCell.getValue() != null)
            {
                objects.add(dataCell.getValue());
            }
            else
            {
                objects.add("");
            }
        }
        return objects;
    }

    public static List<List<DataCell>> map(List<Bond> bonds)
    {
        List<List<DataCell>> dataCells = new ArrayList<>();
        for(Bond bond : bonds)
        {
            dataCells.add(map(bond));
        }

        return dataCells;
    }

    public static List<DataCell> map(Bond bond)
    {
        List<DataCell> dataCells = new ArrayList<>();
        List<Object> objectList = BondMapper.map(bond);

        for(Object object : objectList)
        {
            DataCell dataCell = new DataCell();
            dataCell.setValue(object);
            dataCells.add(dataCell);
        }

        return dataCells;
    }
}
