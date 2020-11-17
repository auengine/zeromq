package com.bist.zeromq.statistics;

import org.apache.log4j.Logger;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class RawDataStatistics {

    private final static Logger log = Logger.getLogger(RawDataStatistics.class);
    private static final String PATH = "\\\\Dsm\\dsm\\ISG\\Ortak\\latency tests\\genium_latency_tests_raw_data\\";
    private static final String FILE_NAME = "genium_latency_results.csv";

    public static void main(String[] args) throws IOException {

        File folder = new File(PATH);

        Comparator<Object> testNameComparator =
                Comparator.comparingInt(o -> ((TestInfo) o).testNameOrder).
                thenComparing(o -> ((TestInfo) o).clientCount).
                thenComparing(o -> ((TestInfo) o).messageSize).
                thenComparing(o -> ((TestInfo) o).messageCount);

        Map<TestInfo, String> testSummaries = new TreeMap<>(testNameComparator);

        File[] files = folder.listFiles();
        assert files != null : "files null";

        for (File file : files) {
            if (file.isDirectory()) {
                processTest(file, testSummaries);
            }
        }

        exportTestSummaries(testSummaries);
    }

    private static void exportTestSummaries(Map<TestInfo, String> testSummaries) {

        log.info("Exporting test results..");

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(PATH + FILE_NAME))) {
            writer.write("TEST CONFIG,,,,,,,,RESULT,,,,,,");
            writer.newLine();
            writer.write("Test Type,Nbr of Clients,Msg Size(byte),Msg per Second,Duration(min)," +
                    "Response Count,Nbr of Total Sent Msgs,,Min(us),Avg100(us),Avg99(us),Median(us),95(us),99(us),Max(us),StdDv100,StdDv99");
            writer.newLine();
            for (String value : testSummaries.values()) {
                writer.write(value);
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void processTest(File folder, Map<TestInfo, String> testSummaries) throws IOException {

        File[] files = folder.listFiles();
        assert files != null : "test files null";

        String testFullName = folder.getName();

        if (testFullName.startsWith("ETR")) {
            return;
        }

        log.info("Processing " + testFullName + " test data..");

        TestInfo testInfo = new TestInfo(testFullName);

        Map<String, List<String>> linesByTest = new TreeMap<>();

        for (File file : files) {
            String testTime = getTestTime(file.getName());
            List<String> strings = linesByTest.computeIfAbsent(testTime, k -> new ArrayList<>());
            strings.addAll(Files.readAllLines(Paths.get(file.toURI())));
        }

        List<SummaryInfo> summaries = new ArrayList<>();

        for (Map.Entry<String, List<String>> entry : linesByTest.entrySet()) {
            List<String> lines = entry.getValue();
            RawLatencyResults rawLatencyResults = new RawLatencyResults();
            lines.forEach(line -> rawLatencyResults.add(Long.parseLong(line)));
            List<RawLatencyResults.LongHolder> results = rawLatencyResults.getSortedResults();
            AverageInfo averageInfo = getAverageInfo(results);
            long min = results.get(0).value;
            long avg100 = averageInfo.avg100;
            long avg99 = averageInfo.avg99;
            long median = getMedian(results);
            long conf95 = getConfidenceResult(results, 95);
            long conf99 = getConfidenceResult(results, 99);
            long max = results.get(results.size() - 1).value;
            long std100 = averageInfo.std100;
            long std99 = averageInfo.std99;
            String summary = String.format("%s,%s,%s,%s,%s,%s,%s,%s,%s", min, avg100, avg99, median, conf95, conf99, max, std100, std99);
            summaries.add(new SummaryInfo(avg100, summary));
        }

        if (summaries.size() == 1) { // constant and multiple
            testSummaries.put(testInfo, testInfo.getSummaryLine() + summaries.get(0).summary);
        } else { // burst
            List<SummaryInfo> summariesTmp = new ArrayList<>(summaries);
            summariesTmp.sort((o1, o2) -> (int) (o1.avg - o2.avg));
            SummaryInfo medianTest = summariesTmp.get(summariesTmp.size() / 2);
            testSummaries.put(testInfo, testInfo.getSummaryLine() + medianTest.summary);
        }
    }

    private static AverageInfo getAverageInfo(List<RawLatencyResults.LongHolder> results) {

        long totalFor100;
        long totalFor99 = 0;
        long deviationsFor100 = 0;
        long deviationsFor99 = 0;
        int sizeOf100 = results.size();
        int sizeOf99 = sizeOf100 * 99 / 100;

        for (int i = 0; i < sizeOf99; i++) { // iterate over first %99 elements
            totalFor99 += results.get(i).value;
        }

        totalFor100 = totalFor99;

        for (int i = sizeOf99; i < sizeOf100; i++) { // iterate over remaining %1 elements
            totalFor100 += results.get(i).value;
        }

        AverageInfo averageInfo = new AverageInfo();
        averageInfo.avg100 = Math.round((double) totalFor100 / sizeOf100);
        averageInfo.avg99 = Math.round((double) totalFor99 / sizeOf99);

        for (int i = 0; i < sizeOf99; i++) { // iterate over first %99 elements
            deviationsFor99 += Math.pow(results.get(i).value - averageInfo.avg99, 2);
            deviationsFor100 += Math.pow(results.get(i).value - averageInfo.avg100, 2);
        }

        for (int i = sizeOf99; i < sizeOf100; i++) { // iterate over remaining %1 elements
            deviationsFor100 += Math.pow(results.get(i).value - averageInfo.avg100, 2);
        }

        double varianceFor100 = (double) deviationsFor100 / (sizeOf100 - 1);
        double varianceFor99 = (double) deviationsFor99 / (sizeOf99 - 1);

        averageInfo.std100 = Math.round(Math.sqrt(varianceFor100));
        averageInfo.std99 = Math.round(Math.sqrt(varianceFor99));

        return averageInfo;
    }

    private static long getMedian(List<RawLatencyResults.LongHolder> results) {
        if (results.size() % 2 == 0) {
            return results.size() >= 2 ? (results.get(results.size() / 2).value + results.get(results.size() / 2 - 1).value) / 2: 0;
        } else {
            return results.get(results.size() / 2).value;
        }
    }

    private static long getConfidenceResult(List<RawLatencyResults.LongHolder> results, int confidenceRange) {
        if (results.size() > 1) {
            int index = (int) ((long) results.size() * confidenceRange / 100) - 1;
            return results.get(index).value;
        } else {
            return 0;
        }
    }

    private static String getTestTime(String fileName) {
        String[] split = fileName.split("-");
        return fileName.startsWith("MULTIPLE") ? split[4]: split[3];
    }

    private static class SummaryInfo {
        long avg;
        String summary;
        SummaryInfo(long avg, String summary) {
            this.avg = avg;
            this.summary = summary;
        }
    }

    private static class AverageInfo {
        long avg100;
        long avg99;
        long std100;
        long std99;
    }

    private static class TestInfo {
        String testName;
        int testNameOrder;
        int clientCount;
        int messageSize;
        int messageCount;
        TestInfo(String fullName) {
            String[] split = fullName.split("-");
            testName = split[0].replace("_RATE", "");
            clientCount = Integer.parseInt(split[1]);
            messageSize = Integer.parseInt(split[2]);
            messageCount = Integer.parseInt(split[3]);
            if (testName.equals("CONSTANT")) {
                testNameOrder = 1;
            } else if (testName.equals("BURST")) {
                testNameOrder = 2;
            } else { // MULTIPLE_RESPONSE
                testNameOrder = 3;
            }
        }
        String getSummaryLine() {
            if (testName.equals("CONSTANT")) {
                return String.format("%s,%s,%s,%s,5,1,%s,,", testName, clientCount, messageSize, messageCount, 300 * messageCount);
            } else if (testName.equals("BURST")) {
                return String.format("%s,%s,%s,-,-,1,%s,,", testName, clientCount, messageSize, messageCount);
            } else {
                return String.format("%s,%s,%s,-,-,%s,10000,,", testName, clientCount, messageSize, messageCount);
            }
        }
    }
}