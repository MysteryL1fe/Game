package utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Utils {
    public static String[] readLinesFromFile(String fileName) {
        List<String> lines;
        try (Scanner scanner = new Scanner(new File(fileName), "UTF-8")) {
            lines = new ArrayList<>();
            while (scanner.hasNext()) {
                lines.add(scanner.nextLine());
            }
            // обязательно, чтобы закрыть открытый файл
        } catch (FileNotFoundException e) {
            lines = new ArrayList<>();
        }
        return lines.toArray(new String[0]);
    }

    public static String stringArrToLine(String[] strArr) {
        StringBuilder result = new StringBuilder();
        for (String string : strArr) {
            result.append(string).append("\n");
        }
        return result.toString();
    }
}
