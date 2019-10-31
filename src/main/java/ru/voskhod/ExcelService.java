package ru.voskhod;

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

    private static final Map<String, BiFunction<Integer, Integer, Integer>> functions = new HashMap<>() {{
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




}
