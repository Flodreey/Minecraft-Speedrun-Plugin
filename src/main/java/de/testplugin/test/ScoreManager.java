package de.testplugin.test;

import org.jetbrains.annotations.NotNull;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class ScoreManager {

    public static class ScoreEntry implements Comparable<ScoreEntry>{
        List<String> playerNames;
        LocalTime speedrun_time;
        LocalTime time;
        LocalDate date;
        long worldSeed;

        ScoreEntry (List<String> playerNames, LocalTime speedrun_time, LocalTime time, LocalDate date, long worldSeed){
            this.playerNames = playerNames;
            this.speedrun_time = speedrun_time;
            this.time = time;
            this.date = date;
            this.worldSeed = worldSeed;
        }

        ScoreEntry (List<String> playerNames, String speedrun_time, String time, String date, long worldSeed){
            this(
                    playerNames,
                    LocalTime.parse(speedrun_time, DateTimeFormatter.ofPattern("H:m:s")),
                    LocalTime.parse(time, DateTimeFormatter.ofPattern("H:m:s")),
                    LocalDate.parse(date, DateTimeFormatter.ofPattern("d.M.yyyy")),
                    worldSeed
            );
        }

        ScoreEntry (List<String> playerNames, LocalTime speedrun_time, long worldSeed){
            this(playerNames, speedrun_time, LocalTime.now(), LocalDate.now(), worldSeed);
        }

        ScoreEntry (List<String> playerNames, String speedrun_time, long worldSeed){
            this(playerNames, LocalTime.parse(speedrun_time, DateTimeFormatter.ofPattern("H:m:s")), worldSeed);
        }

        public List<String> getPlayerNames() {
            return playerNames;
        }

        public LocalTime getSpeedrunTime() {
            return speedrun_time;
        }

        public String getSpeedrunTimeString() {
            return speedrun_time.format(DateTimeFormatter.ofPattern("H:m:s"));
        }

        public LocalTime getTime() {
            return time;
        }

        public String getTimeString() {
            return time.format(DateTimeFormatter.ofPattern("H:m:s"));
        }

        public LocalDate getDate() {
            return date;
        }

        public String getDateString() {
            return date.format(DateTimeFormatter.ofPattern("d.M.yyyy"));
        }

        public long getWorldSeed() {
            return worldSeed;
        }

        @Override
        public String toString() {
            StringBuilder builder = new StringBuilder();
            builder.append("[");
            for (int i = 0; i < playerNames.size(); i++) {
                if (i == playerNames.size() - 1){
                    builder.append(playerNames.get(i));
                } else {
                    builder.append(playerNames.get(i)).append(", ");
                }
            }
            builder.append(getSpeedrunTimeString()).append(getTimeString()).append(getDateString());
            return builder.toString();
        }

        @Override
        public boolean equals(Object obj) {
            return super.equals(obj);
        }

        @Override
        public int compareTo(@NotNull ScoreEntry o) {
            return speedrun_time.compareTo(o.speedrun_time);
        }
    }
    private final String fileName = "scores.json";

    public void clearScores(){
        writeScores(new ArrayList<>());
    }
    public int addEntry(List<String> playerNames, LocalTime requiredTime, LocalTime time, LocalDate date, long worldSeed) {
        ScoreEntry newEntry = new ScoreEntry(playerNames, requiredTime, time, date, worldSeed);
        List<ScoreEntry> entries = readScores();
        boolean added = false;
        int ranking = -1;
        for (int i = 0; i < entries.size(); i++){
            if (entries.get(i).compareTo(newEntry) > 0){
                entries.add(i, newEntry);
                added = true;
                ranking = i + 1;
                break;
            }
        }
        if (!added){
            entries.add(newEntry);
            ranking = entries.size();
        }
        writeScores(entries);
        return ranking;
    }

    public int addEntry(List<String> playerNames, LocalTime requiredTime, long worldSeed){
        return addEntry(playerNames, requiredTime, LocalTime.now(), LocalDate.now(), worldSeed);
    }

    private void writeScores(List<ScoreEntry> entries) {
        try {
            JSONObject jsonObject = new JSONObject();
            JSONArray scoreArray = new JSONArray();
            for (ScoreEntry entry : entries) {
                JSONObject current = new JSONObject();
                JSONArray playerArray = new JSONArray();
                playerArray.addAll(entry.getPlayerNames());
                current.put("players", playerArray);
                current.put("speedrun_time", entry.getSpeedrunTimeString());
                current.put("time", entry.getTimeString());
                current.put("date", entry.getDateString());
                current.put("seed", entry.getWorldSeed());
                scoreArray.add(current);
            }
            jsonObject.put("Scores", scoreArray);
            PrintWriter writer = new PrintWriter(fileName);
            writer.write(jsonObject.toJSONString());
            writer.flush();
            writer.close();
        } catch (FileNotFoundException e){
            throw new RuntimeException(e);
        }
    }

    public List<ScoreEntry> readScores(){
        if (!new File(fileName).exists()){
            return new ArrayList<>();
        }

        List<ScoreEntry> result = new ArrayList<>();
        try{
            Object obj = new JSONParser().parse(new FileReader(fileName));
            JSONObject jsonObject = (JSONObject) obj;
            JSONArray scoreArray = (JSONArray) jsonObject.get("Scores");
            if (scoreArray == null) return result;
            for (Object o : scoreArray) {
                JSONObject current = (JSONObject) o;
                JSONArray playerArray = (JSONArray) current.get("players");
                List<String> playerNames = new ArrayList<>();
                for (int i = 0; i < playerArray.size(); i++) {
                    playerNames.add(playerArray.get(i).toString());
                }
                String speedrun_time = (String) current.get("speedrun_time");
                String time = (String) current.get("time");
                String date = (String) current.get("date");
                long worldSeed = (Long) current.get("seed");
                ScoreEntry entry = new ScoreEntry(playerNames, speedrun_time, time, date, worldSeed);
                result.add(entry);
            }
        } catch(ParseException | IOException e){
            throw new RuntimeException(e);
        }
        return result;
    }

    public ScoreEntry getEntryAtIndex(int index){
        List<ScoreEntry> entries = readScores();
        return entries.get(index);
    }

    public int size(){
        List<ScoreEntry> entries = readScores();
        return entries.size();
    }

    public static void main(String[] args) {
        ScoreManager manager = new ScoreManager();
        System.out.println(manager.getEntryAtIndex(0).getWorldSeed());

        //manager.clearScores();
        //manager.addEntry(Arrays.asList("Flodreey", "linuss"), LocalTime.of(3, 0, 0), 100L);
        //manager.addEntry(Arrays.asList("Flodreey"), LocalTime.of(2, 30, 0));
        //manager.addEntry(Arrays.asList("Flodreey", "linuss", "Merlin"), LocalTime.of(2, 45, 0));

        //manager.readScores().forEach(System.out::println);

        /*
        List<ScoreEntry> entries = new ArrayList<>();
        entries.add(new ScoreEntry("André", LocalTime.of(3,14,22), LocalDate.of(2023, 3, 1)));
        entries.add(new ScoreEntry("Thierry", LocalTime.of(3,0,0), LocalDate.of(2023, 3, 1)));
        manager.writeScores(entries);
         */
    }
}
