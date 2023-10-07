package datamodel;

import java.util.Iterator;
import java.util.List;


public class PetrinetState {
	
	private String state;
	private List<Integer> placeTokens;

	public PetrinetState(String state, List<Integer> placeTokens) {
		this.state = state;
		this.placeTokens = placeTokens;
	}

	public String getState() {
		return state;
	}

	public Iterator<Integer> getPlaceTokens(){
		return placeTokens.iterator();
	}
	
	public int placeTokensSize() {
		return placeTokens.size();
	}
}
