package parser;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class SwapParser {
    private static final List<Symbol> SYMBOLS_LIST = new ArrayList<>();

    public static void main(String[] args) throws IOException, InterruptedException {
        ObjectMapper mapper = new ObjectMapper();
        Path jsonFile = InputOutput.pathForJson();
        JsonNode node = mapper.readTree(jsonFile.toFile());
        try {
            fillSymbolsList(node);
        } catch (NullPointerException e) {
            System.out.println("Something wrong with json file");
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
        node.get("Server").elements().forEachRemaining(x -> x.get("ConfigSymbols").forEach(g -> SYMBOLS_LIST.add(createSymbolObj(g))));
    }



}
