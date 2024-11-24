package app.integration.rusbonds.dto;

public record CalendarListDto(int[] fintoolIds,
                              String[] eventTypes,
                              int pageNum,
                              int pageSize,
                              boolean sortDesc)
{
    public CalendarListDto(int[] fintoolIds, String[] eventTypes)
    {
        this(fintoolIds,
                eventTypes,
                1,
                30,
                false);
    }
}
