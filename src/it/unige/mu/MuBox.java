package it.unige.mu;

public class MuBox implements Assertion {
	
	public String a;
	public Assertion f;
	
	public MuBox(String a, Assertion f) {
		super();
		this.a = a;
		this.f = f;
	}

	@Override
	public String toString() {
		return "[" + a + "] " + f.toString();
	}
	
	@Override
	public int size() {
		return f.size() + 1;
	}
}
