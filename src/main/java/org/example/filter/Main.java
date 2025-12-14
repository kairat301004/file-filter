package org.example.filter;

public class Main {

    public static void main(String[] args) {
        Config config = parseArgs(args);
        if (config.inputFiles.isEmpty()) {
            System.err.println("Не указаны входные файлы");
            return;
        }
        new FileProcessor(config).process();
    }

    private static Config parseArgs(String[] args) {
        Config c = new Config();
        for (int i = 0; i < args.length; i++) {
            switch (args[i]) {
                case "-s" -> {
                    c.shortStat = true;
                    c.statsEnabled = true;
                }
                case "-f" -> {
                    c.fullStat = true;
                    c.statsEnabled = true;
                }
                case "-a" -> c.append = true;
                case "-o" -> c.outputDir = java.nio.file.Path.of(args[++i]);
                case "-p" -> c.prefix = args[++i];
                default -> c.inputFiles.add(args[i]);
            }
        }
        return c;
    }

}