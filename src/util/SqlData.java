package util;

import java.util.List;

public class SqlData {
	private String columns;
	private List<String> values;
	
	public SqlData(String columns,List<String> values) {
		this.values=values;
		this.columns=columns;
	}

	public String getColumns() {
		return columns;
	}

	public List<String> getValues() {
		return values;
	}
}
