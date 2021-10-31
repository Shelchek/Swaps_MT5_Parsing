package parser;

import com.fasterxml.jackson.databind.JsonNode;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class MT4SwapParser {
    static final List<String> PARSED_RESULT = new ArrayList<>();

    private static Path pathSymbolFilter() {
        Path path = Paths.get("");
        try {
            path = Paths.get(MT5SwapParser.class.getProtectionDomain().getCodeSource().
                    getLocation().toURI().getPath().substring(1)).getParent();
        } catch (URISyntaxException e) {
            System.out.println("Something went wrong with resolving path to the MT5Symbol_Filter.csv file");
        }
        return Paths.get(path + "\\MT4_Swaps\\MT4Symbol_Filter.csv");
    }

    static Path pathSymbolsToParse() {
        Path path = Paths.get("");
        try {
            path = Paths.get(MT5SwapParser.class.getProtectionDomain().getCodeSource().
                    getLocation().toURI().getPath().substring(1)).getParent();
        } catch (URISyntaxException e) {
            System.out.println("Something went wrong with resolving path to the SymbolsToParse.txt file");
        }
        return Paths.get(path + "\\MT4_Swaps\\SymbolsToParse.txt");
    }

    static void fillSymbolsList() {
        ArrayList<String> symbolFilter = new ArrayList<>();
        if (Files.exists(pathSymbolFilter())) {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(Files.newInputStream(pathSymbolFilter())))) {
                String s;
                while ((s = reader.readLine()) != null) {
                    symbolFilter.add(s);
                }
            } catch (IOException e) {
                System.out.println("Error happened during reading of MT5Symbol_Filter.csv. Filter won't be applied correctly");
            }
        }
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(Files.newInputStream(pathSymbolsToParse())))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] params = line.split("\t");
                if (symbolFilter.contains(params[0])) {
                    String toAdd = params[0] + "," + params[6] + "," + params[7] + "\n";
                    PARSED_RESULT.add(toAdd);
                }
            }
        } catch (IOException e) {
            System.out.println("Error happened during reading of SymbolsToParse.txt.");
        }
    }

    static void writeParsedSwaps() {
        Path path = Paths.get("");
        try {
            path = Paths.get(MT5SwapParser.class.getProtectionDomain().getCodeSource().
                    getLocation().toURI().getPath().substring(1)).getParent();
        } catch (URISyntaxException e) {
            System.out.println("Something went wrong with resolving path to the folder where to store file");
        }
        Path filePath = Paths.get(path + "\\MT4_Swaps\\MT4 Symbols and swaps.csv");
        try {
            int count = 1;
            while (Files.exists(filePath)) {
                filePath = Paths.get(path + "\\MT4_Swaps\\MT4 Symbols and swaps_" + count + ".csv");
                count++;
            }
            Files.createFile(filePath);
        } catch (IOException e) {
            System.out.println("Can't create the file");
        }
        try (BufferedWriter writer = Files.newBufferedWriter(filePath)) {
            writer.write("sep=,\n");
            writer.write("Symbol,Long,Short\n");
            for (String symbol : PARSED_RESULT) {
                writer.write(symbol);
            }
        } catch (IOException e) {
            System.out.println("Something went wrong with data writing");
        }
    }
}
