package ru.voskhod.excel;

import java.util.*;
import java.util.function.BiFunction;

public class ExcelService {
    private static final int maxIterations = 50;

    private static final Map<String, Integer> operations = new HashMap<>() {{
        put("(", 0);
        put(")", 0);
        put("-", 1);
        put("+", 1);
        put("*", 2);
        put("/", 2);
    }};

    private static final Map<String, BiFunction<Double, Double, Double>> functions = new HashMap<>() {{
        put("-", (a, b) -> a - b);
        put("+", (a, b) -> a + b);
        put("*", (a, b) -> a * b);
        put("/", (a, b) -> a / b);
    }};

    // TODO вынести на фронт
    private static String normalize(String expr) {
        return expr.replaceAll("/\\s/g", "");
    }

    private static TokenizeResult tokenize(String expr) throws Exception {
        ArrayList<Integer> refs = new ArrayList<>();
        ArrayList<String> tokens = new ArrayList<>();

        String token = "";

        for (String ch : expr.split("")) {
            // если символ - знак операции
            if (operations.containsKey(ch)) {
                if (token.length() > 0) {
                    // то сохраняем токен считанного операнда и токен операции
                    tokens.add(token);

                    // если операнд -  не число а ссылка, сохраняем ссылку
                    try {
                        Double.parseDouble(token);
                    } catch (NumberFormatException e) {
                        refs.add(tokens.size() - 1);
                    }

                    tokens.add(ch);
                    token = "";

                // если перед знаком не шло число, то это либо начало выражения, либо ранее был символ операции
                } else {
                    if (tokens.size() == 0 || tokens.get(tokens.size() - 1).equals("(")) {
                        switch (ch) {
                            case "-":
                                tokens.add("0");
                                tokens.add(ch);
                                break;
                            case "+":
                                break;
                            case "(":
                                tokens.add(ch);
                                break;
                            case ")":
                                throw new Exception("Закрывающая скобка расположена некорректно");
                            default:
                                throw new Exception(String.format("Неопознанный унарный оператор: %s", ch));
                        }

                    // если ранее шла закрывающая скобка
                    } else if (tokens.get(tokens.size() - 1).equals(")")) {
                        tokens.add(ch);

                    // если ранее шла операция (кроме скобок)
                    } else if (operations.containsKey(tokens.get(tokens.size() - 1))) {
                        switch (ch) {
                            case "(":
                                tokens.add(ch);
                                break;
                            case ")":
                                throw new Exception("Закрывающая скобка расположена некорректно");
                            default:
                                throw new Exception("Два или более оператора подряд");
                        }
                    // если ранее шли числа
                    } else {
                        tokens.add(ch);
                    }
                }
            } else {
                token += ch;
            }
        }
        if (token.length() > 0) {
            tokens.add(token);

            try {
                Double.parseDouble(token);
            } catch (NumberFormatException e) {
                refs.add(tokens.size() - 1);
            }

        }
        return new TokenizeResult(refs, tokens);
    }

    private static ArrayList<String> convertToRPN(ArrayList<String> inputQueue) throws Exception {
        Deque<String> stack = new ArrayDeque<>();
        ArrayList<String> outputQueue = new ArrayList<>();

        for (String token : inputQueue) {
            switch (token) {
                case "(":
                    stack.push(token);
                    break;
                case ")":
                    // пока на вершине стека не "("
                    while (stack.size() == 0 || !stack.peek().equals("(")) {
                        String last = stack.peek();
                        if (last != null) {
                            outputQueue.add(stack.pop());
                        } else {
                            throw new Exception("В выражении либо неверно поставлен разделитель, либо не согласованы скобки");
                        }
                    }
                    // удаляем из стека открывающую скобку, но никуда не кладем
                    stack.pop();
                    break;
                default:
                    if (operations.containsKey(token)) {
                        if (stack.size() > 0) {
                            while (true) {
                                String topOfStack = stack.peek();
                                if ((topOfStack != null) && (operations.get(topOfStack) >= operations.get(token))) {
                                    outputQueue.add(stack.pop());
                                } else {
                                    break;
                                }
                            }
                        }
                        stack.push(token);
                    } else {
                        outputQueue.add(token);
                    }
                    break;

            }
        }

        outputQueue.addAll(stack);
        return outputQueue;

    }

    private static String compute(ArrayList<String> expr) {
        do {
            for (String token : expr) {
                if (functions.containsKey(token)) {

                    Double opLeft = Double.valueOf(expr.get(expr.indexOf(token) - 2));
                    Double opRight = Double.valueOf(expr.get(expr.indexOf(token) - 1));
                    Double result = functions.get(token).apply(opLeft, opRight);

                    // Удаляем операнды и операцию из массива
                    expr.remove(0);
                    expr.remove(0);
                    expr.remove(0);

                    // Помещаем вместо них результат вычисления
                    expr.add(0, result.toString());
                    break;
                }
            }
        } while (expr.size() != 1);
        return expr.get(0);
    }

