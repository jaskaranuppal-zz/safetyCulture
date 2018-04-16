package com.company.safetyculture;

import java.io.*;
import java.util.*;
import java.io.FileReader;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class Main {
    private static Map<String, List<Double>> temperatureMap = new HashMap<>();

    public static void main(String[] args) {

        JSONParser parser = new JSONParser();
        try{

            Object object = parser.parse(new FileReader
                    ("/Users/jaskaranuppal/Downloads/SafetyCulture/src/com/company/safetyculture/data/model.json"));

            JSONArray results = (JSONArray) object;

            for (int i = 0; i < results.size(); i++) {

                JSONObject jsonObjectRow = (JSONObject) results.get(i);
                String id = (String) jsonObjectRow.get("id");
                Double temperature = (Double) jsonObjectRow.get("temperature");
                List<Double> temperatureList = new ArrayList<>();

                if(temperatureMap.containsKey(id)) {
                    temperatureList = temperatureMap.get(id);
                    temperatureList.add(temperature);
                    temperatureMap.put(id, temperatureList);
                }else {
                    temperatureList.add(temperature);
                    temperatureMap.put(id, temperatureList);
                }
            }

        } catch (FileNotFoundException fe){
            fe.printStackTrace();
        }
        catch (Exception e){
            e.printStackTrace();
        }

        JSONArray response = new JSONArray();

        for(Map.Entry<String , List<Double>> entry : temperatureMap.entrySet()){

            String id = entry.getKey().toString();
            Double average = average(entry.getValue());
            Double median = Math.round(median(entry.getValue()) *100.0)/100.0;
            List<Double> modes = modes(entry.getValue());

            LinkedHashMap<String, Object> jsonOrderedMap = new LinkedHashMap<>();
            jsonOrderedMap.put("id",id);
            jsonOrderedMap.put("average", average);
            jsonOrderedMap.put("median", median);
            jsonOrderedMap.put("mode", modes);

            response.add(jsonOrderedMap);

        }
        System.out.println(response.toJSONString());
    }

    public static Double average (List<Double> temperatureList) {
        double sum = 0;
        for (double d : temperatureList) {
            sum += d;
        }
        Double result = sum / temperatureList.size();
        return Math.round(result * 100.0)/100.0;
    }

    public static double median(List<Double> temperatureList) {
        Collections.sort(temperatureList);

        int middle = temperatureList.size()/2;

        if (temperatureList.size() % 2 == 1) {
            return temperatureList.get(middle);
        } else {
            return (temperatureList.get(middle - 1) + temperatureList.get(middle)) / 2.0;
        }
    }

    public static List<Double> modes(List<Double> numbers) {

        List<Double> modes = new ArrayList<>();

        Map<Double, Integer> countMap = new HashMap<>();

        double max = -1;

        for (double n : numbers) {
            int count = 0;

            if (countMap.containsKey(n)) {
                count = countMap.get(n) + 1;
            } else {
                count = 1;
            }

            countMap.put(n, count);

            if (count > max) {
                max = count;
            }
        }

        for (Map.Entry<Double, Integer> tuple : countMap.entrySet()) {
            if (tuple.getValue() == max) {
                modes.add(Math.round(tuple.getKey() * 100.0)/100.0);
            }
        }
        return modes;

    }
}
