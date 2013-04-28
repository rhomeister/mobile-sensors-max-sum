package maxSumController.io;

import maxSumController.discrete.DiscreteVariableDomainImpl;

public class ColorDomain extends DiscreteVariableDomainImpl<Color> {

	public ColorDomain(int numberOfColors) {
		for (int i = 0; i < numberOfColors; i++) {
			add(new Color("" + i));
		}
	}

	public ColorDomain() {
	}
}
