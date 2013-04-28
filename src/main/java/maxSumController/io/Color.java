package maxSumController.io;

import maxSumController.DiscreteVariableState;

public class Color implements DiscreteVariableState {

	public static final Color BLUE = new Color("BLUE");

	public static final Color RED = new Color("RED");

	public static final Color GREEN = new Color("GREEN");

	public static final Color YELLOW = new Color("YELLOW");

	private String name;

	private Integer hashCode;

	public Color(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	@Override
	public String toString() {
		return name;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Color) {
			Color other = (Color) obj;
			return other.getName().equals(name);
		}

		return false;
	}

	@Override
	public int hashCode() {
		if (hashCode == null) {
			hashCode = name.hashCode();
		}

		return hashCode;
	}

}
