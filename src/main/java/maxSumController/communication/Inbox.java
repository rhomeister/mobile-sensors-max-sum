package maxSumController.communication;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;

import maxSumController.FactorGraphNode;

public class Inbox {

	Map<Class<? extends Message>, RetentionPolicy> policies = new HashMap<Class<? extends Message>, RetentionPolicy>();

	Map<Class<? extends Message>, Collection<Message>> messages = new HashMap<Class<? extends Message>, Collection<Message>>();

	public Inbox() {
		setRetentionPolicy(MaxSumMessage.class, RetentionPolicy.REPLACE);
	}

	public void deliver(Message message) {
		RetentionPolicy retentionPolicy = getRetentionPolicy(message);

		Class<? extends Message> clazz = message.getClass();

		if (messages.get(clazz) == null)
			messages.put(clazz, new Vector<Message>());

		if (retentionPolicy == RetentionPolicy.REPLACE)
			removeMessageFromSameReceiver(message);

		messages.get(clazz).add(message);
	}

	private void removeMessageFromSameReceiver(Message message) {
		Collection<Message> typedMessages = messages.get(message.getClass());

		FactorGraphNode receiver = message.getReceiver();
		FactorGraphNode sender = message.getSender();

		for (Iterator iterator = typedMessages.iterator(); iterator.hasNext();) {
			Message message2 = (Message) iterator.next();

			if (receiver.equals(message2.getReceiver())
					&& sender.equals(message2.getSender())) {
				iterator.remove();
			}
		}
	}

	public void setRetentionPolicy(Class<? extends Message> type,
			RetentionPolicy policy) {
		policies.put(type, policy);
	}

	private RetentionPolicy getRetentionPolicy(Message message) {
		Class<? extends Object> type = message.getClass();

		while (type != null) {
			if (policies.containsKey(type))
				return policies.get(type);

			type = type.getSuperclass();
		}

		return RetentionPolicy.DEFAULT;
	}

	public <T extends Message> Collection<T> getMessages(Class<T> type) {
		Collection<T> result = new Vector<T>();

		for (Class<? extends Message> key : messages.keySet()) {
			if (type.isAssignableFrom(key))
				result.addAll((Collection<? extends T>) messages.get(key));
		}

		return result;
	}

	public <T extends Message> Collection<T> consumeMessages(Class<T> type) {
		Collection<T> result = getMessages(type);
		removeMessages(type);
		return result;
	}

	public void removeMessages(Class<? extends Message> type) {
		messages.put(type, new Vector<Message>());
	}
	
	@Override
	public String toString() {
		String result = "";
		
		for(Class<? extends Message> key : messages.keySet()) {
			result += key + " " + messages.get(key);
		}
		
		return result;
	}
}
