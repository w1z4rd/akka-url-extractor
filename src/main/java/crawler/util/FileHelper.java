package crawler.util;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

import static java.lang.System.lineSeparator;

public class FileHelper {

    public static void saveResult(String fileName, Map<String, Integer> aggregator) throws IOException {
        Path path = Paths.get(fileName);
        try (BufferedWriter writer = Files.newBufferedWriter(path)) {
            aggregator.entrySet()
                    .parallelStream()
                    .forEach(entry -> {
                        try {
                            writer.write(entry.getKey() + "," + entry.getValue() + lineSeparator());
                        } catch (IOException e) {
                          System.err.println(e.getMessage());
                        }
                    });
        }
      System.out.println("Report saved to " + fileName);
    }
}
