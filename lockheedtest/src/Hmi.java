

import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

/**
 * This class is used for UI layout and presentation it contain only the UI and
 * does not contain any logic. The Jcomponent are stocked in the controller for
 * ease of access
 * 
 * @author pa
 *
 */
public class Hmi {

	Controller controller;

	public void populateHMI() {

		JFrame frame = new JFrame("Dictionary app");
		frame.setPreferredSize(new Dimension(800, 1000));
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		Container contentPane = frame.getContentPane();
		contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.Y_AXIS));
		controller = new Controller(contentPane);

		// the import button row
		JPanel importButtonPanel = new JPanel();
		importButtonPanel.setLayout(new FlowLayout());
		importButtonPanel.add(controller.getDictionaryImportButton());
		importButtonPanel.add(controller.getTextImportButton());

		JPanel previousAndNextRow = new JPanel();
		previousAndNextRow.setLayout(new FlowLayout());
		previousAndNextRow.add(controller.getPreviousWordButton());
		previousAndNextRow.add(controller.getNextWordButton());

		controller.getIncorrectTextArea().setPreferredSize(new Dimension(350, 20));
		controller.getReplaceByField().setPreferredSize(new Dimension(150, 20));

		// we had a scroll bar in case the text loaded is huge
		controller.getFullTextArea().setEditable(false);
		JScrollPane mainTextPanel = new JScrollPane(controller.getFullTextArea());
		mainTextPanel.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		mainTextPanel.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		mainTextPanel.setPreferredSize(new Dimension(400, 850));
		JPanel wordSuggestionPanelRow2 = new JPanel();
		wordSuggestionPanelRow2.add(controller.getIncorrectTextArea());
		wordSuggestionPanelRow2.add(controller.getReplaceByField());
		wordSuggestionPanelRow2.add(controller.getReplaceButton());

		JPanel savePanel = new JPanel();
		savePanel.add(controller.getSaveButton());

		contentPane.add(importButtonPanel);
		contentPane.add(mainTextPanel);
		contentPane.add(previousAndNextRow);
		contentPane.add(wordSuggestionPanelRow2);
		contentPane.add(savePanel);

		frame.pack();
		frame.setVisible(true);
	}

}
