package it.unige.parteval;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import it.unige.automata.State;
import it.unige.automata.Transition;
import it.unige.lts.LTS;
import it.unige.mu.Assertion;
import it.unige.mu.MuAnd;
import it.unige.mu.MuBox;
import it.unige.mu.MuDia;
import it.unige.mu.MuEquation;
import it.unige.mu.MuFF;
import it.unige.mu.MuOr;
import it.unige.mu.MuSystem;
import it.unige.mu.MuTT;
import it.unige.mu.MuVar;

public class PartEval {
private static ArrayList<ArrayList<State>> AlgC1(ArrayList<ArrayList<State>> Chi) {
		
		int i = 1;
		int k = Chi.size();
		while(i <= k-1) {
			boolean flag = false;
			Set<State> Y = new HashSet<State>();
			ArrayList<Collection<State>> Chip = new ArrayList<Collection<State>>();
			int j = i + 1;
			while(j <= k) {
				Set<State> tmp = new HashSet<State>();
				tmp.addAll(Chi.get(i));
				tmp.retainAll(Chi.get(j));
				if(!tmp.isEmpty()) {
					flag = true;
					Y.addAll(Chi.get(j));
					Chi.remove(j);
				}
				else {
					Chip.add(Chi.get(j));
				}
				j++;
			}
			if(!flag) {
				i++;
			}
			else {
				Chi.get(i).addAll(Y);
				k = Chi.size();
			}
		}
		
		return Chi; 
	}
	
	public static ArrayList<ArrayList<State>> AlgC2(ArrayList<ArrayList<ArrayList<State>>> P) {
		ArrayList<ArrayList<State>> Z = new ArrayList<ArrayList<State>>();
		for(ArrayList<ArrayList<State>> Pi : P) {
			Z.addAll(Pi);
		}
		
		return AlgC1(Z);
	}
	
	public static ArrayList<ArrayList<State>> AlgC3(LTS A, Set<String> Sigma_B) {
		ArrayList<State> Q = new ArrayList<State>();
		Q.addAll(A.states);
		State q0 = Q.get(0);
		ArrayList<String> Sigma_AmB = new ArrayList<String>();
		Sigma_AmB.addAll(A.Sigma());
		Sigma_AmB.removeAll(Sigma_B);
		Set<Transition> Tq0 = A.getTransitions(q0, Sigma_AmB);
		ArrayList<State> Xq0 = new ArrayList<State>();
		for(Transition t : Tq0) {
			Xq0.add(t.getDestination());
		}
		ArrayList<ArrayList<State>> Chi = new ArrayList<ArrayList<State>>();
		Chi.add(Xq0);
		
		ArrayList<State> Y = new ArrayList<State>();
		Y.addAll(Q);
		Y.removeAll(Xq0);
		while(!Y.isEmpty()) {
			State q = Y.get(0);
			Set<Transition> Tq = A.getTransitions(q, Sigma_AmB);
			ArrayList<State> Xq = new ArrayList<State>();
			for(Transition t : Tq) {
				Xq.add(t.getDestination());
			}
			Chi.add(Xq);
			Y.removeAll(Xq);
		}
		
		return AlgC1(Chi);
	}
	
	public static ArrayList<ArrayList<State>> AlgC4(LTS A, ArrayList<ArrayList<State>> Chi, ArrayList<String> Sigma_B) {
		
		return null;
	}
	
	public static LTS proj(LTS spec, LTS A, Set<String> Sigma_B) {
		return null;
	}
	
	public static String quotienting(String x, String s) {
		return x+ "_" +s;
	}
	
	public static Assertion quotienting(Assertion f, LTS A, State s, Set<String> Sigma_B) {
		if(f instanceof MuTT) return new MuTT();
		else if(f instanceof MuFF) return new MuFF();
		else if(f instanceof MuVar) return new MuVar(quotienting(((MuVar) f).x, s.getLabel()));
		else if(f instanceof MuAnd) return new MuAnd(quotienting(((MuAnd) f).left, A, s, Sigma_B), quotienting(((MuAnd) f).right, A, s, Sigma_B));
		else if(f instanceof MuOr) return new MuOr(quotienting(((MuOr) f).left, A, s, Sigma_B), quotienting(((MuOr) f).right, A, s, Sigma_B));
		else if(f instanceof MuDia) {
			MuDia dia = (MuDia) f;
			if(!Sigma_B.contains(dia.a)) { // Sigma_A \ Gamma
				ArrayList<Assertion> part = new ArrayList<Assertion>();
				for(Transition t : A.getTransitions(s, dia.a))
					part.add(quotienting(dia.f, A, t.getDestination(), Sigma_B));
				return bigOr(part);
			}
			else if(!A.Sigma().contains(dia.a)) { // Sigma_B \ Gamma
				return new MuDia(dia.a, quotienting(dia.f, A, s, Sigma_B));
			}
			else { // Gamma
				ArrayList<Assertion> part = new ArrayList<Assertion>();
				for(Transition t : A.getTransitions(s, dia.a))
					part.add(new MuDia(dia.a, quotienting(dia.f, A, t.getDestination(), Sigma_B)));
				return bigOr(part);
			}
		}
		else if(f instanceof MuBox) {
			MuBox box = (MuBox) f;
			if(!Sigma_B.contains(box.a)) { // Sigma_A \ Gamma
				ArrayList<Assertion> part = new ArrayList<Assertion>();
				for(Transition t : A.getTransitions(s, box.a))
					part.add(quotienting(box.f, A, t.getDestination(), Sigma_B));
				return bigAnd(part);
			}
			else if(!A.Sigma().contains(box.a)) { // Sigma_B \ Gamma
				return new MuBox(box.a, quotienting(box.f, A, s, Sigma_B));
			}
			else { // Gamma
				ArrayList<Assertion> part = new ArrayList<Assertion>();
				for(Transition t : A.getTransitions(s, box.a))
					part.add(new MuBox(box.a, quotienting(box.f, A, t.getDestination(), Sigma_B)));
				return bigAnd(part);
			}
		}
		else assert(false); return null;
	}
	
	private static Assertion bigOr(ArrayList<Assertion> part) {
		if(part.isEmpty())
			return new MuFF();
		else {
			Assertion head = part.remove(0);
			Assertion tail = bigOr(part);
			if(tail instanceof MuFF)
				return head;
			else
				return new MuOr(head, tail);
		}
	}
	
	private static Assertion bigAnd(ArrayList<Assertion> part) {
		if(part.isEmpty())
			return new MuTT();
		else {
			Assertion head = part.remove(0);
			Assertion tail = bigAnd(part);
			if(tail instanceof MuTT)
				return head;
			else
				return new MuAnd(head, tail);
		}
	}

	public static MuSystem quotienting(MuSystem spec, LTS A, Set<String> Sigma_B) {
		MuSystem pSpec = new MuSystem();
		
		for(MuEquation me : spec.eq) {
			for(State s : A.states) {
				pSpec.eq.add(new MuEquation(quotienting(me.x, s.getLabel()), me.mu, quotienting(me.f, A, s, Sigma_B)));
			}
		}
		return pSpec;
	}
}
