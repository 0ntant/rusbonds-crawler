package app.util;

import app.model.Bond;

import java.util.List;

public class NotificationMessageUtil
{
    public static String duplicateBonds(String sheetName, List<Bond> bonds)
    {
        StringBuffer stringBuffer = new StringBuffer("[ОШИБКА]\n");
        stringBuffer.append("Обнаружены задвоенные ISIN коды в таблице: %s isin:\n".formatted(sheetName)
        );
        for(int i = 0; i < bonds.size(); i++)
        {
            String bondNumber = String.valueOf(bonds.get(i).getNumber());
            if (bondNumber.isEmpty() || bondNumber.isBlank())
            {
                bondNumber = "Не установлена";
            }

            stringBuffer.append("%s. запись в таблице номер=%s ISIN=%s\n".formatted(
                    String.valueOf(i + 1),
                    bondNumber,
                    bonds.get(i).getIsin()
            ));
        }
        stringBuffer.append("Дальнейшее обновление таблицы невозможно, исправьте дубликацию.");

        return stringBuffer.toString();
    }

    public static String invalidIsin(String sheetName, List<Bond> bonds)
    {
        StringBuffer stringBuffer = new StringBuffer("[ПРЕДУПРЕЖДЕНИЕ]\n");
        stringBuffer.append("Обнаружены некорректные ISIN коды в таблице: %s isin:\n"
                .formatted(sheetName)
        );

        for(int i = 0; i < bonds.size(); i++)
        {
            String bondNumber = String.valueOf(bonds.get(i).getNumber());
            if (bondNumber.isEmpty() || bondNumber.isBlank())
            {
                bondNumber = "Не установлена";
            }

            stringBuffer.append("%s. запись в таблице номер=%s наименование эмитента=%s наименование бумаги=%s\n".formatted(
                    String.valueOf(i + 1),
                    bondNumber,
                    bonds.get(i).getIssuerName(),
                    bonds.get(i).getPaperName()
            ));
        }
        stringBuffer.append("Эти записи не будут обновлены, ISIN код будет заменен на пустое поле.");
        return stringBuffer.toString();
    }
}
