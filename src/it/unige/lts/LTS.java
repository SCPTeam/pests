package it.unige.lts;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import it.unige.automata.State;
import it.unige.automata.Transition;
import it.unige.automata.impl.TransitionImpl;

public class LTS {
	public Set<State> states;
	public State inits;
	public Set<TransitionImpl> delta;
	
	public LTS() {
		states = new HashSet<State>();
		delta = new HashSet<TransitionImpl>();
	}
	
	public Set<String> Sigma() {
		HashSet<String> Sigma = new HashSet<String>();
		for(TransitionImpl t : delta) {
			Sigma.add(t.getLabel());
		}
		
		return Sigma;
	}

	public Set<Transition> getTransitions(State s, String a) {
		HashSet<Transition> T = new HashSet<Transition>();
		for(TransitionImpl t : delta)
			if(t.getSource().equals(s) && t.getLabel().equals(a))
				T.add(t);
		return T;
	}
	
	public Set<Transition> getTransitions(State s, Collection<String> Sig) {
		HashSet<Transition> T = new HashSet<Transition>();
		for(String a : Sig)
			T.addAll(getTransitions(s, a));
		return T;
	}
}
