package it.unige.automata.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

import it.unige.automata.State;

public class MultiStateImpl implements State {

	ArrayList<State> states = new ArrayList<State>();
	
	public MultiStateImpl(Collection<State> S) {
		states.addAll(S);
	}
	
	
	@Override
	public int compareTo(State o) {
		if(o == null)
			return -2;
		if(o instanceof MultiStateImpl) {
			for(State s : ((MultiStateImpl) o).states) {
				if(!states.contains(s))
					return 1;
			}
			for(State s : states) {
				if(!((MultiStateImpl) o).states.contains(s))
					return -1;
			}
			return 0;
		}
		return -2;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((states.isEmpty()) ? 0 : states.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		MultiStateImpl other = (MultiStateImpl) obj;
		HashSet<State> s1 = new HashSet<State>(states);
		HashSet<State> s2 = new HashSet<State>(other.states);
		return s1.equals(s2);
	}

	@Override
	public String getLabel() {
		String r = "_";
		for(State s : states)
			r += s.getLabel() + "_";
		
		return r;
	}

}
