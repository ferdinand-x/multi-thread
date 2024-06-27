package chapter1.chapter1.cancel;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonWriter;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class Main {

    private static final int BATCH_SIZE = 1; // 每批次处理的数据量
    private static final String TEMP_DIR = "temp_files"; // 临时文件目录
    private static final Gson GSON = new GsonBuilder().create();

    public static void main(String[] args) throws JsonProcessingException {
        // 尝试生成1000个对象，具体大小视数据生成的复杂度而定
        List<Data> dataList = generateLargeDataList(1000);

        long startTime = System.currentTimeMillis();
        Map<Integer, String> jsonMap = convertToJsonMap(dataList);
        long endTime = System.currentTimeMillis();

        System.out.println("转换完成，共 " + jsonMap.size() + " 条数据，耗时 " + (endTime - startTime) + " 毫秒");
    }

    public static List<Data> generateLargeDataList(int size) {
        List<Data> dataList = new ArrayList<>(size);
        Random random = new Random();

        for (int i = 0; i < size; i++) {
            List<String> randomStringList = generateRandomStringList(random, 100);
            List<NestedData> nestedDataList = generateNestedDataList(random, 20);
            dataList.add(new Data(
                    i,
                    "Name" + random.nextInt(size),
                    "Description" + random.nextInt(size),
                    random.nextDouble() * 1000,
                    generateRandomString(random, 1000),
                    randomStringList,
                    nestedDataList,
                    new DeepNestedData(generateNestedDataList(random, 10))
            ));
        }

        return dataList;
    }

    public static String generateRandomString(Random random, int length) {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(characters.charAt(random.nextInt(characters.length())));
        }
        return sb.toString();
    }

    public static List<String> generateRandomStringList(Random random, int size) {
        List<String> list = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            list.add(generateRandomString(random, 100));
        }
        return list;
    }

    public static List<NestedData> generateNestedDataList(Random random, int size) {
        List<NestedData> list = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            list.add(new NestedData(random.nextInt(size), "Nested" + random.nextInt(size), generateRandomStringList(random, 10)));
        }
        return list;
    }

    public static Map<Integer, String> convertToJsonMap(List<Data> dataList) throws JsonProcessingException {
        Map<Integer, String> jsonMap = new HashMap<>();

        File tempDir = new File(TEMP_DIR);
        if (!tempDir.exists()) {
            tempDir.mkdirs();
        }

        try {
            int fileCounter = 0;

            for (int i = 0; i < dataList.size(); i += BATCH_SIZE) {
                int end = Math.min(dataList.size(), i + BATCH_SIZE);
                List<Data> batch = dataList.subList(i, end);
                File tempFile = new File(tempDir, "data_batch_" + fileCounter++ + ".json");

                try (Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(tempFile), StandardCharsets.UTF_8));
                     JsonWriter jsonWriter = new JsonWriter(writer)) {

                    jsonWriter.beginArray();
                    for (Data data : batch) {
                        GSON.toJson(data, Data.class, jsonWriter);
                    }
                    jsonWriter.endArray();
                }
            }

            // 读取临时文件并构建最终的Map
            for (File tempFile : Objects.requireNonNull(tempDir.listFiles())) {
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(tempFile), StandardCharsets.UTF_8))) {
                    String jsonString = "";
                    String line;
                    while ((line = reader.readLine()) != null) {
                        jsonString+=line;
                    }
                    var dataArray = GSON.fromJson(jsonString, Data[].class);
                    for (Data data : dataArray) {
                        jsonMap.put(data.getId(), jsonString);
                    }
                }
                // 删除临时文件
                tempFile.delete();
            }

            // 删除临时文件目录
            tempDir.delete();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return jsonMap;

    }

    @lombok.Data
    static class Data {
        private int id;
        private String name;
        private String description;
        private double value;
        private String randomString;
        private List<String> randomStringList;
        private List<NestedData> nestedDataList;
        private DeepNestedData deepNestedData;

        public Data(int id, String name, String description, double value, String randomString, List<String> randomStringList, List<NestedData> nestedDataList, DeepNestedData deepNestedData) {
            this.id = id;
            this.name = name;
            this.description = description;
            this.value = value;
            this.randomString = randomString;
            this.randomStringList = randomStringList;
            this.nestedDataList = nestedDataList;
            this.deepNestedData = deepNestedData;
        }
    }

    @lombok.Data
    static class NestedData {
        private int nestedId;
        private String nestedName;
        private List<String> nestedStringList;

        public NestedData(int nestedId, String nestedName, List<String> nestedStringList) {
            this.nestedId = nestedId;
            this.nestedName = nestedName;
            this.nestedStringList = nestedStringList;
        }
    }

    @lombok.Data
    static class DeepNestedData {
        private List<NestedData> deepNestedDataList;

        public DeepNestedData(List<NestedData> deepNestedDataList) {
            this.deepNestedDataList = deepNestedDataList;
        }
    }
}
