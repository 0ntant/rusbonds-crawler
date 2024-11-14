package app.model;

import com.google.api.services.sheets.v4.model.Color;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Objects;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class DataCell
{
    Object value;
    String note;
    Color cellColor;
    String formula;

//    @Override
//    public boolean equals(Object o)
//    {
//        if (this == o) return true;
//        if (o == null || getClass() != o.getClass()) return false;
//        DataCell dataCell = (DataCell) o;
//        return Objects.equals(value, dataCell.value)
//                && Objects.equals(note, dataCell.note)
//                && Objects.equals(cellColor, dataCell.cellColor)
//                && Objects.equals(formula, dataCell.formula);
//    }

}
