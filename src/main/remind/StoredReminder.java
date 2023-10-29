package main.remind;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.border.*;

public class StoredReminder {
    JTextArea textArea;
    JScrollPane textAreaPane;
    JLabel titleLabel;
    JPanel containerPanel;
    MouseAdapter mAdapter;
    Reminder reminder;

    public StoredReminder(JTextArea textArea, JScrollPane textAreaPane, JLabel titleLabel, JPanel containerPanel, MouseAdapter mAdapter, Reminder reminder){
        this.textArea = textArea;
        this.textAreaPane = textAreaPane;
        this.titleLabel = titleLabel;
        this.containerPanel = containerPanel;
        this.mAdapter = mAdapter;
        this.reminder = reminder;
    }

    public static StoredReminder createStoredReminder(ArrayList<String> dataList){
        Reminder reminder = Main.decodeAsObjects(dataList);

        

        JTextArea textArea = Main.createNewTextArea(new Font("Segoe UI Emoji", Font.PLAIN, 15), 
        new Insets(10, 0, 10, 20), new Color(0xF4F1DE));
        textArea.setText(Main.decodeForTextArea(dataList));
        textArea.setEditable(false);
        textArea.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        textArea.setFocusTraversalKeysEnabled(false);
        
        JScrollPane textAreaPane = ScrollPaneFactory.newScrollPane(textArea);
        textAreaPane.setPreferredSize(new Dimension(320, 490));
        textAreaPane.setBorder(new EmptyBorder(0, 0, 0, 0));
        KeyListener[] keyListeners = textAreaPane.getKeyListeners();
        for (KeyListener listener : keyListeners) {
            textAreaPane.removeKeyListener(listener);
        }

        JLabel titleLabel = new JLabel(reminder.title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setPreferredSize(new Dimension(320, 30));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 3,0));
        titleLabel.setForeground(new Color(0x181A25));
        titleLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        
        JPanel containerPanel = Main.createNewPanel(340, 550, new Color(0xF4F1DE), new FlowLayout(FlowLayout.LEADING, 0,10));
        containerPanel.setPreferredSize(new Dimension(340, 550));
        containerPanel.setMinimumSize(containerPanel.getPreferredSize());
        containerPanel.setMaximumSize(containerPanel.getPreferredSize());
        containerPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 0));
        containerPanel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        

        containerPanel.add(titleLabel); containerPanel.add(textAreaPane);

        Color defaultColor = new Color(0xF4F1DE);
        Color brighterColor = new Color(0xFAF9F0);

        MouseAdapter mAdapter = new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                // Perform the action when the text area is clicked
                for (StoredReminder sr: Main.remindersCollection){
                    if (sr.textArea == ((Component) e.getSource()) || sr.titleLabel == ((Component) e.getSource()) || sr.containerPanel == ((Component) e.getSource())){
                        Main.selectedStoredReminder = sr; sr.containerPanel.setBorder(null); sr.containerPanel.setOpaque(false); sr.textArea.setVisible(false);
                        sr.titleLabel.setVisible(false); sr.textAreaPane.setVisible(false);
                        if (Main.currentlyUsing == Main.data.get(Main.remindersCollection.indexOf(sr))){
                            Main.useButton.setEnabled(false); Main.deleteButton.setEnabled(false);
                        }
                        Main.selectedGroup.textArea.setText(sr.textArea.getText());
                        Main.selectedGroup.titleLabel.setText(sr.reminder.title);
                        
                        break;
                    }

                }
                JComponent view = (JComponent) Main.navigationPane.getViewport().getView();
                view.setEnabled(false); Main.navigationPane.setEnabled(false);
                                
                Main.frame.setComponentZOrder(Main.lowOpacityBackground, 0);
                Main.lowOpacityBackground.setVisible(true);

                SwingUtilities.invokeLater(() -> Main.selectedGroup.textArea.requestFocus());
            }

            public void mouseEntered(MouseEvent e) {
                containerPanel.setBackground(brighterColor);
                textArea.setBackground(brighterColor);
            }

            public void mouseExited(MouseEvent e) {
                if (!(Main.lowOpacityBackground.isVisible())){
                    containerPanel.setBackground(defaultColor);
                    textArea.setBackground(defaultColor);
                }
            }
        };

        textArea.addMouseListener(mAdapter); titleLabel.addMouseListener(mAdapter); containerPanel.addMouseListener(mAdapter);


        return (new StoredReminder(textArea, textAreaPane, titleLabel, containerPanel, mAdapter, reminder));
    } 
}
