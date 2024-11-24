package mapper;

import app.mapper.RusbondMapper;
import app.model.BondRepayment;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class RusbondMapperIT
{
    ObjectMapper objectMapper = new ObjectMapper();

    @Test
    public void getListRepaymentDate() throws IOException
    {
        //given
        File file = new File("./src/test/resources/calendarList");
        //then
        List<BondRepayment> repayments = RusbondMapper.repayments(objectMapper.readTree(file));

        //expected
        for(BondRepayment bondRepayment : repayments)
        {
            System.out.printf("%s %s \n",
                    bondRepayment.getMtyDate(),
                    bondRepayment.getMtyPart()
            );
        }
    }
}
