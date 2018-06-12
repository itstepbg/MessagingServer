package storage;

import java.util.ArrayList;

import exceptions.InvalidWhereClauseException;

public class WCB {
	private static final String LOGICAL_AND = " AND ";
	private static final String LOGICAL_OR = " OR ";
	private static final String WHERE = " WHERE ";
	private static final String CONDITION_EQUALS = " = ";

	private ArrayList<String> columns = new ArrayList<String>();
	private ArrayList<String> values = new ArrayList<String>();
	private ArrayList<String> logicalOperators = new ArrayList<String>();

	public WCB eq(String column, String value) {
		columns.add(column);
		values.add(value);

		return this;
	}

	public WCB and() {
		logicalOperators.add(LOGICAL_AND);

		return this;
	}

	public WCB or() {
		logicalOperators.add(LOGICAL_OR);

		return this;
	}

	public String build() throws InvalidWhereClauseException {
		if (logicalOperators.size() != columns.size() - 1) {
			throw new InvalidWhereClauseException("Condition and logical operator count mismatch.");
		}
		
		StringBuilder sBuilder = new StringBuilder();
		sBuilder.append(WHERE);
		for (int i = 0; i < columns.size(); i++) {
			sBuilder.append(columns.get(i)).append(CONDITION_EQUALS).append(values.get(i));
			if (i < columns.size() - 1) {
				sBuilder.append(logicalOperators.get(i));
			}
		}
		return sBuilder.toString();
	}
}
