package parser;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

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

public class MT5SwapParser {
    protected static final List<MT5Symbol> SYMBOLS_LIST = new ArrayList<>();

    private static MT5Symbol createSymbolObj (JsonNode node) {
        return new MT5Symbol(node.get("Symbol").asText(), node.get("SwapLong").asDouble(),
                node.get("SwapShort").asDouble(), node.get("Swap3Day").asInt());
    }

    static void fillSymbolsList(JsonNode node) throws NullPointerException{
        ArrayList<String> symbolFilter = new ArrayList<>();
        if (Files.exists(pathSymbolFilter())) {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(Files.newInputStream(pathSymbolFilter())))){
                String s;
                while ((s = reader.readLine()) != null) {
                    symbolFilter.add(s);
                }
            } catch (IOException e) {
                System.out.println("Error happened during reading of MT5Symbol_Filter.csv. Filter won't be applied correctly");
            }
        }
        node.get("Server").elements().forEachRemaining(x -> x.get("ConfigSymbols").forEach(g -> {
            if (!symbolFilter.isEmpty() && symbolFilter.contains(g.get("Symbol").asText())) {
                SYMBOLS_LIST.add(createSymbolObj(g));
            }
            else if (symbolFilter.isEmpty()) SYMBOLS_LIST.add(createSymbolObj(g));
        }));
        if (symbolFilter.isEmpty()) System.out.println("Filter wasn't applied");
    }

//Creating path for the MT5Symbol_Filter.csv file in the folder where the jar is stored
   private static Path pathSymbolFilter () {
        Path path = Paths.get("");
        try {
            path = Paths.get(MT5SwapParser.class.getProtectionDomain().getCodeSource().
                    getLocation().toURI().getPath().substring(1)).getParent();
        } catch (URISyntaxException e) {
            System.out.println("Something went wrong with resolving path to the MT5Symbol_Filter.csv file");
        }
        return Paths.get(path +"\\MT5_Swaps\\MT5Symbol_Filter.csv");
    }

    static Path pathJsonFile () {
        Path path = Paths.get("");
        try {
            path = Paths.get(MT5SwapParser.class.getProtectionDomain().getCodeSource().
                    getLocation().toURI().getPath().substring(1)).getParent();
        } catch (URISyntaxException e) {
            System.out.println("Something went wrong with resolving path to the CMAPltd-Live.json file");
        }
        return Paths.get(path +"\\MT5_Swaps\\CMAPltd-Live.json");
    }

    static void writeParsedSwaps (List<MT5Symbol> symbols){
        Path path = Paths.get("");
        try {
            path = Paths.get(MT5SwapParser.class.getProtectionDomain().getCodeSource().
                    getLocation().toURI().getPath().substring(1)).getParent();
        } catch (URISyntaxException e) {
            System.out.println("Something went wrong with resolving path to the folder where to store file");
        }
        Path filePath = Paths.get(path + "\\MT5_Swaps\\MT5 Symbols and swaps.csv");
        try {
            int count = 1;
            while (Files.exists(filePath)) {
                filePath = Paths.get(path + "\\MT5_Swaps\\MT5 Symbols and swaps_" + count + ".csv");
                count++;
            }
            Files.createFile(filePath);
        } catch (IOException e) {
            System.out.println("Can't create the file");
        }
        try(BufferedWriter writer = Files.newBufferedWriter(filePath)) {
            writer.write("sep=,\n");
            writer.write("Symbol,Swap Long,Swap Short,3-days swap\n");
            for (MT5Symbol symbol : symbols) {
                writer.write(symbol.toString());
            }
        } catch (IOException e) {
            System.out.println("Something went wrong with data writing");
        }
    }
}
