package branchNbound;

import branchNbound.State;


public class DummyState implements State {
	private String name;

	public DummyState(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}
}
