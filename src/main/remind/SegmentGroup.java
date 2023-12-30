package main.remind;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.border.*;

public class SegmentGroup {
	JTextField titleField;
    JComboBox<String> formatCombo, emojiCombo;
	JButton deleteButton;
    JPanel segmentPanel, elementInputPanel;
    ArrayList<ElementGroup> elementGroups;

    static ImageIcon deleteGraphic = Main.newScaledImage("deleteButton.png", 25, 25);
    static ImageIcon deleteHoveredGraphic = Main.newScaledImage("deleteHovered.png", 25, 25);
	
	public SegmentGroup(JTextField titleField, JComboBox<String> formatCombo, JComboBox<String> emojiCombo, JButton deleteButton, JPanel segmentPanel, JPanel elementInputPanel, ArrayList<ElementGroup> elementGroups){
        this.titleField = titleField;
        this.formatCombo = formatCombo;
        this.emojiCombo = emojiCombo;
        this.deleteButton = deleteButton;
        this.segmentPanel = segmentPanel;
        this.elementInputPanel = elementInputPanel;
        this.elementGroups = elementGroups;
	}

    public static SegmentGroup createNewSegmentGroup(ReminderGroup RG){
        JPanel segmentPanel = Main.createNewPanel(510, 430, new Color(0xF6DEB6), null);

        JLabel segmentIndicator1 = new JLabel("--");
        segmentIndicator1.setFont(new Font("Segoe UI", Font.PLAIN, 30));
        segmentIndicator1.setBounds(15,15, 50,20);

        JTextField segmentTitleField = new JTextField("Segment Title");
        segmentTitleField.setFont(new Font("Segoe UI", Font.PLAIN, 20));
        segmentTitleField.setBounds(50,10, 180,40);
        segmentTitleField.setToolTipText("Segment Title");
        Border defaultBorder = segmentTitleField.getBorder();
        Border combine = BorderFactory.createCompoundBorder(defaultBorder, BorderFactory.createEmptyBorder(0, 5, 5, 4)); 
        Border combineRed = BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(new Color(0xE07A5F), 1), BorderFactory.createEmptyBorder(0, 5, 5, 4));
        segmentTitleField.setBorder(combine);
        segmentTitleField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e){
                String text = segmentTitleField.getText();
                if (text.isEmpty() || (text.contains("_"))) {
                    segmentTitleField.setBorder(combineRed);
                } else {
                    segmentTitleField.setBorder(combine);
                }
            }
        });

        JLabel segmentIndicator2 = new JLabel("--");
        segmentIndicator2.setFont(new Font("Segoe UI", Font.PLAIN, 30));
        segmentIndicator2.setBounds(240,15,50,20);

        String formatChoices[] = {"> subject - content, deadline", "> subject - content", "> content, deadline", "> content"};
        JComboBox<String> segmentFormatCombo = new JComboBox<String>(formatChoices);
        segmentFormatCombo.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        segmentFormatCombo.setBounds(330,15, 180,30);
        segmentFormatCombo.setToolTipText("Format style");
        segmentFormatCombo.setBackground(Color.white); segmentFormatCombo.setForeground(Color.darkGray);
        segmentFormatCombo.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                for (SegmentGroup sg: Main.reminderGroup.segmentGroups){
                    if (sg.formatCombo == segmentFormatCombo){
                        ElementGroup eg = sg.elementGroups.get(sg.elementGroups.size() - 1);
                        eg.formatCombo.setSelectedIndex(sg.formatCombo.getSelectedIndex());
                    }
                }
            }
        });

        JComboBox<String> segmentEmojiCombo = new JComboBox<String>(Main.emojis);
        segmentEmojiCombo.setBounds(270,15, 50,30);
        segmentEmojiCombo.setFont(new Font("Segoe UI Emoji", Font.BOLD, 10));
        segmentEmojiCombo.setForeground(new Color(0x3D405B));
        segmentEmojiCombo.setBorder(new EmptyBorder(0, 0, 0, 0));
        segmentEmojiCombo.setToolTipText("Bullet style");
        segmentEmojiCombo.setBackground(Color.white); segmentEmojiCombo.setForeground(Color.darkGray);
        segmentEmojiCombo.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Main.refresh(Main.reminderGroup);
            }
        });
        
        
        JPanel elementInputPanel = Main.createNewPanel(500, 330, new Color(0xF0ECD1), null);
        elementInputPanel.setLayout(new BoxLayout(elementInputPanel, BoxLayout.Y_AXIS));
        elementInputPanel.setBounds(10,60,500,330);
        elementInputPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JScrollPane elementInputPane = ScrollPaneFactory.newScrollPane(elementInputPanel);
        elementInputPane.setBounds(10,60,500,330);
        elementInputPane.setBorder(new EmptyBorder(0, 0, 0, 0));
        elementInputPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        JButton deleteButton = Main.createNewButton("Delete Segment", new Font("Segoe UI", Font.BOLD, 13), 
        new Insets(3, 3, 3, 3), new Color(0x3D405B), new Color(0xF4F1DE), "Delete currently displayed segment");
        deleteButton.setBounds(10,420,180,30);
        deleteButton.setIcon(deleteGraphic);
        deleteButton.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                for (SegmentGroup sg: RG.segmentGroups){
                    if (sg.deleteButton == e.getSource() && RG.segmentGroups.size() > 1){
                        int x = JOptionPane.showConfirmDialog(null, "Are you sure you want to delete \"" + sg.titleField.getText() +  "\" segment?", "Reminders", JOptionPane.YES_NO_OPTION);
                        if (x == 0){
                            if (RG.segmentGroups.size() == Main.currentPage){
                            Main.currentPage--;
                            Main.cardLayout.previous(Main.containerPanel);
                            RG.segmentGroups.remove(sg);
                            Main.containerPanel.remove(sg.segmentPanel);
                            } else {
                                RG.segmentGroups.remove(sg);
                                Main.containerPanel.remove(sg.segmentPanel);
                            }
                        }
                        break;
                    } else if (RG.segmentGroups.size() <= 1) {
                        JOptionPane.showMessageDialog(null, "Leave atleast 1 Segment.", "Reminders", JOptionPane.INFORMATION_MESSAGE);
                    }
                }
                Main.refresh(Main.reminderGroup);
            }
        });
        deleteButton.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                deleteButton.setBackground(new Color(0xE07A5F));
                deleteButton.setIcon(deleteHoveredGraphic);
            }

            public void mouseExited(MouseEvent e) {
                deleteButton.setBackground(new Color(0x3D405B));
                deleteButton.setIcon(deleteGraphic);
            }
        });
        

        segmentPanel.add(segmentIndicator1); segmentPanel.add(segmentTitleField); segmentPanel.add(segmentIndicator2); 
        segmentPanel.add(segmentFormatCombo); segmentPanel.add(segmentEmojiCombo); segmentPanel.add(elementInputPane);
        segmentPanel.add(deleteButton);


        return (new SegmentGroup(segmentTitleField, segmentFormatCombo, segmentEmojiCombo, deleteButton, segmentPanel, elementInputPanel, new ArrayList<ElementGroup>()));
        
    }
}
