import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import static java.awt.Desktop.getDesktop;

public class MenuPanel extends JPanel {

    private final ParentPanel parentPanel;
    private BingoParent bingoParent;
    private final CardPanel cardPanel;

    public MenuPanel(ParentPanel parentPanel) {
        this.parentPanel = parentPanel;

        setBackground(new Color(230, 69, 69));
        askPrompts();

        cardPanel = new CardPanel(parentPanel, bingoParent);
    }

    private void askPrompts() {
        String input = "";
        int gameNumber;
        int numOfBingoCards;
        int numOfDays;
        int numOfWinners;

        try {
            input = getInput("Enter game number: ");
            gameNumber = Integer.parseInt(input);

            input = getInput("Enter number of bingo cards: ");
            numOfBingoCards = Integer.parseInt(input);

            if (!checkInput(numOfBingoCards, Integer.MAX_VALUE, "title")) {
                return;
            }

            input = getInput("Enter number of days to play (max of 5 days): ");
            numOfDays = Integer.parseInt(input);

            if (!checkInput(numOfDays, 5, "title")) {
                return;
            }

            input = getInput("Enter number of winners (max of " + numOfBingoCards + " winners): ");
            numOfWinners = Integer.parseInt(input);

            if (!checkInput(numOfWinners, numOfBingoCards, "title")) {
                return;
            }

            JOptionPane.showMessageDialog(null, "Create and select folder where generated files will be stored.", "Folder Selection", JOptionPane.PLAIN_MESSAGE);

            String filePath = getFileInput();

            if (filePath == null) {
                JOptionPane.showMessageDialog(null, "No folder selected or selected folder does not exist.", "ERROR", JOptionPane.ERROR_MESSAGE);
                parentPanel.changePanel("title");
            }  else {

                bingoParent = new BingoParent(gameNumber, numOfBingoCards, numOfDays, numOfWinners, filePath, parentPanel);
                setButtons(filePath);

                parentPanel.add(this, "menu");
            }
        } catch (NumberFormatException e) {

            String msg;

            if (input == null) {
                msg = "Input not found.";
            } else if (input.matches("[0-9]+")) {
                msg = "Input exceeded integer limit.";
            } else {
                msg = "Input must only include numbers.";
            }

            System.out.println("Exception error: " + e.getMessage());
            JOptionPane.showMessageDialog(null, msg, "ERROR", JOptionPane.ERROR_MESSAGE);
            parentPanel.changePanel("title");
        }
    }

    private void setButtons(String filePath) {
        JButton openDirectory = new JButton("OPEN DIRECTORY");
        JButton viewCard = new JButton("VIEW CARD");
        JButton gameInfo = new JButton("GAME INFO");

        openDirectory.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    getDesktop().open(new File(filePath));
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(null, "Directory cannot be found.", "ERROR", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        viewCard.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String input = JOptionPane.showInputDialog(null, "Enter card ID number (max ID of " + bingoParent.getMaxBingoCards() + "): ", "View Card", JOptionPane.PLAIN_MESSAGE);

                try {
                    int id = Integer.parseInt(input);

                    if (!checkInput(id, bingoParent.getMaxBingoCards(), "menu")) {
                        return;
                    }

                    cardPanel.changeCard(bingoParent.getCard(id));
                    parentPanel.changePanel("card");

                } catch (NumberFormatException ex) {
                    String msg;

                    if (input == null) {
                        msg = "Input not found.";
                    } else if (input.matches("[0-9]+")) {
                        msg = "Input exceeded integer limit.";
                    } else {
                        msg = "Input must only include numbers.";
                    }

                    System.out.println("Exception error: " + ex.getMessage());
                    JOptionPane.showMessageDialog(null, msg, "ERROR", JOptionPane.ERROR_MESSAGE);
                    parentPanel.changePanel("menu");
                }
            }
        });

        add(openDirectory);
        add(viewCard);
        add(gameInfo);
    }

    private String getInput(String message) {
        return JOptionPane.showInputDialog(null, message, "Parameter Selection", JOptionPane.PLAIN_MESSAGE);
    }

    private boolean checkInput(int input, int max, String changePanel) {
        if (input <= 0 || input > max) {
            JOptionPane.showMessageDialog(null, "Invalid Input.", "ERROR", JOptionPane.ERROR_MESSAGE);
            parentPanel.changePanel(changePanel);
            return false;
        }

        return true;
    }

    private String getFileInput() {
        JFileChooser chooser = new JFileChooser();
        chooser.setCurrentDirectory(new File
                (System.getProperty("user.home") + System.getProperty("file.separator")+ "Downloads"));
        chooser.setDialogTitle("Select folder directory");
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        chooser.setAcceptAllFileFilterUsed(false);

        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            System.out.println("getCurrentDirectory(): "
                    +  chooser.getCurrentDirectory());
            System.out.println("getSelectedFile() : "
                    +  chooser.getSelectedFile());

            File file = chooser.getSelectedFile();

            if (!file.exists()) {
                System.out.println("Folder does not exist");
                return null;
            } else {
                System.out.println("Folder does exist");
            }

            return chooser.getSelectedFile().toString();
        }
        else {
            System.out.println("No Selection ");
            return null;
        }
    }
}
