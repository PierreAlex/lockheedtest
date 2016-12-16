
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

/**
 * A class in charge of implementing the action listener and loading the file
 * that client select. This is where most of the application logic occur and
 * where data is updated when an action occur
 * 
 * @author pa
 *
 */
public class Controller implements ActionListener {

	private static final int MAX_FILE_SIZE = 20480;
	private static final String SAVE = "Save text";
	private static final String REPLACE = "Replace";
	private static final String PREVIOUS_WORD = "Previous word";
	private static final String IMPORT_DICTIONARY = "Import dictionary";
	private static final String IMPORT_TEXT = "Import text";
	private static final String NEXT_WORD = "Next word";

	// the button component
	private JButton dictionaryImportButton = new JButton(IMPORT_DICTIONARY);
	private JButton textImportButton = new JButton(IMPORT_TEXT);
	private JButton previousWordButton = new JButton(PREVIOUS_WORD);
	private JButton nextWordButton = new JButton(NEXT_WORD);
	private JButton replaceButton = new JButton(REPLACE);
	private JButton saveButton = new JButton(SAVE);

	// the text component
	private JTextArea fullTextArea = new JTextArea("");
	private JLabel incorrectTextArea = new JLabel();
	private JTextField replaceByField = new JTextField();

	// the file chooser. We reuse the same for all file loading
	private JFileChooser importFileChooser = new JFileChooser();

	// the main view container. This is needed by the FileChooser
	private Container container;

	// The current position in the incorrectWord array. It mark the word
	// currently edited by the user
	public int currentPosition;

	// contain the dictionary and the list of incorrect word
	private Model data;

	private ExecutorService executor = Executors.newFixedThreadPool(5);

	public Controller(Container container) {
		this.container = container;
		setData(new Model());
		importFileChooser.setMultiSelectionEnabled(false);
		dictionaryImportButton.addActionListener(this);
		textImportButton.addActionListener(this);
		previousWordButton.addActionListener(this);
		nextWordButton.addActionListener(this);
		replaceButton.addActionListener(this);
		saveButton.addActionListener(this);
	}

	@Override
	public void actionPerformed(ActionEvent event) {
		if (event.getSource() == dictionaryImportButton) {
			// user clicked on the dictionary import button
			// we load the dictionary file and spellCheck
			String dictionaryString = getTextFromFile();
			if (dictionaryString != null) {
				// word are separeted by any space delimiter. So we split the
				// string. Into a collection
				data.setDictionary(Arrays.asList(dictionaryString.split("\\s+")));
				System.out.println("Successfuly loaded dictionary:" + data.getDictionary().toString());
			}
			spellCheck();
		} else if (event.getSource() == textImportButton) {
			// user clicked on the textImportButton
			// we load the text file and spellCheck
			String currentText = getTextFromFile();
			if (currentText != null) {
				fullTextArea.setText(currentText);
				System.out.println("Successfuly loaded text: " + currentText.toString());
			}
			spellCheck();
		} else if (event.getSource() == nextWordButton) {
			// user clicked on the nextWordButton
			// we increment the position and update the incorrect text field
			if (data.getIncorectWord() != null && data.getIncorectWord().size() != 0) {
				if (currentPosition + 1 < data.getIncorectWord().size()) {
					currentPosition++;
				} else {
					currentPosition = 0;
				}
				if (currentPosition < data.getIncorectWord().size())
					incorrectTextArea.setText(data.getIncorectWord().get(currentPosition));
			}
		} else if (event.getSource() == previousWordButton) {
			// user clicked on the previousWordButton
			// we decrement the position and update the incorrect text field
			if (data.getIncorectWord() != null && data.getIncorectWord().size() != 0) {
				if (currentPosition != 0) {
					currentPosition--;
				} else {
					currentPosition = data.getIncorectWord().size() - 1;
				}
				if (currentPosition < data.getIncorectWord().size())
					incorrectTextArea.setText(data.getIncorectWord().get(currentPosition));
			}
		} else if (event.getSource() == replaceButton) {
			// user clicked on the replace button
			// we validate that there is a word to replace and that the user
			// entered an input and then we replace it
			if (data.getIncorectWord() != null && currentPosition < data.getIncorectWord().size() && !replaceByField.getText().isEmpty()) {
				String newText = fullTextArea.getText().replace(data.getIncorectWord().get(currentPosition), replaceByField.getText());
				fullTextArea.setText(newText);
				data.getIncorectWord().remove(currentPosition);
				if (currentPosition == data.getIncorectWord().size())
					currentPosition = 0;
				if (currentPosition < data.getIncorectWord().size()) {
					incorrectTextArea.setText(data.getIncorectWord().get(currentPosition));
				} else {
					incorrectTextArea.setText("You have corrected all word");
				}
			}
		} else if (event.getSource() == saveButton) {
			// user clicked on the save button
			// we save the current text even if there is error to the selected
			// file
			int ret = importFileChooser.showOpenDialog(container);
			if (ret == JFileChooser.APPROVE_OPTION) {

				File file = importFileChooser.getSelectedFile();
				System.out.println("Saving text to : " + file.getName());
				try {
					Files.write(Paths.get(file.getPath()), fullTextArea.getText().getBytes());
				} catch (IOException e) {
					System.out.println("Error saving file");
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * We ask the user to choose a file the return it's value as a string if the
	 * file is bigger than 20Kb we don't load it
	 * 
	 * @return
	 */
	private String getTextFromFile() {
		int ret = importFileChooser.showOpenDialog(container);
		if (ret == JFileChooser.APPROVE_OPTION) {

			File file = importFileChooser.getSelectedFile();
			if (file.length() < MAX_FILE_SIZE) {
				try {
					return new String(Files.readAllBytes(file.toPath()), StandardCharsets.UTF_8);
				} catch (IOException e) {
					System.out.println("Error opening file");
					e.printStackTrace();
					return null;
				}
			} else {
				System.out.println("File too big. Max size is " + MAX_FILE_SIZE + " bytes");
			}
		}
		return null;
	}

	/**
	 * Method used to compare each word of a string to a dictionary Each word is
	 * processed n parallel
	 */
	public void spellCheck() {
		if (getFullTextArea().getText() != null && !getFullTextArea().getText().trim().isEmpty() && data.getDictionary() != null) {
			data.setIncorectWord(new ArrayList<String>());
			currentPosition = 0;
			for (String word : getFullTextArea().getText().split("\\s+")) {
				Runnable worker = new WordAnalyzer(word.trim(), data, incorrectTextArea);
				executor.execute(worker);
			}
		}
	}

	public JButton getDictionaryImportButton() {
		return dictionaryImportButton;
	}

	public JButton getTextImportButton() {
		return textImportButton;
	}

	public JFileChooser getImportFileChooser() {
		return importFileChooser;
	}

	public JButton getPreviousWordButton() {
		return previousWordButton;
	}

	public JButton getNextWordButton() {
		return nextWordButton;
	}

	public JLabel getIncorrectTextArea() {
		return incorrectTextArea;
	}

	public JTextArea getFullTextArea() {
		return fullTextArea;
	}

	public JTextField getReplaceByField() {
		return replaceByField;
	}

	public JButton getReplaceButton() {
		return replaceButton;
	}

	public JButton getSaveButton() {
		return saveButton;
	}

	public Model getData() {
		return data;
	}

	// this was added for test purpose
	public void setData(Model data) {
		this.data = data;
	}

}
