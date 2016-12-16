

import javax.swing.JLabel;

/**
 * This class is a runnable used to check if a word is in the dictionary
 * 
 * @author pa
 *
 */
public class WordAnalyzer implements Runnable {

	private String word;
	private JLabel incorrectTextArea;
	private Model data;

	public WordAnalyzer(String word, Model data, JLabel incorrectTextArea) {
		this.data = data;
		this.word = word;
		this.incorrectTextArea = incorrectTextArea;
	}

	@Override
	public void run() {
		// we check that the word is in the dictionary if it's not we add it to
		// the incorrect word list
		if (data.getDictionary().stream().filter(s -> s.trim().equals(word)).count() == 0) {
			synchronized (data) {
				if (data.getIncorectWord().size() == 0) {
					incorrectTextArea.setText(word);
				}
				data.getIncorectWord().add(word);
				System.out.println("Word in error : " + word);
			}
		}
	}
}
