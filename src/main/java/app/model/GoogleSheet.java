package app.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class GoogleSheet
{
    public GoogleSheet(String name)
    {
        this.name = name;
    }

    String name;
    LocalDate modifyDate;
}
