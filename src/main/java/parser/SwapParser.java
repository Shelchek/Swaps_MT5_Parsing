package parser;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class SwapParser {
    private static final List<Symbol> SYMBOLS_LIST = new ArrayList<>();

    public static void main(String[] args) throws IOException, InterruptedException, URISyntaxException {
        ObjectMapper mapper = new ObjectMapper();
        Path jsonFile = InputOutput.pathForJson();
        JsonNode node = mapper.readTree(jsonFile.toFile());
        try {
            fillSymbolsList(node);
        } catch (NullPointerException e) {
            System.out.println("Something is wrong with json file");
            Thread.sleep(1000);
            return;
        }
        InputOutput.writeParsedSwaps(SYMBOLS_LIST);
        System.out.println("All work is done. Bye Bye!");
        Thread.sleep(1000);
    }

    private static Symbol createSymbolObj (JsonNode node) {
        return new Symbol(node.get("Symbol").asText(), node.get("SwapLong").asDouble(),
                node.get("SwapShort").asDouble(), node.get("Swap3Day").asInt());
    }

    private static void fillSymbolsList (JsonNode node) throws NullPointerException{
        ArrayList<String> symbolFilter = new ArrayList<>();
        if (Files.exists(pathSymbolFilter())) {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(Files.newInputStream(pathSymbolFilter())))){
                String s;
                while ((s = reader.readLine()) != null) {
                    symbolFilter.add(s);
                }
            } catch (IOException e) {
                System.out.println("Error happened during reading of Symbol_Filter.csv. Filter won't be applied correctly");
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
//Creating path for the Symbol_Filter.csv file in the folder where the jar is stored
    private static Path pathSymbolFilter () {
        Path path = Paths.get("");
        try {
            path = Paths.get(SwapParser.class.getProtectionDomain().getCodeSource().
                    getLocation().toURI().getPath().substring(1)).getParent();
        } catch (URISyntaxException e) {
            System.out.println("Something went wrong with resolving path to the Symbol_Filter.csv file");
        }
        return Paths.get(path +"\\"+ "Symbol_Filter.csv");
    }
}
