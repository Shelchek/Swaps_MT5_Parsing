package parser;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Path;

public class Main {
    public static void main(String[] args) throws IOException, InterruptedException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        while (true) {
            System.out.println("Enter the number of operation:");
            System.out.println("1 -> MT5 Swaps parse");
            System.out.println("2 -> MT4 Swaps parse");
            System.out.println("0 -> Exit");
            int number;
            try {
                number = Integer.parseInt(reader.readLine());
                if (number == 1) MT5Parse();
                else if (number == 2) MT4Parse();
                else if (number == 0) {
                    System.out.println("See you next time. Bye Bye!");
                    Thread.sleep(1000);
                    break;
                }
                else System.out.println("Unknown operation's number. Try to aim 0-1 range. It's not that hard");

            } catch (NumberFormatException e) {
                System.out.println("That it's not a number. Try better");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static void MT5Parse () throws IOException, InterruptedException {
        ObjectMapper mapper = new ObjectMapper();
        Path jsonFile = MT5SwapParser.pathJsonFile();
        JsonNode node = mapper.readTree(jsonFile.toFile());
        try {
            MT5SwapParser.fillSymbolsList(node);
        } catch (NullPointerException e) {
            System.out.println("Something is wrong with json file");
            Thread.sleep(1000);
            return;
        }
        MT5SwapParser.writeParsedSwaps(MT5SwapParser.SYMBOLS_LIST);
        System.out.println("MT5 Swaps are parsed");
    }

    private static void MT4Parse () {
        MT4SwapParser.fillSymbolsList();
        MT4SwapParser.writeParsedSwaps();
        System.out.println("MT4 Swaps are parsed");
    }
}
