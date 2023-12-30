package main.remind;

import javax.swing.*;
import javax.swing.border.Border;

import java.awt.*;
import java.awt.event.*;
import com.raven.datechooser.*;
import main.remind.Main.GroupType;

public class ElementGroup {
	JTextField subjectField, contentField, deadlineField;
    DateChooser dateChooser;
    JLabel subjectLabel, contentLabel, deadlineLabel, formatLabel;
    JComboBox<String> formatCombo;
    JButton addButton, removeButton;
    JLayeredPane elementPanel;

    static Border defaultBorder = null;

    static ImageIcon deleteGraphic = Main.newScaledImage("deleteButton.png", 30, 30);
    static ImageIcon deleteHoveredGraphic = Main.newScaledImage("deleteHovered.png", 30, 30);
	
	ElementGroup(JLabel subjectLabel, JTextField subjectField, JLabel contentLabel, JTextField contentField, JLabel deadlineLabel, JTextField deadlineField,  JLabel formatLabel, JComboBox<String> formatCombo, DateChooser dateChooser, JButton addButton, JButton removeButton, JLayeredPane elementPanel){
		this.subjectLabel = subjectLabel;
        this.subjectField = subjectField;
        this.contentLabel = contentLabel;
		this.contentField = contentField;
        this.deadlineLabel = deadlineLabel;
        this.deadlineField = deadlineField;
        this.formatLabel = formatLabel;
        this.formatCombo = formatCombo;
        this.dateChooser = dateChooser;
        this.addButton = addButton;
		this.removeButton = removeButton;
        this.elementPanel = elementPanel;
	}

