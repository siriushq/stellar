package org.apache.log4j;

import sirius.stellar.facility.Strings;

import java.util.Stack;

/**
 * Shadow class for {@code org.apache.log4j.NDC}.
 *
 * @author Mechite
 * @since 1.0
 */
public final class NDC {

	private static final Stack<String> ndc = new Stack<>();

	private NDC() {
	}

	public static void clear() {
		ndc.clear();
	}

	public static Stack cloneStack() {
		if (ndc.isEmpty()) return null;
		return ndc;
	}

	public static void inherit(Stack stack) {
        ndc.clear();
        ndc.addAll(stack);
	}

	public static int getDepth() {
		return ndc.size();
	}

	public static String pop() {
		if (ndc.isEmpty()) return Strings.EMPTY;
		return ndc.pop();
	}

	public static String peek() {
		if (ndc.isEmpty()) return Strings.EMPTY;
		return ndc.peek();
	}

	public static void push(String message) {
		ndc.push(message);
	}

	public static void remove() {
		ndc.remove(ndc.get(0));
	}

	public static void setMaxDepth(int maxDepth) {
		while (ndc.size() > maxDepth) ndc.pop();
	}
}