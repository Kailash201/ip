import java.io.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Objects;
import java.util.Scanner;

public class Duke {

    private final Ui ui;
    private final DataFile dF;
    private TaskList tL;

    public Duke(String filePath, String fileName) {
        ui = new Ui();
        dF = new DataFile(filePath, fileName);
        try {
            tL = new TaskList(dF.fileToObjects());
        } catch (IOException e) {
            System.out.println(e.getMessage());
            tL = new TaskList(new ArrayList<Task>());
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
                Command c = new Command(command);
                c.execute(tL, dF);
                run = !c.isExit();
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        }
    }
}
