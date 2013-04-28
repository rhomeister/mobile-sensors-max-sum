package maxSumController.communication;

import maxSumController.FactorGraphNode;
import maxSumController.MarginalValues;

import org.apache.commons.lang.Validate;
import org.apache.commons.lang.builder.HashCodeBuilder;

/**
 * 
 * @author sandrof
 * 
 *         Represents an interface for messages used in the MaxSum algorithm
 *         (function to variable or variable to function)
 * 
 */

public abstract class MaxSumMessage<S extends FactorGraphNode, R extends FactorGraphNode>
		extends Message<S, R, MarginalValues<?>> {

	// protected MarginalValues<?> function;

	public MaxSumMessage(S sender, R receiver, MarginalValues<?> function) {
		super(sender, receiver, function);
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(getSender()).append(getReceiver())
				.toHashCode();
	}

	@Override
	public boolean equals(Object other) {
		return other.hashCode() == hashCode();
	}

	public MarginalValues<?> getMarginalFunction() {
		return getContents();
	}

	/**
	 * Returns the size of this message in the number of doubles it can be
	 * represented with
	 * 
	 * @return
	 */
	public int getSize() {
		Validate.notNull(getContents());
		return getContents().getSize();
	}
}