    private static int processCells(Map<String, Cell> preparedTable) {
        int doneCellsCounter = 0;

        for (Map.Entry<String, Cell> cellItem : preparedTable.entrySet()) {
            Cell cell = cellItem.getValue();

            if (cell.getStatus() == Status.PROCESSING) {

                // итерация по массиву, который может уменьшаться
                for (int i = 0; i < cell.getRefs().size();) {
                    String refElement = cell.getExpr().get(cell.getRefs().get(i));
                    Cell referenceCell = preparedTable.get(refElement);
                    if (referenceCell == null) {
                        cell.setErrorMsg("Ссылка на несуществующую ячейку, либо неизвестный символ");
                        cell.setStatus(Status.ERROR);
                        break;
                    } else {
                        if (refElement.equals(cellItem.getKey())) {
                            cell.setErrorMsg("Ошибка рекурсии");
                            cell.setStatus(Status.ERROR);
                        }
                        if (referenceCell.getStatus() == Status.DONE) {
                            // меняем ссылку на число, удаляем ссылку
                            cell.getExpr().set(cell.getRefs().get(i), referenceCell.getResult());


                            // удаляем первый элемент массива, счетчик не инкрементируем - на следующей итерации
                            // будет обрабатываться первый элемент уже измененного массива
                            cell.getRefs().remove(0);

                            if (cell.getRefs().size() == 0) {

                                try {
                                    cell.setExprPRN(convertToRPN(cell.getExpr()));
                                    cell.setResult(compute(cell.getExprPRN()));
                                    cell.setStatus(Status.DONE);
                                } catch (Exception e) {
                                    cell.setErrorMsg(e.getMessage());
                                    cell.setStatus(Status.ERROR);
                                }

                                doneCellsCounter++;
                            }
                        } else {
                            i++;
                        }
                    }
                }
            } else {
                doneCellsCounter++;
            }

        }
        return doneCellsCounter;
    }

    private static Map<String, Cell> parseTable(Map<String, String> table) {
        Map<String, Cell> result = new HashMap<>();
        for (Map.Entry<String, String> element: table.entrySet()) {
            Cell currentCell = new Cell();

            try {
                String exprRaw = element.getValue();
                TokenizeResult tokenizeResult = tokenize(exprRaw);
                currentCell.setRefs(tokenizeResult.getRefs());
                currentCell.setExpr(tokenizeResult.getTokens());

                if (currentCell.getRefs().size() == 0) {
                    currentCell.setExprPRN(convertToRPN(currentCell.getExpr()));
                    currentCell.setResult(compute(currentCell.getExprPRN()));
                    currentCell.setStatus(Status.DONE);
                }
            } catch (Exception e) {
                currentCell.setStatus(Status.ERROR);
                currentCell.setErrorMsg(e.getMessage());
            }
            result.put(element.getKey(), currentCell);
        }

        return result;

    }

    public static Map<String, ResultCell> computeTable(Map<String, String> inputTable) {
        Map<String, Cell> parsedTable = parseTable(inputTable);
        Map<String, ResultCell> result = new HashMap<>();

        for (int i = 0; i < maxIterations; i++) {
            if (processCells(parsedTable) == parsedTable.size()) {
                for (Map.Entry<String, Cell> cellItem : parsedTable.entrySet()) {
                    String cellName = cellItem.getKey();
                    Cell cell = cellItem.getValue();

                    packResults(result, cellName, cell);
                }

                return result;
            }
        }

        for (Map.Entry<String, Cell> cellItem : parsedTable.entrySet()) {
            String cellName = cellItem.getKey();
            Cell cell = cellItem.getValue();
            if (cell.getStatus() == Status.PROCESSING) {
                cell.setStatus(Status.ERROR);
                cell.setErrorMsg("Ошибка рекурсии");
                ResultCell resultCell = new ResultCell(cell.getStatus(), cell.getErrorMsg());
                result.put(cellName, resultCell);
            } else {
                packResults(result, cellName, cell);
            }


        }

        return result;
    }

    private static void packResults(Map<String, ResultCell> result, String cellName, Cell cell) {
        if (cell.getStatus() == Status.DONE) {
            ResultCell resultCell = new ResultCell(cell.getStatus(), Double.parseDouble(cell.getResult()));
            result.put(cellName, resultCell);
        } else {
            ResultCell resultCell = new ResultCell(cell.getStatus(), cell.getErrorMsg());
            result.put(cellName, resultCell);
        }
    }


}
