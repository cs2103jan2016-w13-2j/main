//@@author A0130620B
package todolist.model;

public class SearchCommand {
	private String type = null;
	private String content = null;
	
	public SearchCommand(String type, String content) {
		this.type = type;
		this.content = content;
	}

    public String getType() {
    	return type;
    }
    
    public String getContent() {
    	return content;
    }

}
