import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.nio.Buffer;
import java.util.*;

public class TagExtractor extends JFrame {
    private JTextArea textArea;
    private JButton openFileButton;
    private JButton openStopWordButton;
    private JButton extractTagsButton;
    private JButton saveTagsButton;
    private File selectedFile;
    private Set<String> stopWords;
    private Map<String, Integer> tagFrequency;

    public TagExtractor() {
        setTitle("Tag Extractor");
        setSize(400,400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        textArea = new JTextArea(10,40);
        JScrollPane scrollPane = new JScrollPane(textArea);
        openFileButton = new JButton("Open Text File");
        openStopWordButton = new JButton("Open Stop Words File");
        extractTagsButton = new JButton("Extract Tags");
        saveTagsButton = new JButton("Save Tags");

        openFileButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                int result = fileChooser.showOpenDialog(null);
                if (result == JFileChooser.APPROVE_OPTION){
                    selectedFile = fileChooser.getSelectedFile();
                    textArea.setText("File Selected" + selectedFile.getName());
                }
            }
        });

        openStopWordButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                int result = fileChooser.showOpenDialog(null);
                if (result == JFileChooser.APPROVE_OPTION){
                    File stopWordsFile = fileChooser.getSelectedFile();
                    stopWords = loadStopWordsFromFile(stopWordsFile);
                    textArea.setText("Stop Words Loaded");
                }
            }
        });

        extractTagsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (selectedFile == null || stopWords == null){
                    textArea.setText("Please select a text file and load stop words file.");
                    return;
                }
                tagFrequency = extractTags(selectedFile, stopWords);
                displayTagFrequency(tagFrequency);
            }
        });

        saveTagsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (tagFrequency == null){
                    textArea.setText("No tags to save. Please extract tags first.");
                    return;
                }
                JFileChooser fileChooser = new JFileChooser();
                int result = fileChooser.showSaveDialog(null);
                if (result == JFileChooser.APPROVE_OPTION){
                    File outputFile = fileChooser.getSelectedFile();
                    saveTagsToFile(outputFile, tagFrequency);
                    textArea.append("\nTags saved to:" + outputFile.getName());
                }
            }
        });

        JPanel panel = new JPanel();
        panel.add(openFileButton);
        panel.add(openStopWordButton);
        panel.add(extractTagsButton);
        panel.add(saveTagsButton);

        Container contentPane = getContentPane();
        contentPane.add(scrollPane, BorderLayout.CENTER);
        contentPane.add(panel, BorderLayout.SOUTH);

    }

    private Set<String> loadStopWordsFromFile(File stopWordsFile) {
        Set<String> stopWords = new HashSet<>();
        try(BufferedReader reader = new BufferedReader(new FileReader(stopWordsFile))) {
            String line;
            while((line = reader.readLine()) != null){
                stopWords.add(line.toLowerCase());
            }
        }catch (IOException e){
            e.printStackTrace();
        }
        return stopWords;
    }

    private Map<String, Integer> extractTags(File textFile, Set<String> stopWords) {
        Map<String, Integer> tagFrequency = new HashMap<>();
        try(BufferedReader reader = new BufferedReader(new FileReader(textFile))){
            String line;
            while ((line = reader.readLine()) != null){
                String[] words = line.split("\\s+");
                for (String word : words){
                    word = word.toLowerCase().replaceAll("[^a-z]" , "");
                    if (!word.isEmpty() && !stopWords.contains(word)) {
                        tagFrequency.put(word, tagFrequency.getOrDefault(word, 0) + 1);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return tagFrequency;
    }

    private void displayTagFrequency(Map<String, Integer> tagFrequency){
        textArea.setText("Tags and Frequencies:\n");
        for (Map.Entry<String, Integer> entry : tagFrequency.entrySet()) {
            textArea.append(entry.getKey() + ": " + entry.getValue() + "\n");
        }
    }

    private void saveTagsToFile(File outputFile, Map<String, Integer> tagFrequency) {
        try(BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile))) {
            for (Map.Entry<String, Integer> entry : tagFrequency.entrySet()){
                writer.write(entry.getKey() + ": " + entry.getValue());
                writer.newLine();
            }
        }catch (IOException e){
            e.printStackTrace();
        }
    }

}