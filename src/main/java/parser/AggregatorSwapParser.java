package parser;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AggregatorSwapParser {
    private final List<String> parsedResult = new ArrayList<>();
    private final Map<String, String> symbolNameException = new HashMap<>();

    public AggregatorSwapParser() {
        symbolNameException.put("NK1JPY", "NKYJPY");
        symbolNameException.put("SP1USD", "SPXUSD");
        symbolNameException.put("SP2USD", "SPXUSD");
        symbolNameException.put("DJ2USD", "DJ1USD");
        symbolNameException.put("ND2USD", "ND1USD");
        symbolNameException.put("NK2JPY", "NKYJPY");
    }


    private Path MUpathSymbolFilter() {
        Path path = Paths.get("");
        try {
            path = Paths.get(MT5SwapParser.class.getProtectionDomain().getCodeSource().
                    getLocation().toURI().getPath().substring(1)).getParent();
        } catch (URISyntaxException e) {
            System.out.println("Something went wrong with resolving path to the MT5Symbol_Filter.csv file");
        }
        return Paths.get(path + "\\Aggregator\\MU_Symbols_filter.csv");
    }

    private Path UKpathSymbolFilter() {
        Path path = Paths.get("");
        try {
            path = Paths.get(MT5SwapParser.class.getProtectionDomain().getCodeSource().
                    getLocation().toURI().getPath().substring(1)).getParent();
        } catch (URISyntaxException e) {
            System.out.println("Something went wrong with resolving path to the MT5Symbol_Filter.csv file");
        }
        return Paths.get(path + "\\Aggregator\\UK_Symbol_filter.csv");
    }

    private Path pathSymbolsToParse() {
        Path path = Paths.get("");
        try {
            path = Paths.get(MT5SwapParser.class.getProtectionDomain().getCodeSource().
                    getLocation().toURI().getPath().substring(1)).getParent();
        } catch (URISyntaxException e) {
            System.out.println("Something went wrong with resolving path to the Symbols _to_parse.csv file");
        }
        return Paths.get(path + "\\Aggregator\\Symbols_to_parse.csv");
    }

    void fillParsedResult(String entity) {
        Path pathFilter;
        if (entity.equals("UK")) pathFilter = UKpathSymbolFilter();
        else pathFilter = MUpathSymbolFilter();

        ArrayList<String> symbolFilter = new ArrayList<>();
        if (Files.exists(pathFilter)) {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(Files.newInputStream(pathFilter)))) {
                String s;
                while ((s = reader.readLine()) != null) {
                    symbolFilter.add(s);
                }
            } catch (IOException e) {
                System.out.println("Error happened during reading of" + entity + "_Symbol_filter.csv. Filter won't be applied correctly");
            }
        } else System.out.println("Filter file is not found");

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(Files.newInputStream(pathSymbolsToParse())))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] params = line.split(",");
                for (String symbol : symbolFilter) {
                    if (symbol.contains(params[0]) || symbolNameException.containsKey(symbol) &&
                            symbolNameException.get(symbol).equals(params[0])) {
                        String toAdd = String.format("\"%s\",\"%s\",\"%s\",\"%s\"%n", symbol, params[2], params[3], params[4]);
                        parsedResult.add(toAdd);
                    }
//                    if (symbolNameException.containsKey(symbol) && symbolNameException.get(symbol).equals(params[0])) {
//                        String toAdd = String.format("\"%s\",\"%s\",\"%s\",\"%s\"%n", symbol, params[2], params[3], params[4]);
//                        parsedResult.add(toAdd);
//                    }
                }
            }
        } catch (IOException e) {
            System.out.println("Error happened during reading of Symbols_to_parse.csv.");
        }
    }

    void writeParsedSwaps(String entity) {
        Path path = Paths.get("");
        try {
            path = Paths.get(MT5SwapParser.class.getProtectionDomain().getCodeSource().
                    getLocation().toURI().getPath().substring(1)).getParent();
        } catch (URISyntaxException e) {
            System.out.println("Something went wrong with resolving path to the folder where to store file");
        }
        Path filePath = Paths.get(path + "\\Results\\" + entity + "_swaps_to_import.csv");

        try {
            int count = 1;
            while (Files.exists(filePath)) {
                filePath = Paths.get(path + "\\Results\\" + entity + "_swaps_to_import" + count + ".csv");
                count++;
            }
            Files.createFile(filePath);
        } catch (IOException e) {
            System.out.println("Can't create the file");
        }
        try (BufferedWriter writer = Files.newBufferedWriter(filePath)) {
            writer.write("symbol,shortSwap,longSwap,tripleSwapDay\n");
            for (String symbol : parsedResult) {
                writer.write(symbol);
            }
        } catch (IOException e) {
            System.out.println("Something went wrong with data writing");
        }
    }

}
