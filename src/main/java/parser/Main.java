package parser;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Path;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        while (true) {
            System.out.println("Enter the number of operation:");
            System.out.println("1 -> MT5 Swaps parse");
            System.out.println("2 -> MT4 Swaps parse");
            System.out.println("3 -> UK aggregator parse");
            System.out.println("4 -> MU aggregator parse");
            System.out.println("5 -> All operations above");
            System.out.println("0 -> Exit");
            int number;
            try {
                number = Integer.parseInt(reader.readLine());
                if (number == 1) MT5Parse();
                else if (number == 2) MT4Parse();
                else if(number == 3) aggrParse(3);
                else if (number == 4) aggrParse(4);
                else if (number == 5) {
                    MT4Parse();
                    MT5Parse();
                    aggrParse(3);
                    aggrParse(4);
                }
                else if (number == 0) {
                    System.out.println("See you next time. Bye Bye!");
                    Thread.sleep(1000);
                    break;
                }
                else System.out.println("Unknown operation's number. Try to aim 0-1 range. It's not that hard");

            } catch (NumberFormatException e) {
                System.out.println("That's not a number. Try better");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static void MT5Parse () throws IOException, InterruptedException {
        MT5SwapParser parser = new MT5SwapParser();
        ObjectMapper mapper = new ObjectMapper();
        Path jsonFile = parser.pathJsonFile();
        JsonNode node = mapper.readTree(jsonFile.toFile());
        try {
            parser.fillSymbolsList(node);
        } catch (NullPointerException e) {
            System.out.println("Something is wrong with json file");
            Thread.sleep(1000);
            return;
        }
        parser.writeParsedSwaps();
        System.out.println("MT5 Swaps are parsed\n");
    }

    private static void MT4Parse () {
        MT4SwapParser parser = new MT4SwapParser();
        parser.fillSymbolsList();
        parser.writeParsedSwaps();
        System.out.println("MT4 Swaps are parsed\n");
    }

    private static void aggrParse(int ent) {
        String entity;
        if (ent == 3) entity = "UK";
        else entity = "MU";
        AggregatorSwapParser parser = new AggregatorSwapParser();
        parser.fillParsedResult(entity);
        parser.writeParsedSwaps(entity);
        System.out.println("Aggregator's " + entity + " swaps are parsed. File for import is created\n");
    }
}
