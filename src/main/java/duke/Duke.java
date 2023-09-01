package duke;

import commands.Command;
import parser.Parser;
import storage.DataFile;
import tasks.Task;
import tasks.TaskList;
import ui.Ui;
import java.io.*;
import java.util.ArrayList;

public class Duke {

    private final Ui ui;
    private final DataFile dataFile;
    private TaskList taskList;

    public Duke(String filePath, String fileName) {
        ui = new Ui();
        dataFile = new DataFile(filePath, fileName);
        try {
            taskList = new TaskList(dataFile.fileToObjects());
        } catch (IOException e) {
            System.out.println(e.getMessage());
            taskList = new TaskList(new ArrayList<Task>());
        }

    }

    public static void main(String[] args) {
        String filePath = "./data/";
        String fileName = "trying.txt";
        new Duke(filePath, fileName).run();
    }

    public void run() {
        ui.welcomeMsg();
        boolean run = true;
        while (run) {
            try {
                String command = ui.readCommand();
                Command c = new Parser().getCommand(command);
                c.execute(taskList, dataFile);
                run = !c.isExit();
            } catch (DukeException e) {
                System.out.println(e.getMessage());
            }
        }
    }
}
