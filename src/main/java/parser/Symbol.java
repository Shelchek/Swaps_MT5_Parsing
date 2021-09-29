package parser;

public class Symbol {
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

    public String getSymbolName() {
        return symbolName;
    }

    public double getSwapLong() {
        return swapLong;
    }

    public double getSwapShort() {
        return swapShort;
    }

    public String getSwap3Day() {
        return swap3Day;
    }

    //Creates the csv formatted string
    @Override
    public String toString() {
        return symbolName + "," + swapLong + "," + swapShort + "," + swap3Day +"\n";
    }
}
