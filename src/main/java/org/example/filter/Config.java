package org.example.filter;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class Config {
    boolean shortStat;
    boolean fullStat;
    boolean append;
    Path outputDir = Path.of(".");
    String prefix = "";
    public boolean statsEnabled = false;
    List<String> inputFiles = new ArrayList<>();

}
