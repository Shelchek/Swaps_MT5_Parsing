package parser;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class InputOutput {
    private static final BufferedReader READ_FROM_CONSOLE = new BufferedReader(new InputStreamReader(System.in));

    private static String askToEnter() {
        String line;
        while (true) {
            try {
                line = READ_FROM_CONSOLE.readLine();
                return line;
            } catch (IOException e) {
                System.out.println("Something went wrong. Please enter one more time");
            }
        }
    }

    public static Path pathForJson () {
        Path path;
        System.out.println("Enter the path for json file");
        while (true) {
            String fromConsole = askToEnter().replaceAll("\"","");
            if (fromConsole.endsWith("json")) {
                try {
                    path = Paths.get(fromConsole);
                    if (Files.exists(path) && Files.isRegularFile(path)) {
                        return path;
                    }
                } catch (InvalidPathException e) {
                    System.out.println("Wrong path format");
                }

            }
            System.out.println("File either doesn't exist or that is not a json. Check and enter the path again");
        }
    }

    public static Path pathWhereToStore () {
        Path path;
        System.out.println("Enter the path for folder where to store the parsed data");
        while (true) {
            String fromConsole = askToEnter().replaceAll("\"","");
            try {
                path = Paths.get(fromConsole);
                if (Files.exists(path) && Files.isDirectory(path)) {
                    return path;
                }
            } catch (InvalidPathException e) {
                System.out.println("Wrong path format");
            }
            System.out.println("Folder either doesn't exist or that is not a folder. Check and enter the path again");
        }
    }

    public static void writeParsedSwaps (List<Symbol> symbols){
        String folder = pathWhereToStore().toString();
        Path path = Paths.get(folder + "\\MT5 Symbols and swaps.csv");
        int count = 1;
        while (Files.exists(path)) {
            path = Paths.get(folder + "\\MT5 Symbols and swaps_" + count + ".csv");
            count++;
        }
        try {
            Files.createFile(path);
        } catch (IOException e) {
            System.out.println("Can't create the file");
        }
        try(BufferedWriter writer = Files.newBufferedWriter(path)) {
            writer.write("sep=,\n");
            writer.write("Symbol,Swap Long,Swap Short,3-days swap\n");
            for (Symbol symbol : symbols) {
                writer.write(symbol.toString());
            }
        } catch (IOException e) {
            System.out.println("Something went wrong with data writing");
        }
    }
}
