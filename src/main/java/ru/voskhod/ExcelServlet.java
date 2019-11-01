package ru.voskhod;
import ru.voskhod.excel.ExcelService;
import ru.voskhod.excel.ResultCell;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;
import java.util.stream.Collectors;

public class ExcelServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String body = req.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
        Map<String, String> table = JsonUtils.parseRequest(body);

        Map<String, ResultCell> result = ExcelService.computeTable(table);

        String response = JsonUtils.writeResponse(result);

        resp.addHeader("Content-Type", "application/json");
        resp.getWriter().write(response);
    }

}
