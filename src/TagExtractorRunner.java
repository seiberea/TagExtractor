import javax.swing.*;

public class TagExtractorRunner {
    public static void main(String[] args){
        SwingUtilities.invokeLater(() -> {
            TagExtractor gui = new TagExtractor();
            gui.setVisible(true);
        });
    }
}
