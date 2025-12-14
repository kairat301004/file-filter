package org.example.filter;

import java.io.*;
import java.nio.file.*;
import java.util.EnumMap;
import java.util.Map;

public class FileProcessor {

    private final Config config;
    private final Map<DataType, Statistics> stats = new EnumMap<>(DataType.class);
    private final Map<DataType, BufferedWriter> writers = new EnumMap<>(DataType.class);

    public FileProcessor(Config config) {
        this.config = config;
        for (DataType type : DataType.values()) {
            stats.put(type, new Statistics());
        }
    }

    public void process() {
        for (String file : config.inputFiles) {
            try (BufferedReader reader = Files.newBufferedReader(Path.of(file))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    handleLine(line);
                }
            } catch (IOException e) {
                System.err.println("Ошибка чтения файла: " + file + " — " + e.getMessage());
            }
        }
        closeWriters();
        printStats();
    }

    private void handleLine(String line) {
        try {
            long l = Long.parseLong(line);
            write(DataType.INTEGER, line);
            stats.get(DataType.INTEGER).addNumber(l);
            return;
        } catch (NumberFormatException ignored) {}

        try {
            double d = Double.parseDouble(line);
            write(DataType.FLOAT, line);
            stats.get(DataType.FLOAT).addNumber(d);
            return;
        } catch (NumberFormatException ignored) {}

        write(DataType.STRING, line);
        stats.get(DataType.STRING).addString(line);
    }

    private void write(DataType type, String value) {
        try {
            BufferedWriter writer = writers.computeIfAbsent(type, this::createWriter);
            writer.write(value);
            writer.newLine();
        } catch (IOException e) {
            System.err.println("Ошибка записи: " + e.getMessage());
        }
    }

    private BufferedWriter createWriter(DataType type) {
        try {
            String name = config.prefix + switch (type) {
                case INTEGER -> "integers.txt";
                case FLOAT -> "floats.txt";
                case STRING -> "strings.txt";
            };
            Path path = config.outputDir.resolve(name);
            return Files.newBufferedWriter(path,
                    config.append ? StandardOpenOption.CREATE : StandardOpenOption.CREATE,
                    config.append ? StandardOpenOption.APPEND : StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void closeWriters() {
        writers.values().forEach(w -> {
            try { w.close(); } catch (IOException ignored) {}
        });
    }

    private void printStats() {
        if (!config.statsEnabled) return;
        for (var entry : stats.entrySet()) {
            Statistics s = entry.getValue();
            if (s.count == 0) continue;

            System.out.println(entry.getKey() + ":");
            System.out.println("  count = " + s.count);

            if (config.fullStat) {
                if (entry.getKey() != DataType.STRING) {
                    System.out.println("  min = " + s.min);
                    System.out.println("  max = " + s.max);
                    System.out.println("  sum = " + s.sum);
                    System.out.println("  avg = " + (s.sum / s.count));
                } else {
                    System.out.println("  min length = " + s.minLength);
                    System.out.println("  max length = " + s.maxLength);
                }
            }
        }
    }
}

