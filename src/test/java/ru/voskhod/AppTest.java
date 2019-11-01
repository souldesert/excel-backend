package ru.voskhod;

import static org.junit.Assert.assertTrue;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import ru.voskhod.excel.ExcelService;
import ru.voskhod.excel.ResultCell;

import java.util.HashMap;
import java.util.Map;

/**
 * Unit tests for App.
 */
public class AppTest 
{

    @Test
    public void computeSimpleTable() {
        Map<String, String> sample = new HashMap<>(){{
            put("A1", "(7.5-3)*4");
            put("B1", "A1-1");
            put("A2", "A1+B1");
            put("B2", "A2-1");
        }};

        Map<String, ResultCell> result = ExcelService.computeTable(sample);
        for (Map.Entry<String, ResultCell> res : result.entrySet()) {
            System.out.println(res.getKey() + " " + res.getValue().getValue());
        }
    }

    @Test
    public void readRequestBodyFromJson() throws JsonProcessingException {
        String json = """
            [
                {
                "name": "A1",
                "value": "7"
                },
                {
                "name": "B1",
                "value": "A1-1"
                },
                {
                "name": "A2",
                "value": "A1+B1"
                },
                {
                "name": "B2",
                "value": "A2-1"
                }
            ]
            """;

        ObjectMapper mapper = new ObjectMapper();
        Request[] cells = mapper.readValue(json, Request[].class);
        System.out.println("Finished");
    }

}
