package ru.voskhod;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import ru.voskhod.excel.ResultCell;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class JsonUtils {
    static Map<String, String> parseRequest(String json) throws JsonProcessingException {
        Map<String, String> parseResult = new HashMap<>();
        ObjectMapper mapper = new ObjectMapper();
        Request[] cells = mapper.readValue(json, Request[].class);

        for (Request cell : cells) {
            parseResult.put(cell.getName(), cell.getValue());
        }

        return parseResult;
    }

    static String writeResponse(Map<String, ResultCell> result) throws JsonProcessingException {
        List<Response> response = new ArrayList<>();
        for (Map.Entry<String, ResultCell> entry : result.entrySet()) {
            Response resp = new Response();
            resp.setName(entry.getKey());
            resp.setResult(entry.getValue());
            response.add(resp);
        }
        ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsString(response);
    }
}
