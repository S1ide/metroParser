import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class Metro {
    private static final ArrayList<Line> lines = new ArrayList<>();
    private static int count = 0;

    public static void getJsonFromMetro(String file) {
        try {
            Document document = Jsoup.connect("https://www.moscowmap.ru/metro.html#lines")
                    .maxBodySize(0) // получаем экземпляр класса Document с сайта
                    .userAgent("Chrome/81.0.4044.138")
                    .referrer("http://www.google.com")
                    .get();
            Elements linesName = document.select("span.js-metro-line");
            Elements stationName = document.select("div.js-metro-stations");
            for (Element line : linesName){
                Line lineForAdd = new Line(line.attr("data-line"), line.text());
                lineForAdd.setStations(getStations(lineForAdd, stationName));
                lines.add(lineForAdd);
            }
            
            FileWriter writer = new FileWriter(file);
            JSONObject output = new JSONObject(), stations = new JSONObject();
            lines.forEach(line -> {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("name", line.getNameOfLine());
                jsonObject.put("stations", line.getStations());
                stations.put(line.getNumber(), jsonObject);
            });
            output.put("stations", stations);

            JSONArray connections = new JSONArray();
            lines.forEach(line -> {
                JSONObject object = new JSONObject();
                object.put("number", line.getNumber());
                object.put("name", line.getNameOfLine());
                connections.add(object);
            });
            output.put("connections", connections);
            writer.write(output.toJSONString());
            writer.flush();

        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    public static String getStringFromJson(String input){
        try {
            JSONParser jsonParser = new JSONParser();
            StringBuilder builder = new StringBuilder();
            Files.readAllLines(Paths.get(input)).forEach(builder::append);
            JSONObject jsonObject = (JSONObject)jsonParser.parse(builder.toString());
            var stations = (HashMap<String, HashMap<String, ArrayList<String>>>)jsonObject.get("stations");
            var o = stations.keySet();
            stations.keySet().stream().sorted(new StringLengthSort()).forEach(s -> {
                System.out.printf("Станция номер %s —> %s\n", s, stations.get(s).get("stations").size());
            });
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return null;
    }

    public static ArrayList<String> getStations(Line line, Elements listOfStations){
        ArrayList<String> result = new ArrayList<>();
        for (Element stations : listOfStations){
            if (stations.attr("data-line").equals(line.getNumber())){
                stations.select("span.name").forEach(element -> result.add(element.text()));
            }
        }
        return result;
    }

    static class StringLengthSort implements Comparator<String>{
        @Override
        public int compare(String o1, String o2) {
            if(o1.length() > o2.length()){
                return 1;
            }else{
                if(o1.length() < o2.length()){
                    return -1;
                }else{
                    return o1.compareTo(o2);
                }
            }
        }
    }
}