    public static ElementGroup createNewElementGroup(String emoji, int format, Main.GroupType type, ReminderGroup rg){
        JLayeredPane elementPanel = new JLayeredPane();
        elementPanel.setBackground(new Color(0xFAF9F0));
        int width = 460; int height = 70;
        elementPanel.setPreferredSize(new Dimension(width,height));
        elementPanel.setMaximumSize(elementPanel.getPreferredSize());
        elementPanel.setMinimumSize(elementPanel.getPreferredSize());
        elementPanel.setVisible(true);

        JLabel elementSubjectLabel = new JLabel(emoji + " Subject:");
        elementSubjectLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 14));
        elementSubjectLabel.setPreferredSize(new Dimension(70,24));

        JTextField elementSubjectField = new JTextField();
        elementSubjectField.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 11));
        elementSubjectField.setPreferredSize(new Dimension(50,24));
        elementSubjectField.setToolTipText("Type element's subject name here");

        JLabel elementContentLabel = new JLabel("Content:");
        elementContentLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 14));

        JTextField elementContentField = new JTextField();
        elementContentField.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        elementContentField.setPreferredSize(new Dimension(330,24));
        elementContentField.setToolTipText("Type element's content here");
        defaultBorder = elementContentField.getBorder();

        JPanel elementContentWrapper = new JPanel();
        elementContentWrapper.setLayout(new FlowLayout(FlowLayout.LEADING, 7, 0));
        elementContentWrapper.setPreferredSize(new Dimension(400, 26));
        elementContentWrapper.setBackground(Color.white); elementContentWrapper.setOpaque(false); 
        elementContentWrapper.setFocusTraversalPolicy(new LayoutFocusTraversalPolicy() {
            @Override
            public Component getDefaultComponent(Container focusCycleRoot) {
                return elementContentField;
            }
        });
        elementContentWrapper.add(elementContentLabel); elementContentWrapper.add(elementContentField);

        JLabel elementDeadlineLabel = new JLabel("Deadline:");
        elementDeadlineLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        JTextField elementDeadlineField = new JTextField();
        elementDeadlineField.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        elementDeadlineField.setPreferredSize(new Dimension(110,24));
        elementDeadlineField.setToolTipText("Pick a date for the element's deadline");
        DateChooser datePicker = new DateChooser();
        datePicker.setDateFormat("MMMM-dd-yyyy"); datePicker.setFocusable(false);
        datePicker.setForeground(new Color(0x3D405B));
        datePicker.setTextRefernce(elementDeadlineField);
        elementDeadlineField.setBackground(Color.white);
        
        FocusAdapter fAdapter = new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e){
                JTextField textField = ((JTextField) e.getSource());
                String text = textField.getText();
                
                if (text.isEmpty() || (text.contains("_") || text.contains("~"))) {
                    textField.setBorder(BorderFactory.createLineBorder(new Color(0xE07A5F)));
                } else {
                    textField.setBorder(defaultBorder);
                }
            }
        };
        elementSubjectField.addFocusListener(fAdapter); elementContentField.addFocusListener(fAdapter);

        JLabel elementFormatLabel = new JLabel("Format:");
        elementFormatLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 14));

        String formatChoices[] = {"s - c, d", "s - c" ,"c, d", "c"};
        JComboBox<String> elementFormatCombo = new JComboBox<String>(formatChoices);
        elementFormatCombo.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        elementFormatCombo.setPreferredSize(new Dimension(70,24));
        elementFormatCombo.setToolTipText("Element format style");
        elementFormatCombo.setBackground(Color.white); elementFormatCombo.setForeground(Color.darkGray);
        elementFormatCombo.setSelectedIndex(format);
        elementFormatCombo.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Main.refresh(Main.reminderGroup);
            }
        });


        JButton removeButton = Main.createNewButton(null, new Font("Segoe UI Emoji", Font.BOLD, 10), 
        new Insets(3, 3, 3, 3), new Color(0x3D405B), new Color(0xF4F1DE), "Delete element");
        removeButton.setIcon(deleteGraphic);
        removeButton.setPreferredSize(new Dimension(30,30));
        removeButton.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                for (SegmentGroup sg: rg.segmentGroups){
                    if (sg.elementInputPanel == removeButton.getParent().getParent().getParent()){
                        Component[] components = sg.elementInputPanel.getComponents();
                        for (ElementGroup eg: sg.elementGroups){
                            if (eg.removeButton == e.getSource()){
                                for (int i = 0; i < components.length; i++) {
                                    if (components[i] == eg.elementPanel) {
                                        sg.elementInputPanel.remove(components[i + 1]);
                                        break;
                                    }
                                }
                                
                                sg.elementGroups.remove(eg);
                                sg.elementInputPanel.remove(eg.elementPanel);
                                break;
                            }
                        }
                        break;
                    }
                }
                Main.refresh(Main.reminderGroup);
            }
        });
        removeButton.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                removeButton.setIcon(deleteHoveredGraphic);
            }

            public void mouseExited(MouseEvent e) {
                removeButton.setIcon(deleteGraphic);
            }
        });

        JPanel componentsContainer = new JPanel();
        componentsContainer.setLayout(new FlowLayout(FlowLayout.LEADING, 7, 5));
        componentsContainer.setBounds(0, 0, width, height);
        componentsContainer.setBackground(new Color(0xFAF9F0));
        componentsContainer.setFocusTraversalPolicy(new LayoutFocusTraversalPolicy() {
            @Override
            public Component getDefaultComponent(Container focusCycleRoot) {
                JTextField defaultField = new JTextField();
                if (format == 0){
                    defaultField = elementSubjectField;
                } else if (format == 1){
                    defaultField = elementDeadlineField;
                } else {
                    defaultField = elementContentField;
                }
                return defaultField;
            }
        });

        KeyAdapter keyAdapter = new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    KeyboardFocusManager.getCurrentKeyboardFocusManager().focusNextComponent();
                    SwingUtilities.invokeLater(new Runnable() {
                        public void run() { if (KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusOwner() == elementDeadlineField){
                                datePicker.showPopup(); }
                        }});
                } 
            }
        };

        elementSubjectField.addKeyListener(keyAdapter);
        elementDeadlineField.addKeyListener(keyAdapter); 
        elementContentField.addKeyListener(keyAdapter);


        componentsContainer.add(elementSubjectLabel); componentsContainer.add(elementSubjectField);
        componentsContainer.add(elementDeadlineLabel); componentsContainer.add(elementDeadlineField);
        componentsContainer.add(elementFormatLabel); componentsContainer.add(elementFormatCombo);
        componentsContainer.add(elementContentWrapper);
        componentsContainer.add(removeButton);
        
        elementPanel.add(componentsContainer, Integer.valueOf(0));

        if (type == GroupType.NEW){
            JButton addButton = Main.createNewButtonBevel("+", new Font("Arial", Font.BOLD, 39), 
            new Insets(0, 0, 0, 0), new Color(0x3D405B), Color.white, "Add an element");
            addButton.setPreferredSize(new Dimension(60,45));
            addButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    for (SegmentGroup sg : rg.segmentGroups) {
                        if (sg.elementInputPanel == addButton.getParent().getParent().getParent()) {
                            for (ElementGroup eg : sg.elementGroups) {
                                eg.elementPanel.setVisible(false);
                            }
                            ElementGroup EG = ElementGroup.createNewElementGroup(sg.emojiCombo.getSelectedItem().toString(), sg.formatCombo.getSelectedIndex(), GroupType.NEW, rg);
                            sg.elementGroups.add(EG);
                            sg.elementInputPanel.add(EG.elementPanel);
                            sg.elementInputPanel.add(Box.createVerticalStrut(10));
                            for (ElementGroup eg : sg.elementGroups) {
                                if (eg.elementPanel == addButton.getParent().getParent()) {
                                    eg.subjectField.setEnabled(true); eg.contentField.setEnabled(true);
                                    eg.deadlineField.setEnabled(true); eg.formatCombo.setEnabled(true);
                                    eg.removeButton.setEnabled(true);
                                    eg.addButton.getParent().setVisible(false);
                                    eg.elementPanel.setLayer(eg.addButton.getParent(), 0);
                                    eg.elementPanel.setLayer(eg.subjectField.getParent(), 1);
                                    eg.subjectField.getParent().setVisible(true);
                                    break;
                                }
                            }
                            for (ElementGroup eg : sg.elementGroups) {
                                eg.elementPanel.setVisible(true);
                            }
            
                            SwingUtilities.invokeLater(new Runnable() {
                                @Override
                                public void run() {
                                    JScrollBar verticalScrollbar = ((JScrollPane) sg.elementInputPanel.getParent().getParent()).getVerticalScrollBar();
                                    verticalScrollbar.setValue(verticalScrollbar.getMaximum());
                                    ((JScrollPane) sg.elementInputPanel.getParent().getParent()).revalidate();
                                }
                            });
            
                            break;
                        }
                    }
                    Main.refresh(Main.reminderGroup);
                }
            });
            

            elementSubjectField.setEnabled(false); elementContentField.setEnabled(false);
            elementDeadlineField.setEnabled(false); elementFormatCombo.setEnabled(false);
            removeButton.setEnabled(false);

            JPanel addButtonContainer = new JPanel();
            addButtonContainer.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 13));
            addButtonContainer.setBounds(0, 0, width, height);
            addButtonContainer.setBackground(new Color(0xFAF9F0));
            addButtonContainer.add(addButton);
            addButtonContainer.setVisible(true);

            elementPanel.add(addButtonContainer, Integer.valueOf(1));

            return (new ElementGroup(elementSubjectLabel, elementSubjectField, elementContentLabel, elementContentField, elementDeadlineLabel, elementDeadlineField, elementFormatLabel, elementFormatCombo, datePicker, addButton, removeButton, elementPanel));
        } else {
            return (new ElementGroup(elementSubjectLabel, elementSubjectField, elementContentLabel, elementContentField, elementDeadlineLabel, elementDeadlineField, elementFormatLabel, elementFormatCombo, datePicker, null, removeButton, elementPanel));
        }
    }
}
