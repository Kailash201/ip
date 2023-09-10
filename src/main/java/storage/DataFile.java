package storage;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;

import tasks.Deadline;
import tasks.Event;
import tasks.Task;
import tasks.Todo;

/**
 * Handles all the manipulation of the file that is stored
 * locally.
 */
public class DataFile {

    private final String filePath;
    private final String fileName;
    private File file;

    /**
     * DateFile constructor that takes in String, String.
     * @param filePath Name of the path.
     * @param fileName Name of the file.
     */
    public DataFile(String filePath, String fileName) {
        this.fileName = fileName;
        this.filePath = filePath;
        createDirectoryNFileIfFileDoNotExists();
        assert file.exists();
    }

    /**
     * Writes data into file.
     * @param task The task that is being written into the file.
     * @throws IOException If any file issues exists.
     */
    public void writeToFile(Task task) throws IOException {
        FileWriter fW = new FileWriter(file, true);
        String div = "*|,";
        StringBuilder custom = new StringBuilder();
        if (task instanceof Todo) {
            Todo td = (Todo) task;
            custom.append("T").append(div).append(td.getDesc()).append(div)
                    .append(td.getStatus().equals("X") ? 1 : 0)
                    .append(System.lineSeparator());
        } else if (task instanceof Deadline) {
            Deadline dl = (Deadline) task;
            custom.append("D").append(div).append(dl.getDesc()).append(div).append(dl.getBy()).append(div)
                    .append(dl.getStatus().equals("X") ? 1 : 0)
                    .append(System.lineSeparator());
        } else if (task instanceof Event) {
            Event eve = (Event) task;
            custom.append("E").append(div).append(eve.getDesc()).append(div)
                    .append(eve.getFrom()).append(div).append(eve.getTo()).append(div)
                    .append(eve.getStatus().equals("X") ? 1 : 0)
                    .append(System.lineSeparator());
        } else {
            assert false;
        }
        fW.write(custom.toString());
        fW.close();
    }

    /**
     * Edit the marks or unmarks of a task in the file.
     * @param n The line to be edited.
     * @param newChar The mark to be replaced.
     * @throws IOException If any file issues exists.
     */
    public void editFileAtLineN(int n, char newChar) throws IOException {
        // n starts from 0
        BufferedReader reader = new BufferedReader(new FileReader(file));
        String line = reader.readLine();
        StringBuilder oldContent = new StringBuilder();
        int i = 0;
        while (line != null) {
            if (i == n) {
                StringBuilder sB = new StringBuilder(line);
                sB.setCharAt(line.length() - 1, newChar);
                line = sB.toString();
            }
            oldContent.append(line).append(System.lineSeparator());
            line = reader.readLine();
            i++;
        }
        FileWriter fW = new FileWriter(filePath + "/" + fileName);
        fW.write(oldContent.toString());
        fW.close();
        assert !oldContent.toString().isEmpty();
    }

    /**
     * Deletes a task from the file at line n.
     * @param n The line to be deleted.
     * @throws IOException If any file issues exists.
     */
    public void deleteTaskFromFile(int n) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(file));
        String line = reader.readLine();
        StringBuilder oldContent = new StringBuilder();
        int i = 0;
        while (line != null) {
            if (i != n) {
                oldContent.append(line).append(System.lineSeparator());
            }
            line = reader.readLine();
            i++;
        }
        FileWriter fW = new FileWriter(file);
        fW.write(oldContent.toString());
        fW.close();
    }

    /**
     * Read the data from the file and convert it to list of Tasks object.
     * @return List of Tasks in ArrayList.
     * @throws IOException If any file issues exists.
     */
    public ArrayList<Task> fileToObjects() throws IOException {
        ArrayList<Task> tasks = new ArrayList<>();
        BufferedReader reader = new BufferedReader(new FileReader(file));
        String line = reader.readLine();
        String div = "*|,";
        Task task = new Task("empty");

        while (line != null) {
            if (line.charAt(0) == 'T') {
                String desc = line.substring(div.length() + 1, line.lastIndexOf(div));
                task = new Todo(desc);

            } else if (line.charAt(0) == 'D') {
                String sub = line.substring(div.length() + 1);
                String desc = sub.substring(0, sub.indexOf(div));
                int firstLine = sub.indexOf(div) + div.length();
                String by = sub.substring(firstLine, sub.indexOf(div, firstLine));
                task = new Deadline(desc, LocalDateTime.parse(by));

            } else if (line.charAt(0) == 'E') {
                String sub = line.substring(div.length() + 1);
                String desc = sub.substring(0, sub.indexOf(div));
                int firstLine = sub.indexOf(div) + div.length();
                String from = sub.substring(firstLine, sub.indexOf(div, firstLine));
                int secLine = sub.indexOf(div, firstLine) + div.length();
                String to = sub.substring(secLine, sub.indexOf(div, secLine));
                task = new Event(desc, LocalDateTime.parse(from), LocalDateTime.parse(to));
            } else {
                assert false;
            }
            // check if task is completed
            if (line.charAt(line.length() - 1) == '1') {
                task.taskCompleted();
            } else {
                task.taskNotCompleted();
            }
            tasks.add(task);
            line = reader.readLine();
        }
        return tasks;
    }

    /**
     * Checks if file or directory exists. If it does not
     * exist, makes a new file or directory.
     */
    private void createDirectoryNFileIfFileDoNotExists() {
        File directory = new File(filePath);
        File localFile = new File(filePath + "/" + fileName);
        if (!directory.exists()) {
            directory.mkdir();
            try {
                new FileWriter(filePath + "/" + fileName);
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
            file = new File(filePath + "/" + fileName);
        } else if (!localFile.exists()) {
            try {
                new FileWriter(filePath + "/" + fileName);
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
            file = new File(filePath + "/" + fileName);
        } else {
            file = localFile;
        }
    }
}
