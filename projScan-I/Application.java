import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

import org.json.simple.*;
import org.json.simple.parser.JSONParser;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

public class Application {
    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Project Path is missing, Program Terminated...");
            return;
        }
        //String filePath = "/home/th2888/cs6156/SANGRIA/service-operation/";
        String filePath = args[0];
        String javaAnaFilePath = filePath + "reports/scan-full-report.json";
        String depAnaFilePath = filePath + "reports/depscan-report-java.json";
        String codeSimiFilePath = filePath + "report.html";
        //String codeSimiFilePath = filePath + "reports/report.html";
	try{
	    parseJavaAnalysis(filePath, javaAnaFilePath);
	} catch (Exception e) {
	    System.out.println("No code analysis report, proceed to the next one.");
	}
        try{
	    parseDepAnalysis(filePath, depAnaFilePath);
	} catch (Exception e) {
	    System.out.println("No dependency analysis report, proceed to the next one.");
	}
	try{
	    parseCodeSimiAnalysis(filePath, codeSimiFilePath);
	} catch (Exception e) {
	    System.out.println("No code similarity analysis report, proceed to the next one.");
	} 
    }
    private static void parseCodeSimiAnalysis(String filePath, String codeSimiFilePath) {
        File htmlFile = new File(codeSimiFilePath);
        Document document;
        try {
            document = Jsoup.parse(htmlFile, "UTF-8");
            Elements p = document.getElementsByTag("p");
            Elements s = document.getElementsByClass("highlight-red");
            Elements g = document.getElementsByClass("highlight-green");
            int index_p = 0;
            int index_s = 0;
            int index_g = 0;
            int count = 1;
            String fileStr = "Code Similarity Analysis Report\n\n";
            fileStr += "Scanned Project Path: " + filePath + "\n\n";
            fileStr += "Code Similarity Found Summary:\n\n";
            while (index_p < p.size() && index_s < s.size() && index_g < g.size()) {
                if (index_p == 0 && p.get(index_p).text().startsWith("Note:")) {
                    index_p++;
                    continue;
                }
                if (index_p == 1) {
                    String text = p.get(index_p).text();
                    String tested = "";
                    String reference = "";
                    String above = "";
                    if (text.startsWith("Number of files")) {
                        int referenceStartIndex = text.indexOf("Number of reference files",0);
                        if (referenceStartIndex != -1) {
                            tested = text.substring(0, referenceStartIndex);
                        }
                        int testStartIndex = text.indexOf("Test files above display");
                        if (referenceStartIndex != -1) {
                            reference = text.substring(referenceStartIndex, testStartIndex);
                            above = text.substring(testStartIndex);
                        }
                    }
                    fileStr += tested + "\n" + reference + "\n" + above + "\n\n";
                    index_p++;
                }

                String p_text = p.get(index_p).text();
                if (!p_text.startsWith("Test file:")) {
                    index_p++;
                    continue;
                }
                String testFile = "";
                String refFile = "";
                String token = "";
                int refStartIndex = p_text.indexOf("Reference file:");
                if (refStartIndex != -1) {
                    testFile = p_text.substring(0, refStartIndex);
                }
                int tokenStartIndex = p_text.indexOf("Token overlap:");
                if (tokenStartIndex != -1) {
                    refFile = p_text.substring(refStartIndex,tokenStartIndex);
                    int viewStartIndex = p_text.indexOf("View matched code");
                    if (viewStartIndex != -1) {
                        token = p_text.substring(tokenStartIndex, viewStartIndex);
                    }
                }
                fileStr += count + ").\n";
                count++;
                fileStr += testFile + "\n" + refFile + "\n" + token + "\n\n";
                index_p++;
                fileStr += "Similar Code Snippet: \n\n";
                fileStr += s.get(index_s).text() + "\n\n\n";
                fileStr += g.get(index_g).text() + "\n\n";
                index_g++;
                index_s++;
            }
//            for (Element e : p) {
//                System.out.println(e.text());
//            }
//            for (Element e : s) {
//                System.out.println(e.text());
//                System.out.println("-------------");
//            }
            try{
                BufferedWriter writer = new BufferedWriter(new FileWriter("Code_Similarity_Analysis_Report_" + System.currentTimeMillis() + ".txt"));
                writer.write(fileStr);

                writer.close();
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private static void parseDepAnalysis(String filePath, String depAnaFilePath) {
        Map<String, Integer> countMap = new HashMap<>();
        List<DependenceResult> resultMap = new ArrayList<>();
        try{
            List<String> lines = Files.readAllLines(Paths.get(depAnaFilePath));
            for (String line: lines) {
                JSONParser parser = new JSONParser();
                JSONObject jsonObject = (JSONObject) parser.parse(line);
                DependenceResult resultObject = new DependenceResult();
                String id = (String)jsonObject.get("id");
                String packageName = (String)jsonObject.get("package");
                String severity = (String)jsonObject.get("severity");
                String score = (String)jsonObject.get("cvss_score");
                String message = (String)jsonObject.get("short_description");

                resultObject.id = id;
                resultObject.packageName = packageName;
                resultObject.severity = severity;
                resultObject.score = score;
                resultObject.message = message;

                if (countMap.containsKey(severity)) {
                    countMap.put(severity,countMap.get(severity) + 1);
                } else {
                    countMap.put(severity,1);
                }

                JSONArray urls = (JSONArray)jsonObject.get("related_urls");
                for (int i = 0; i < urls.size(); i++) {
                    String url = (String)urls.get(i);
                    resultObject.urls.add(url);
                }
                resultMap.add(resultObject);
            }
            writeDepAnaResult(filePath, countMap, resultMap);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }
    private static void writeDepAnaResult(String filePath, Map<String, Integer> countMap, List<DependenceResult> resultMap) {
        String resultStr = "Project Dependencies Analysis Report\n\n";
        resultStr += "Scanned Project Path: " + filePath + "\n\n";
        resultStr += "Dependency Analysis Found Vulnerabilities Summary:\n\n";

        for (String key : countMap.keySet()) {
            resultStr += key + "    " + countMap.get(key) + "\n";
        }
        resultStr += "\nFound Vulnerabilities Detail Information:\n\n";
        int count = 1;
        for (DependenceResult result : resultMap) {
            resultStr += count + ".\n" + result.printResult() + "\n";
            count++;
        }
        try{
            BufferedWriter writer = new BufferedWriter(new FileWriter("Dependencies_Analysis_Report_" + System.currentTimeMillis() + ".txt"));
            writer.write(resultStr);

            writer.close();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
    private static void parseJavaAnalysis(String filePath, String javaAnaFilePath) {
        Map<String, Map<String,Integer>> countMap = new HashMap<>();
        Map<String, List<ScanResult>> detailMap = new HashMap<>();
        try{
            List<String> lines = Files.readAllLines(Paths.get(javaAnaFilePath));
            for (String line : lines) {
                parseToolInfo(line, countMap, detailMap);
            }
            writeResultFile(filePath, countMap, detailMap);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
    private static void parseToolInfo(String line, Map<String, Map<String,Integer>> countMap, Map<String, List<ScanResult>> detailMap) {
        try {
            JSONParser parser = new JSONParser();
            JSONObject jsonObject = (JSONObject) parser.parse(line);
            JSONObject tool = (JSONObject)jsonObject.get("tool");
            JSONObject driver = (JSONObject)tool.get("driver");
            String TestName = (String)driver.get("name");

            JSONObject properties = (JSONObject)jsonObject.get("properties");
            JSONObject metrics = (JSONObject)properties.get("metrics");
            int total = ((Long) metrics.get("total")).intValue();
            int critical = ((Long) metrics.get("critical")).intValue();
            int high = ((Long) metrics.get("high")).intValue();
            int medium = ((Long) metrics.get("medium")).intValue();
            int low = ((Long) metrics.get("low")).intValue();

            Map<String, Integer> metrix = new HashMap<>();
            metrix.put("critical",critical);
            metrix.put("total",total);
            metrix.put("high",high);
            metrix.put("medium",medium);
            metrix.put("low",low);
            countMap.put(TestName, metrix);

            // ---- parsing detailed error information
            JSONArray results = (JSONArray)jsonObject.get("results");
            List<ScanResult> resultList = new ArrayList<>();
            for (int i = 0; i < results.size(); i++) {
                ScanResult resultObject = new ScanResult();

                JSONObject result = (JSONObject)results.get(i);
                JSONObject message = (JSONObject)result.get("message");
                String textMessage = (String)message.get("text");
                String level = (String)result.get("level");
                JSONObject prop = (JSONObject)result.get("properties");
                String severity = (String)prop.get("issue_severity");

                resultObject.message = textMessage;
                resultObject.level = level;
                resultObject.severity = severity;
                resultObject.fileLocation = new ArrayList<>();
                resultObject.lineContent = new ArrayList<>();
                resultObject.lineNumber = new ArrayList<>();

                JSONArray locations = (JSONArray)result.get("locations");
                for(int j = 0; j < locations.size(); j++) {
                    JSONObject location = (JSONObject)locations.get(j);
                    JSONObject physicalLocation = (JSONObject) location.get("physicalLocation");
                    JSONObject region = (JSONObject) physicalLocation.get("region");
                    int lineNumber = ((Long) region.get("startLine")).intValue();
                    JSONObject snippet = (JSONObject) region.get("snippet");
                    String lineContent = (String) snippet.get("text");

                    JSONObject artifactLocation = (JSONObject) physicalLocation.get("artifactLocation");
                    String fileLocation = (String) artifactLocation.get("uri");

                    resultObject.lineNumber.add(lineNumber);
                    resultObject.lineContent.add(lineContent);
                    resultObject.fileLocation.add(fileLocation);
                }
                //System.out.println(resultObject.printResult());
                resultList.add(resultObject);
            }
            detailMap.put(TestName,resultList);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.out.println("Error in parsing Tool Info.");
        }
    }
    private static void writeResultFile(String filePath, Map<String, Map<String,Integer>> countMap, Map<String, List<ScanResult>> detailMap) {
        String fileStr = "Java Project Code Static Analysis Result \n\n";
        fileStr += "Scanned Project Path: " + filePath + "\n\n";
        fileStr += "Code Threat and Vulnerabilities Found Summary: \n\n";
        for (String key : countMap.keySet()) {
            if (key.equals("Java Source Analyzer")) {
                fileStr += "1).Source File Result: \n";
            } else if (key.equals("Security Audit for Infrastructure")) {
                fileStr += "2).Infrastructure Security: \n";
            } else if (key.equals("Class File Analyzer")) {
                fileStr += "3).Class File Result: \n";
            } else {
                fileStr += "4).Secrets Audit: \n";
            }
            Map<String,Integer> countResult = countMap.get(key);
            for (String level: countResult.keySet()) {
                fileStr += level + "    " + countResult.get(level) + "\n";
            }
            fileStr += "\n";
        }
        fileStr += "\n";
        fileStr += "Found Vulnerabilities Detail Information: \n\n";
        for (String name : detailMap.keySet()) {
            if (name.equals("Java Source Analyzer")) {
                fileStr += "1).Source File Result: \n\n";
            } else if (name.equals("Security Audit for Infrastructure")) {
                fileStr += "2).Infrastructure Security: \n\n";
            } else if (name.equals("Class File Analyzer")) {
                fileStr += "3).Class File Result: \n\n";
            } else {
                fileStr += "4).Secrets Audit: \n\n";
            }
            List<ScanResult> detailResults = detailMap.get(name);
            if (detailResults == null || detailResults.size() == 0) {
                fileStr += "None\n\n";
            } else {
                int count = 1;
                for (ScanResult re : detailResults) {
                    fileStr += count + ".";
                    fileStr += re.printResult();
                    count++;
                }
            }
        }
        try{
            BufferedWriter writer = new BufferedWriter(new FileWriter("Code_Analysis_Report_" + System.currentTimeMillis() + ".txt"));
            writer.write(fileStr);

            writer.close();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
    static class DependenceResult{
        public String id;
        public String packageName;
        public String score;
        public String severity;
        public String message;
        public List<String> urls;

        public DependenceResult() {
            this.urls = new ArrayList<>();
        }
        public String printResult() {
            String str = "";
            str += "ID: " + this.id + "\n";
            str += "Package Name: " + this.packageName + "\n";
            str += "CVSS Score (A higher score indicates higher severity): " + this.score + "\n";
            str += "Severity: " + this.severity + "\n";
            str += "Related Urls: \n";
            int count = 1;
            for (String url : this.urls) {
                str += count + "." + url + "\n";
                count++;
            }
            str += "Threat Description: \n" + this.message + "\n";
            return str;
        }
    }

    static class ScanResult{
        public String message;
        public String level;
        public List<String> fileLocation;
        public List<String> lineContent;
        public List<Integer> lineNumber;
        public String severity;

        public ScanResult() {

        }

        public ScanResult(String message, String level, List<String> fileLocation, List<String> lineContent, List<Integer> lineNumber, String severity) {
            this.message = message;
            this.level = level;
            this.fileLocation = fileLocation;
            this.lineContent = lineContent;
            this.lineNumber = lineNumber;
            this.severity = severity;
        }

        public String printResult() {
            String result = "Issue Details: \n\n";
            result += message + "\n\n";
            for (int i = 0; i < this.fileLocation.size(); i++) {
                result += "File Location: " + fileLocation.get(i) + "\n";
                result += "At Line " + lineNumber.get(i) + ", code content: " + lineContent.get(i) + "\n";
            }
            result += "Issue Level: " + level + "\n";
            result += "Issue Severity: " + severity + "\n\n";

            return result;
        }
    }
}
