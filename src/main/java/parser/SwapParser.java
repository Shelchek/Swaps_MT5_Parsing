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

    public static class Symbol {
        private final String symbolName;
        private final double swapLong;
        private final double swapShort;
        private final String swap3Day;

        public Symbol(String symbolName, double swapLong, double swapShort, int swap3Day) {
            this.symbolName = symbolName;
            this.swapLong = swapLong;
            this.swapShort = swapShort;
            this.swap3Day = dayOfWeek(swap3Day);
        }
        private String dayOfWeek (int dayNumber) throws IllegalArgumentException {
             switch (dayNumber) {
                case 1:
                    return "Monday";
                 case 2:
                     return "Tuesday";
                 case 3:
                     return  "Wednesday";
                 case 4:
                     return "Thursday";
                 case 5:
                     return "Friday";
                 case 6:
                     return "Saturday";
                 case 7:
                     return  "Sunday";
                 default:
                    System.out.println("Wrong number of \"Swap3Day\" for Symbol: "+symbolName);
                    throw new IllegalArgumentException();
                }

        }
//Creates the csv formatted string
        @Override
        public String toString() {
            return symbolName + "," + swapLong + "," + swapShort + "," + swap3Day +"\n";
        }
    }


}
