

import java.util.ArrayList;
import java.util.Collection;

public class Model {
	private Collection<String> dictionary;
	private ArrayList<String> incorectWord;

	public Collection<String> getDictionary() {
		return dictionary;
	}

	public void setDictionary(Collection<String> dictionary) {
		this.dictionary = dictionary;
	}

	public ArrayList<String> getIncorectWord() {
		return incorectWord;
	}

	public void setIncorectWord(ArrayList<String> incorectWord) {
		this.incorectWord = incorectWord;
	}
}
