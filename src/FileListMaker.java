import javax.swing.*;
import java.io.*;
import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.Scanner;

public class FileListMaker {
    static ArrayList<String> list = new ArrayList<>();
    public static boolean needsToBeSaved = false;
    public static boolean done = false;
    public static boolean save = false;
    public static void main(String[] args) throws FileNotFoundException {


        Scanner in = new Scanner(System.in);
        final String menu = "O - Open A - Add  D - Delete C - Clear \nV - View S - Save Q - Quit";

        String cmd = "";
        String item = "";
        int listRemove = 0;
        int listLength = 0;

        do {
            listLength = list.size();
            //display list
            displayList();
            //Display menu and get a menu choice (bulletproof with safeinput
            cmd = SafeInput.getRegExString(in, menu, "[OoAaDdCcVvSsQq]");
            cmd = cmd.toUpperCase();

            switch(cmd)
            {
                case "A":
                    //add item to list
                    addItem(in);
                    break;
                case "D":
                    //delete item from list
                   deleteItem(in);
                    break;
                case "V":
                    //display the list
                    displayList();
                    break;
                case "Q":
                    //quit / close program
                    quitProgram(in);
                    break;
                case "O":
                    //open a list file from disk
                    openListFile(in);
                    break;
                case  "C":
                    //clear (remove) all elements from current list
                    list.clear();
                    break;
                case "S":
                    //save current list file to disk
                    saveListFile();
                    break;
            }

        }while(!done);

    }

    private static void displayList() {
        System.out.println("---------------------------------------------------");
        if (list.size() != 0) {
            for (int x = 0; x < list.size(); x++) {
                System.out.printf("%3d         %-5s", x + 1, list.get(x));
                System.out.println();
            }
        } else {
            System.out.println("-------------------List is Empty-------------------");
        }
        System.out.println("---------------------------------------------------");

    }
    //prompt the user for list item (bulletproof with safeinput)
    //add to the list
    private static void addItem(Scanner scanner) {
        String item = SafeInput.getNonZeroLenString(scanner, "What would you like to add to the list?");
        list.add(item);
        needsToBeSaved = true;
    }

    private static void deleteItem(Scanner scanner) {
        if (list.isEmpty()) {
            System.out.println("List is empty.");
            return;
        }
        //prompt for number to remove from list (bulletproof with safeinput)
        //translate to index by subtracting 1
        int listRemove = SafeInput.getRangedInt(scanner, "Which item would you like to remove from the list?", 1, list.size()) -1;
        //remove from list
        list.remove(listRemove);
        needsToBeSaved = true;
    }

    private static void openListFile(Scanner scanner) throws FileNotFoundException {
        if (needsToBeSaved) {
            boolean response = SafeInput.getYNConfirm(scanner, "Save changes before loading a new list?");
            if (response = true) {
                saveListFile();
            }
        }
        JFileChooser chooser = new JFileChooser();
        int returnValue = chooser.showOpenDialog(null);
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            File selectedFile = chooser.getSelectedFile();
            try (BufferedReader reader = new BufferedReader(new FileReader(selectedFile)))
            {
                list.clear();
                String line;
                while ((line = reader.readLine()) != null) {
                    list.add(line);
                }
                needsToBeSaved = false;
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            } catch (IOException e) {
                System.err.println("Error Reading the File: " + e.getMessage());
            }
        } else {
            System.out.println("No file chosen.");
        }
    }

    private static void saveListFile() throws FileNotFoundException {
        if (list.isEmpty()) {
            System.out.println("List is empty; Nothing to Save.");
            return;
        }
        JFileChooser chooser = new JFileChooser();
        int returnVal = chooser.showSaveDialog(null);

        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File selectedFile = chooser.getSelectedFile();
            if (!selectedFile.getName().endsWith(".txt")) {
                selectedFile = new File(selectedFile.getAbsolutePath() + ".txt");
            }
            try (PrintWriter writer = new PrintWriter(selectedFile)) {
                for (String item : list) {
                    writer.println(item);
                }
                needsToBeSaved = false;
            } catch (IOException e) {
                System.err.println("Error saving the file: " + e.getMessage());
            }
        } else {
            System.out.println("Save Cancelled.");
        }
    }

    private static boolean quitProgram(Scanner scanner) throws FileNotFoundException {
        if (needsToBeSaved) {
            save = SafeInput.getYNConfirm(scanner, "Save unsaved changes before quitting?");
            if (save == true) {
                saveListFile();
            }
        }
        done = SafeInput.getYNConfirm(scanner, "Are you done?");
        return done;
    }
}