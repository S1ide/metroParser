import java.util.ArrayList;


public class Line {
    private ArrayList<String>stations = new ArrayList<>();
    private String number;
    private String nameOfLine;

    private static int count = 1;

    public Line(String number, String nameOfLine) {
        this.nameOfLine = nameOfLine;
        this.number = number;
    }

    public ArrayList<String> getStations() {
        return stations;
    }

    public String getNameOfLine() {
        return nameOfLine;
    }

    public void setStations(ArrayList<String> stations) {
        this.stations = stations;
    }

    public String getNumber() {
        return number;
    }


}
