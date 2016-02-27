package todolist.model;

import java.util.ArrayList;

public static class UIHandler {
    
    private DataBase database;
    
    private Boolean isSorted = false;
    private Boolean isFiltered = false;
    
    private String fieldName = null;
    private String category = null;
    private String order = null;
    
    public UIHandler(DataBase database) {
        this.database = database;
    }
    
    public void refresh() {
        if(isSorted&&!isFiltered) {
            this.sort(fieldName, order);
        } 
        
        if(!isSorted&&isFiltered) {
            this.filter(category);
        }
        
        if(isFiltered&&isSorted) {
            ArrayList<Task> tempTaskList = new Task(database.retreive(new SearchCommand("Category", category));
            UI.display(Sorter.sort(tempTaskList));
        }
        
        if(!isSorted&&!isFiltered) {
            UI.display(database.retrieveAll());
        }
    }
    
    public void sendMessage(String message) {
        UI.messageBox(message);
    }
    
    public void highLight(Task task) {
        UI.highLight(task);
    }
    
    public void search(String title) {
        ArrayList<Task> tempTaskList = new Task(database.retreive(new SearchCommand("Name", title));
        UI.highLight(tempTaskList);
    }
    
    public void sort(String fieldName, String order) {
        if(isFiltered) {
            this.sort = sort;
            ArrayList<Task> tempTaskList = new Task(database.retreive(new SearchCommand("Category", category));
            UI.display(Sorter.sort(tempTaskList));
            isSorted = true;
        } else {
            this.sort = sort;
            UI.display(Sorter.sort(database.retrieveAll()));
            isSorted = true;
        }
    }
    
    public void filter(String category) {
        if(isSorted) {
            this.category = category;
            ArrayList<Task> tempTaskList = new Tdatabase.retreiveAll());
            UI.display(Sorter.sort(tempTaskList));
            isFiltered = true;
        } else {
            this.category = category;
            ArrayList<Task> tempTaskList = new Task(database.retreive(new SearchCommand("Category", category));
            UI.display(tempTaskList);
            isFiltered = true;
        }
    }
    
    public void exit() {
        System.exit(0);
    }
    
    public String retrieve() {
        String userInput = null;
        return userInput;
    }
    
    public String enter() {
        return false;;
    }
}