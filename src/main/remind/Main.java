package main.remind;

import java.awt.*;
import java.awt.datatransfer.*;
import java.awt.event.*;
import java.io.*;
import java.net.URLDecoder;
import java.text.*;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.*;

import javax.swing.*;
import javax.swing.border.*;

import com.raven.datechooser.SelectedDate;

public class Main extends JFrame implements ActionListener {

//#region[rgba(30, 40, 50, 0.15)] Initialization
    static JFrame frame;    
    private static String filePath;

    // Main Home Panel Components
    JPanel homePanel;
    JTextArea savedReminderArea, noteArea;
    JButton createButton, updateButton, collectionsButton;
    JLabel remindLabel;
    ImageIcon remindImage;

    // Create and Update Components
    static JButton homeButton, initiateButton, addButton, leftButton, rightButton, saveButton, collectionsButton2, backtoHomeButton, backButton;
    static JPanel mainPanel, subPanel1, inputPanel, containerPanel, subPanel2, resultPanel;
    static JLabel titleImage, guideImage, pageNumberLabel, pageInfoLabel;
    static JTextArea resultArea; 
    static JTextField remindersTitleField;
    static CardLayout cardLayout;
    static ImageIcon updateImage, createImage, collectionsImage;

    // Collection Components
    static JPanel collectionsPanel, navigationPanel, lowOpacityBackground, menuBarPanel, newPanel;
    static JScrollPane navigationPane;
    static ArrayList<StoredReminder> remindersCollection = new ArrayList<StoredReminder>();
    static StoredReminder selectedGroup;
    static StoredReminder selectedStoredReminder;
    static JButton homeButtonCollections, useButton, deleteButton, newButton;
    static JLabel collectionsLabel;

    // Storage
    static ReminderGroup reminderGroup;
    static Reminder REMINDER;
    StringBuilder text = new StringBuilder();
    static int currentPage = 1;

    // Other Variables
    public enum GroupType {NEW, SAVED}
    static String emojis[] = {"📖","📝","📍","🔔", "📚", "📔", "🔎", "📒", "📕", "📁", "📌", "⏳"};
    static ArrayList<ArrayList<String>> data;
    static ArrayList<String> currentlyUsing; 
    static String[][] months = {
        {"Jan","Jan."},{"Feb", "Feb."},{"Mar", "March"},{"Apr", "April"},
        {"May","May"},{"Jun", "June"},{"Jul", "July"},{"Aug", "August"},
        {"Sep","Sept."},{"Oct", "Oct."},{"Nov", "Nov."},{"Dec", "Dec."}
    };
    

    //#endregion

    Main(){
        //filePath = "src\\main\\resources\\data\\RemindersData.txt";
        filePath = getDatabaseFilePath();
        createDatabaseFile(filePath);
        this.setTitle("Reminders");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setBounds(100,30, 1250,780);
        this.setResizable(false);
        this.getContentPane().setBackground(new Color(0x3D405B));
        ImageIcon logo = new ImageIcon(Main.class.getResource("/images/RemindLogo.png"));
        this.setIconImage(logo.getImage());
        
        //#region[rgba(30, 87, 75, 0.15)] Home Panel
        homePanel = createNewPanel(1250, 780, new Color(0x101118), null);
        homePanel.setVisible(true);

        createButton = createNewButtonBevel("Create", new Font("Segoe UI", Font.BOLD, 20), 
        new Insets(3, 3, 3, 3), new Color(0xF6DEB6), new Color(0x101218), "Create a new Reminder");
        createButton.setBounds(870,370, 170,50);
        createButton.addActionListener(this);

        updateButton = createNewButtonBevel("Update", new Font("Segoe UI", Font.BOLD, 20), 
        new Insets(3, 3, 3, 3), new Color(0xF6DEB6), new Color(0x101218), "Update currently using Reminder");
        updateButton.setBounds(870,440, 170,50);
        updateButton.addActionListener(this);

        collectionsButton = createNewButtonBevel("Collections", new Font("Segoe UI", Font.BOLD, 20), 
        new Insets(3, 3, 3, 3), new Color(0xF6DEB6), new Color(0x101218), "Reminders Storage");
        collectionsButton.setBounds(870,510, 170,50);
        collectionsButton.addActionListener(this);
        
        data = readData();
        currentlyUsing = findCurrentlyUsing(data);

        savedReminderArea = createNewTextArea(new Font("Segoe UI Emoji", Font.PLAIN, 17), 
        new Insets(20, 20, 20, 20), new Color(0xF4F1DE));
        savedReminderArea.setText(decodeForTextArea(currentlyUsing));
        savedReminderArea.setEditable(false);

        JScrollPane savedReminderPane = ScrollPaneFactory.newScrollPane(savedReminderArea);
        savedReminderPane.setBounds(30, 30, 650, 650);
        savedReminderPane.setBorder(new EmptyBorder(0, 0, 0, 0));

        JButton copySavedReminderButton = createNewButton("Copy", new Font("Segoe UI", Font.BOLD, 13), 
        new Insets(3, 3, 3, 3), new Color(0x3D405B), Color.white, "Copy to clipboard");
        copySavedReminderButton.setBounds(30, 680, 80, 30);
        if (data.isEmpty()){copySavedReminderButton.setEnabled(false);}
        copySavedReminderButton.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e) {
                ((JButton) e.getSource()).setEnabled(true);
                Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                StringSelection selection = new StringSelection(decodeForTextArea(currentlyUsing));
                clipboard.setContents(selection, null);
                JOptionPane.showMessageDialog(null, "Text have been copied to clipboard.");
            }
        });
        remindImage = newScaledImage("Remind.png", 407, 179);
        remindLabel = new JLabel(remindImage);
        remindLabel.setBounds(750,120, 407,179);

        this.add(homePanel);
        homePanel.add(createButton); homePanel.add(updateButton); homePanel.add(collectionsButton); homePanel.add(savedReminderPane);
        homePanel.add(copySavedReminderButton); homePanel.add(remindLabel);
        
        //#endregion

        //#region[rgba(30, 87, 75, 0.15)] Edit Panel (Create and Update Panel)

        updateImage = newScaledImage("Update.png", 570, 93);
        createImage = newScaledImage("Create.png", 540, 93);

        mainPanel = createNewPanel(1250, 780, new Color(0x212231), null); 
        mainPanel.setVisible(false);

        this.add(mainPanel);

        subPanel1 = createNewPanel(1250, 780, new Color(0x212231), null);
        subPanel1.setVisible(true);
        subPanel1.requestFocusInWindow();
        subPanel1.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                int keyCode = e.getKeyCode();

                if (keyCode == KeyEvent.VK_LEFT) {
                    leftButton.doClick();
                } else if (keyCode == KeyEvent.VK_RIGHT) {
                    rightButton.doClick();
                }
            }
        });

        homeButton = createNewButton("Home", new Font("Segoe UI", Font.PLAIN, 15), 
        new Insets(3, 3, 2, 3), new Color(0x212231), Color.white, "Go to Home page");
        homeButton.setBorder(BorderFactory.createLineBorder(Color.WHITE, 1));
        homeButton.setBounds(10,10, 80,30);
        homeButton.addActionListener(this);
        
        titleImage = new JLabel();
        titleImage.setBounds(30,50,579,93);

        remindersTitleField = new JTextField("Reminders Title");
        remindersTitleField.setFont(new Font("Segoe UI", Font.PLAIN, 23));
        remindersTitleField.setBounds(30,160,580,40);
        remindersTitleField.setToolTipText("Reminders Title");
        remindersTitleField.setBackground(new Color(0x31324B)); remindersTitleField.setForeground(Color.white);
        remindersTitleField.setCaretColor(Color.white);
        Border combine = BorderFactory.createCompoundBorder(new RoundedLineBorder(Color.white, 2, 5), BorderFactory.createEmptyBorder(0, 10, 5, 4)); 
        Border combineRed = BorderFactory.createCompoundBorder(new RoundedLineBorder(new Color(0xE07A5F), 2, 5), BorderFactory.createEmptyBorder(0, 10, 5, 4));
        remindersTitleField.setBorder(combine);
        remindersTitleField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e){
                String text = remindersTitleField.getText();
                if (text.isEmpty() || text.contains("#")) {
                    remindersTitleField.setBorder(combineRed);
                } else {
                    remindersTitleField.setBorder(combine);
                }
            }
        });


        pageNumberLabel = new JLabel(currentPage + "");
        pageNumberLabel.setFont(new Font("Segoe UI", Font.PLAIN, 22));
        pageNumberLabel.setBounds(35,200,30,60);
        pageNumberLabel.setForeground(Color.white);

        pageInfoLabel = new JLabel("Page " + currentPage + " out of 1");
        pageInfoLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        pageInfoLabel.setBounds(60,655,150,60);
        pageInfoLabel.setForeground(Color.white);

        initiateButton = createNewButtonBevel("CREATE REMINDER", new Font("Segoe UI", Font.BOLD, 12), 
        new Insets(3, 3, 3, 3), new Color(0x3D405B), Color.white, "Compile to text");
        initiateButton.setBounds(380,670, 200,30);
        initiateButton.addActionListener(this);
        
        guideImage = new JLabel(newScaledImage("Guide.png", 545, 550));
        guideImage.setBounds(650,90, 545,550);
        
        inputPanel = createNewPanel(550, 460, new Color(0xF6DEB6), null);
        inputPanel.setBounds(60,210,520,460);

        containerPanel = new JPanel();
        cardLayout = new CardLayout();
        containerPanel.setLayout(cardLayout);
        containerPanel.setBounds(0,0,520,500);
        
        leftButton = createNewButton("<", new Font("Bahnschrift", Font.BOLD, 25), new Insets(3, 3, 3, 3), 
        new Color(0x3D405B), Color.white, "Back");
        leftButton.setBounds(30,390,30,50);
        leftButton.setEnabled(false);
        leftButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (currentPage > 1) {
                    cardLayout.previous(containerPanel);
                    Main.currentPage--;
                }
                refresh(reminderGroup);
            }
        });
        

        rightButton = createNewButton(">", new Font("Bahnschrift", Font.BOLD, 25), new Insets(3, 3, 3, 3), 
        new Color(0x3D405B), Color.white, "Next");
        rightButton.setBounds(580,390,30,50);
        rightButton.setEnabled(false);
        rightButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (currentPage < reminderGroup.segmentGroups.size()) {
                    cardLayout.next(containerPanel);
                    Main.currentPage++;
                } 
                refresh(reminderGroup);
            }
        });

        addButton = createNewButton("+", new Font("Bahnschrift", Font.BOLD, 28), new Insets(3, 3, 3, 3), 
        new Color(0xF6DEB6), new Color(0x181A25), "Add a segment");
        addButton.setBounds(580,230,30,40);
        addButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                inputPanel.setVisible(false);
                SegmentGroup sg = SegmentGroup.createNewSegmentGroup(reminderGroup);
                ElementGroup eg = ElementGroup.createNewElementGroup(sg.emojiCombo.getSelectedItem().toString(), sg.formatCombo.getSelectedIndex(), GroupType.NEW, reminderGroup);
                sg.elementGroups.add(eg); sg.elementInputPanel.add(eg.elementPanel); sg.elementInputPanel.add(Box.createVerticalStrut(10));
                reminderGroup.segmentGroups.add(sg); containerPanel.add(sg.segmentPanel, "segment" + reminderGroup.segmentGroups.size());
                
                cardLayout.show(containerPanel, "segment" + (reminderGroup.segmentGroups.indexOf(sg) + 1));
                leftButton.setEnabled(true); rightButton.setEnabled(true);
                inputPanel.setVisible(true);
                currentPage = reminderGroup.segmentGroups.size();
                pageNumberLabel.setText(currentPage + "");
                pageInfoLabel.setText("Page " + currentPage + " out of " + reminderGroup.segmentGroups.size());
                rightButton.setEnabled(false);
            }
        });

        inputPanel.add(containerPanel);

        subPanel1.add(homeButton); subPanel1.add(titleImage); 
        subPanel1.add(remindersTitleField); 
        subPanel1.add(initiateButton); subPanel1.add(inputPanel); subPanel1.add(guideImage);
        subPanel1.add(addButton); subPanel1.add(leftButton); subPanel1.add(rightButton); 
        subPanel1.add(pageNumberLabel); subPanel1.add(pageInfoLabel);

        mainPanel.add(subPanel1);
        
        //* ---------------------------- subPanel2  ---------------------------- 

        subPanel2 = createNewPanel(1250, 780, new Color(0x212231), null);

        JLabel picture = new JLabel(newScaledImage("Remind.png", 407,179));
        picture.setBounds(50,170, 407,179);

        backButton = createNewButtonBevel("Back", new Font("Segoe UI", Font.BOLD, 17), 
        new Insets(3, 3, 3, 3), new Color(0xF4F1DE), new Color(0x3D405B), "Return to edit page");
        backButton.setBounds(175, 410, 170, 40);
        backButton.addActionListener(this);

        backtoHomeButton = createNewButtonBevel("Home", new Font("Segoe UI", Font.BOLD, 17), 
        new Insets(3, 3, 3, 3), new Color(0xF4F1DE), new Color(0x3D405B), "Go to Home page");
        backtoHomeButton.setBounds(175, 470, 170, 40);
        backtoHomeButton.addActionListener(this);

        collectionsButton2 = createNewButtonBevel("Collections", new Font("Segoe UI", Font.BOLD, 17), 
        new Insets(3, 3, 3, 3), new Color(0xF4F1DE), new Color(0x3D405B), "Reminders Storage");
        collectionsButton2.setBounds(175, 530, 170, 40);
        collectionsButton2.addActionListener(this);

        resultPanel = new JPanel();
        resultPanel.setLayout(null);
        resultPanel.setBounds(510, 30, 650, 660);
        resultPanel.setBackground(new Color(0xF0EBD1));

        resultArea = createNewTextArea(new Font("Segoe UI Emoji", Font.PLAIN, 17), 
        new Insets(20, 20, 20, 20), new Color(0xF4F1DE));
        resultArea.setBounds(0, 0, 650, 620);
        resultArea.setEditable(false); 

        JScrollPane resultPane = ScrollPaneFactory.newScrollPane(resultArea);
        resultPane.setBounds(0, 0, 650, 620);
        resultPane.setBorder(new EmptyBorder(0, 0, 0, 0));

        JButton copyButton = createNewButton("Copy", new Font("Segoe UI", Font.BOLD, 13), 
        new Insets(3, 3, 3, 3), new Color(0x3D405B), Color.white, "Copy to clipboard");
        copyButton.setBounds(10, 625, 80, 30);
        copyButton.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e) {
                Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                StringSelection selection = new StringSelection(text.toString());
                clipboard.setContents(selection, null);
                JOptionPane.showMessageDialog(null, "Text have been copied to clipboard.");
            }
        });

        saveButton = createNewButton("Save", new Font("Segoe UI", Font.BOLD, 13), 
        new Insets(3, 3, 3, 3), new Color(0x3D405B), Color.white, "Save to Collections");
        saveButton.setBounds(100, 625, 80, 30);
        saveButton.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e) {
                if (initiateButton.getText().equals("UPDATE REMINDER")){
                    data.set(data.indexOf(currentlyUsing), reminderToDataList(REMINDER));
                    updateData(data);
                    JOptionPane.showMessageDialog(null, "Changes are saved.");
                } else {
                    writeData(reminderToDataList(REMINDER));
                }
                saveButton.setEnabled(false); backButton.setEnabled(false);
                copySavedReminderButton.setEnabled(true);
            }
        });

        resultPanel.add(resultPane); resultPanel.add(copyButton); resultPanel.add(saveButton);

        subPanel2.add(picture); subPanel2.add(backButton); subPanel2.add(backtoHomeButton); subPanel2.add(collectionsButton2); subPanel2.add(resultPanel); 
        subPanel2.setVisible(false);

        mainPanel.add(subPanel2);

        //#endregion

        //#region[rgba(30, 87, 75, 0.15)] Collections Panel
         
        collectionsPanel = createNewPanel(1250, 780, new Color(0x212231), new BorderLayout(10, 10));
        collectionsPanel.setBounds(0,0,1235,740);
        collectionsPanel.setVisible(false);
        this.add(collectionsPanel);

        menuBarPanel = createNewPanel(1250, 80, new Color(0x212231), new FlowLayout(FlowLayout.LEADING, 30, 10));
        menuBarPanel.setPreferredSize(new Dimension(1235, 100));
        menuBarPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        homeButtonCollections = createNewButton("Home", new Font("Segoe UI", Font.PLAIN, 15), 
        new Insets(3, 3, 2, 3), new Color(0x212231), Color.white, "Go to Home page");
        homeButtonCollections.setPreferredSize(new Dimension(70, 25));
        homeButtonCollections.setBorder(BorderFactory.createLineBorder(Color.WHITE, 1));
        homeButtonCollections.addActionListener(this);

        collectionsImage = newScaledImage("Collections.png", 300, 50);
        collectionsLabel = new JLabel();
        collectionsLabel.setIcon(collectionsImage);
        collectionsLabel.setPreferredSize(new Dimension(300, 50));

        navigationPanel = createNewPanel(1150, 780, new Color(0x212231), null);
        navigationPanel.setLayout(new FlowLayout(FlowLayout.LEADING, 15, 0));
        navigationPanel.setBorder(BorderFactory.createEmptyBorder(10, 30, 10, 30));

        navigationPane = ScrollPaneFactory.newScrollPane(navigationPanel);
        navigationPane.setBorder(new EmptyBorder(0, 0, 0, 0));
        navigationPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
        navigationPane.setFocusable(true);
        
        addKeyListeners(navigationPane);

        menuBarPanel.add(homeButtonCollections); menuBarPanel.add(collectionsLabel);
        
        collectionsPanel.add(navigationPane, BorderLayout.CENTER);
        collectionsPanel.add(menuBarPanel, BorderLayout.PAGE_START);
        
        JTextArea selectedArea = Main.createNewTextArea(new Font("Segoe UI Emoji", Font.PLAIN, 17), 
        new Insets(10, 0, 10, 20), new Color(0xF4F1DE));
        selectedArea.setEditable(false); selectedArea.setFocusable(false);
        JScrollPane selectedAreaPane = ScrollPaneFactory.newScrollPane(selectedArea);
        selectedAreaPane.setPreferredSize(new Dimension(430, 590));
        selectedAreaPane.setBorder(new EmptyBorder(0, 0, 0, 0));

        JLabel selectedAreaTitleLabel = new JLabel();
        selectedAreaTitleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        selectedAreaTitleLabel.setPreferredSize(new Dimension(430, 30));
        selectedAreaTitleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 3,0));
        selectedAreaTitleLabel.setForeground(new Color(0x181A25));

        JPanel selectedAreaContainerPanel = Main.createNewPanel(450, 550, new Color(0xF4F1DE), new FlowLayout(FlowLayout.LEADING, 0,10));
        selectedAreaContainerPanel.setBounds(350, 50, 450, 650);
        selectedAreaContainerPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 0));

        selectedAreaContainerPanel.add(selectedAreaTitleLabel); selectedAreaContainerPanel.add(selectedAreaPane);

        selectedGroup = new StoredReminder(selectedArea, selectedAreaPane, selectedAreaTitleLabel, selectedAreaContainerPanel, null, null);

        lowOpacityBackground = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setComposite(AlphaComposite.SrcOver.derive(0.8f)); // Set opacity level (0.0 - 1.0)
                g2d.setColor(getBackground());
                g2d.fillRect(0, 0, getWidth(), getHeight());
                g2d.dispose();
            }
        };
        lowOpacityBackground.setSize(1250, 720);
        lowOpacityBackground.setLayout(null);
        lowOpacityBackground.setBackground(Color.black);
        lowOpacityBackground.setVisible(false);
        lowOpacityBackground.add(selectedGroup.containerPanel);
        lowOpacityBackground.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                JComponent view = (JComponent) Main.navigationPane.getViewport().getView();
                view.setEnabled(true); Main.navigationPane.setEnabled(true);
                refreshCollections(remindersCollection);
                lowOpacityBackground.setVisible(false); 
                selectedGroup.containerPanel.setBackground(new Color(0xF5F2E0));
                selectedGroup.textArea.setBackground(new Color(0xF5F2E0));
                useButton.setEnabled(true); deleteButton.setEnabled(true);
                selectedStoredReminder.containerPanel.setOpaque(true); selectedStoredReminder.textArea.setVisible(true); 
                selectedStoredReminder.titleLabel.setVisible(true); selectedStoredReminder.textAreaPane.setVisible(true);
                navigationPanel.requestFocus();
            }
        });

        useButton = createNewButton("Use", new Font("Segoe UI", Font.PLAIN, 20), 
        new Insets(3, 3, 2, 3), new Color(0x2C4E40), Color.white, "Use reminder as daily reminders (This reminder will be displayed on the Home page)");
        useButton.setBounds(800, 50, 100, 40);
        useButton.setBorder(BorderFactory.createLineBorder(new Color(0xF4F1DE), 1));
        useButton.addActionListener(this);

        deleteButton = createNewButton("Delete", new Font("Segoe UI", Font.PLAIN, 20), 
        new Insets(3, 3, 2, 3), new Color(0x6C150F), Color.white, null);
        deleteButton.setBounds(800, 100, 100, 40);
        deleteButton.setBorder(BorderFactory.createLineBorder(new Color(0xF4F1DE), 1));
        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int x = JOptionPane.showConfirmDialog(null, "Are you sure you want to delete \"" + selectedStoredReminder.titleLabel.getText() +  "\"?", "Reminders", JOptionPane.YES_NO_OPTION);
                if (x == 0){
                    navigationPanel.remove(selectedStoredReminder.containerPanel);
                    int indexToRemove = remindersCollection.indexOf(selectedStoredReminder);
                    remindersCollection.remove(selectedStoredReminder);
                    data.remove(indexToRemove);
                    updateData(data);
                    JComponent view = (JComponent) Main.navigationPane.getViewport().getView();
                    view.setEnabled(true); Main.navigationPane.setEnabled(true);
                    lowOpacityBackground.setVisible(false);
                    refreshCollections(remindersCollection);
                    navigationPanel.requestFocus();
                    
                }
            }
        });

        JButton copyButtonCollections = createNewButton("Copy", new Font("Segoe UI", Font.PLAIN, 20), 
        new Insets(3, 3, 2, 3), new Color(0x19304D), Color.white, null);
        copyButtonCollections.setBounds(800, 150, 100, 40);
        copyButtonCollections.setBorder(BorderFactory.createLineBorder(new Color(0xF4F1DE), 1));
        copyButtonCollections.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                StringSelection selection = new StringSelection(selectedGroup.textArea.getText());
                clipboard.setContents(selection, null);
                JOptionPane.showMessageDialog(null, "Text have been copied to clipboard.");
            }
        });

        lowOpacityBackground.add(selectedAreaContainerPanel); lowOpacityBackground.add(useButton); 
        lowOpacityBackground.add(deleteButton); lowOpacityBackground.add(copyButtonCollections);
        this.add(lowOpacityBackground);

        MouseAdapter newAdapter = new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                newPanel.setBackground(new Color(0x212231));
                newButton.setBackground(new Color(0xF4F1DE)); newButton.setForeground(new Color(0x212231));
                
                resetCollections();
                initializeCreate();

                collectionsPanel.setVisible(false);
                mainPanel.setVisible(true);

                subPanel1.requestFocus();
            }
            @Override
            public void mouseEntered(MouseEvent e){
                newPanel.setBackground(new Color(0xF4F1DE));
                newButton.setBackground(new Color(0x212231)); newButton.setForeground(new Color(0xF4F1DE));
                
            }

            public void mouseExited(MouseEvent e){
                newPanel.setBackground(new Color(0x212231));
                newButton.setBackground(new Color(0xF4F1DE)); newButton.setForeground(new Color(0x212231));
            }
        };

        newButton = Main.createNewButton("+", new Font("Arial", Font.BOLD, 39), 
        new Insets(0, 0, 0, 0), new Color(0xF4F1DE), new Color(0x212231), "Create a new reminder");
        newButton.setBounds(140, 240, 60, 60);
        newButton.addMouseListener(newAdapter);

        newPanel = Main.createNewPanel(335, 545, new Color(0x212231), null);
        newPanel.setPreferredSize(new Dimension(340, 550));
        newPanel.setBorder(BorderFactory.createLineBorder(new Color(0xF4F1DE), 4));
        newPanel.addMouseListener(newAdapter);
        newPanel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        newPanel.add(newButton);


        



        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                initializeCreate();
                mainPanel.setVisible(true);
            }
        });
        
        //#endregion
        this.setLayout(null);
        this.setVisible(true);

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                mainPanel.setVisible(false);
                resetCreate();
            }
        });
        
        
    }
    
    //#region[rgba(30, 40, 75, 0.15)] actionPerformed()
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == createButton){
            initializeCreate();
            homePanel.setVisible(false);
            mainPanel.setVisible(true);

            subPanel1.requestFocus();
        } else if (e.getSource() == updateButton){
            if (decodeAsObjects(currentlyUsing) == null){
                JOptionPane.showMessageDialog(null, "There is no saved reminders to update\nPlease make one first");
            } else {
                initializeUpdate();
                homePanel.setVisible(false);
                mainPanel.setVisible(true);

                subPanel1.requestFocus();
            }
        } else if (e.getSource() == homeButton) {
            int a = JOptionPane.showConfirmDialog(null, "Progress will be discarded. Are you sure to proceed?", "Reminders", JOptionPane.YES_NO_OPTION);
            if (a == 0){
                mainPanel.setVisible(false);
                this.setComponentZOrder(homePanel, 0);
                homePanel.repaint();
                homePanel.setVisible(true);
                resetCreate();
            }
        } else if(e.getSource() == initiateButton){
            reminderGroup = new ReminderGroup(Main.remindersTitleField, reminderGroup.segmentGroups);
            boolean checker = textFieldsChecker(reminderGroup);
            if (checker == false){
                JOptionPane.showMessageDialog(null, "Please fill-up all of the text fields. \nAlso keep in mind that (#, ~, _) characters are not allowed");
            } else {
                REMINDER = objectConverter(reminderGroup);
                text = objectToText(REMINDER);
                resultArea.setText(text.toString());
                subPanel1.setVisible(false);
                subPanel2.setVisible(true);
                saveButton.setEnabled(true); backButton.setEnabled(true); 
                if (initiateButton.getText().equals("CREATE REMINDER")){
                    saveButton.setToolTipText("Save to Collections");
                } else {
                    saveButton.setToolTipText("Save changes");
                }
            }
        } else if(e.getSource() == backButton){
            subPanel2.setVisible(false);
            subPanel1.setVisible(true);
        } else if(e.getSource() == backtoHomeButton){
            boolean checker = checkIfSaved(saveButton);
            if (checker){
                mainPanel.setVisible(false);
                homePanel.setVisible(true);
                resetCreate(); 

                data = readData();
                currentlyUsing = findCurrentlyUsing(data);
                if (currentlyUsing == null){
                    currentlyUsing = data.get(data.size() - 1);
                }
                savedReminderArea.setText(decodeForTextArea(currentlyUsing));
            }
        } else if (e.getSource() == collectionsButton2) {
            boolean checker = checkIfSaved(saveButton);
            if (checker){
                data = readData();
                currentlyUsing = findCurrentlyUsing(data);
                if (currentlyUsing == null){
                    currentlyUsing = data.get(data.size() - 1);
                }

                initializeCollections();
    
                homePanel.setVisible(false);
                mainPanel.setVisible(false);
                collectionsPanel.setVisible(true);

                resetCreate();

                navigationPanel.requestFocus();
            }
        } else if (e.getSource() == collectionsButton){
            initializeCollections();

            homePanel.setVisible(false);
            mainPanel.setVisible(false);
            collectionsPanel.setVisible(true);

            navigationPanel.requestFocus();
            
        } else if(e.getSource() == homeButtonCollections || e.getSource() == useButton) { 
            if (e.getSource() == useButton){
                currentlyUsing = data.get(remindersCollection.indexOf(selectedStoredReminder));
                updateCurrentlyUsingData(data);
                refreshCollections(remindersCollection);
                
                selectedGroup.containerPanel.setBackground(new Color(0xF2E2BB));
                selectedGroup.textArea.setBackground(new Color(0xF2E2BB));
                useButton.setEnabled(false);
                JComponent view = (JComponent) Main.navigationPane.getViewport().getView();
                view.setEnabled(true); Main.navigationPane.setEnabled(true);
                lowOpacityBackground.setVisible(false); 
                selectedGroup.containerPanel.setBackground(new Color(0xF5F2E0));
                selectedGroup.textArea.setBackground(new Color(0xF5F2E0));
                useButton.setEnabled(true); deleteButton.setEnabled(true);
                selectedStoredReminder.containerPanel.setOpaque(true); selectedStoredReminder.textArea.setVisible(true); 
                selectedStoredReminder.titleLabel.setVisible(true); selectedStoredReminder.textAreaPane.setVisible(true);
            }
            resetCollections();

            data = readData();
            currentlyUsing = findCurrentlyUsing(data);
            savedReminderArea.setText(decodeForTextArea(currentlyUsing));

            
            collectionsPanel.setVisible(false);
            homePanel.setVisible(true);
        }
    }
    //#endregion

    //#region[rgba(30, 10, 75, 0.15)] Class Methods
    public void initializeCreate(){
        titleImage.setIcon(createImage);
        titleImage.setBounds(30,50,540,93);
        initiateButton.setText("CREATE REMINDER");

        reminderGroup = new ReminderGroup(remindersTitleField, new ArrayList<SegmentGroup>());
        SegmentGroup sg = SegmentGroup.createNewSegmentGroup(reminderGroup);
        ElementGroup eg = ElementGroup.createNewElementGroup(sg.emojiCombo.getSelectedItem().toString(), sg.formatCombo.getSelectedIndex(), GroupType.NEW, reminderGroup);
        sg.elementGroups.add(eg); sg.elementInputPanel.add(eg.elementPanel); sg.elementInputPanel.add(Box.createVerticalStrut(10));
        reminderGroup.segmentGroups.add(sg); containerPanel.add(sg.segmentPanel, "segment" + reminderGroup.segmentGroups.size());
    }

    public void initializeUpdate(){
        titleImage.setIcon(updateImage);
        titleImage.setBounds(30,50,570,93);
        initiateButton.setText("UPDATE REMINDER");

        REMINDER = decodeAsObjects(currentlyUsing);
        reminderGroup = new ReminderGroup(remindersTitleField, new ArrayList<SegmentGroup>());
        reminderGroup.titleField.setText(REMINDER.title);

        for (Segment seg: REMINDER.segments){
            SegmentGroup sg = SegmentGroup.createNewSegmentGroup(reminderGroup);
            sg.titleField.setText(seg.title); sg.formatCombo.setSelectedIndex(seg.format); sg.emojiCombo.setSelectedIndex(seg.emoji); 
            for (Element elem: seg.elements){
                ElementGroup eg = ElementGroup.createNewElementGroup(emojis[seg.emoji], elem.format, GroupType.SAVED, reminderGroup);
                eg.subjectLabel.setText(emojis[seg.emoji] + " Subject: ");
                int[] intDate = null; String deadline;
                eg.formatCombo.setSelectedIndex(elem.format);
                switch (elem.format) {
                    case 0: deadline = formatDate(elem.deadline); eg.subjectField.setText(elem.subject); eg.contentField.setText(elem.content); eg.deadlineField.setText(deadline);  
                    intDate = convertToIntArray(deadline.split("-")); eg.dateChooser.setSelectedDate(new SelectedDate(intDate[1], intDate[0], intDate[2])); break;
                    case 1: eg.subjectField.setText(elem.subject); eg.contentField.setText(elem.content); break;
                    case 2: deadline = formatDate(elem.deadline); eg.contentField.setText(elem.content); eg.deadlineField.setText(deadline);
                    intDate = convertToIntArray(deadline.split("-")); eg.dateChooser.setSelectedDate(new SelectedDate(intDate[1], intDate[0], intDate[2])); break;
                    case 3: eg.contentField.setText(elem.content); break;
                }
                sg.elementGroups.add(eg); sg.elementInputPanel.add(eg.elementPanel); sg.elementInputPanel.add(Box.createVerticalStrut(10));
            }
            ElementGroup egNew = ElementGroup.createNewElementGroup(emojis[seg.emoji], seg.format, GroupType.NEW, reminderGroup);
            sg.elementGroups.add(egNew); sg.elementInputPanel.add(egNew.elementPanel); sg.elementInputPanel.add(Box.createVerticalStrut(10));
            reminderGroup.segmentGroups.add(sg); containerPanel.add(sg.segmentPanel, "segment" + reminderGroup.segmentGroups.size());
        }
        Main.refresh(Main.reminderGroup);
    }

    public void initializeCollections(){
        if (data.isEmpty()){
            navigationPanel.add(newPanel);
        } else {
            for (ArrayList<String> dl: data){
                remindersCollection.add(StoredReminder.createStoredReminder(dl));
            }
            
            for (StoredReminder sr: remindersCollection){
                navigationPanel.add(sr.containerPanel);
            }

            navigationPanel.add(newPanel);
            refreshCollections(remindersCollection);
        }
    }

    public boolean checkIfSaved(JButton saveBtn){
        boolean mainChecker = false;
        boolean saved = false;
        if (!(saveBtn.isEnabled())){
            saved = true;
        }
        if (saved){
            mainChecker = true;
        } else {
            int answer = 0;
            if (initiateButton.getText().equals("UPDATE REMINDER")){
                answer = JOptionPane.showConfirmDialog(null, "Changes will not be saved, are you sure to proceed?", "Reminders", JOptionPane.YES_NO_OPTION);
            } else if (initiateButton.getText().equals("CREATE REMINDER")){
                answer = JOptionPane.showConfirmDialog(null, "Created reminder will not be saved to Collections \nand will be discarded, are you sure to proceed?", "Reminders", JOptionPane.YES_NO_OPTION);
            }
            if (answer == 0){
                mainChecker = true;
            } else {
                mainChecker = false;
            }
        }
        return mainChecker;
    }

    public static ImageIcon newScaledImage(String imageName, int width, int height){
        ImageIcon image = new ImageIcon(Main.class.getResource("/images/" + imageName));
        ImageIcon scaledImage = new ImageIcon(image.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH));
        return scaledImage;
    }

    public static String getDatabaseFilePath() {
        String jarFilePath = Main.class.getProtectionDomain().getCodeSource().getLocation().getPath();
        String decodedJarFilePath = "null";
        try {
            decodedJarFilePath = URLDecoder.decode(jarFilePath, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            JOptionPane.showMessageDialog(null, "Error: " + e.getMessage());
        }
        String parentDirectory = new File(decodedJarFilePath).getParent();
        String databaseFilePath = parentDirectory + File.separator + "RemindersData.txt";
        return databaseFilePath;
    }

    public static void createDatabaseFile(String filePath) {
        File databaseFile = new File(filePath);

        if (databaseFile.exists()) {
            //JOptionPane.showMessageDialog(null, "Using existing database file: " + filePath);
        } else {
            try {
                if (databaseFile.createNewFile()) {
                    //JOptionPane.showMessageDialog(null, "New database file created: " + filePath);
                } else {
                    JOptionPane.showMessageDialog(null, "Failed to create the database file.");
                }
            } catch (IOException e) {
                JOptionPane.showMessageDialog(null, "Error creating the database file: " + e.getMessage());
            }
        }
    }

    public ArrayList<ArrayList<String>> readData() {
        ArrayList<ArrayList<String>> data = new ArrayList<>();
        ArrayList<String> compiler = new ArrayList<>();

        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                if (line.equals("-----------------------")) {
                    data.add(compiler);
                    compiler = new ArrayList<>();
                    continue;
                } else {
                    compiler.add(line);
                }
            }
            bufferedReader.close();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error reading the database file: " + e);
        }

        return data;
    }

    public void writeData(ArrayList<String> dataList) {
        if (filePath != null) {
            try {
                FileWriter fileWriter = new FileWriter(filePath, true);
                BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);

                for (String d : dataList) {
                    bufferedWriter.write(d + "\n");
                }
                bufferedWriter.write("-----------------------\n");
                bufferedWriter.close();
                fileWriter.close();
                JOptionPane.showMessageDialog(null, "Reminder is saved.");
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, "Exception: " + e);
            }
        } else {
            JOptionPane.showMessageDialog(null, "Please create the database file first.");
        }
    }

    public static void updateData(ArrayList<ArrayList<String>> data) {
        if (filePath != null) {
            try {
                FileWriter fileWriter = new FileWriter(filePath);
                BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);

                for (ArrayList<String> dataList : data) {
                    for (String dl : dataList) {
                        bufferedWriter.write(dl + "\n");
                    }
                    bufferedWriter.write("-----------------------\n");
                }

                bufferedWriter.close();
                fileWriter.close();

            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, "Exception: " + e);
            }
        } else {
            JOptionPane.showMessageDialog(null, "Please create the database file first.");
        }
    }

    public void resetCollections(){
        remindersCollection.clear();
        navigationPanel.removeAll();
    }
    public void resetCreate(){
        remindersTitleField.setText("Reminders Title");
        currentPage = 1;
        pageNumberLabel.setText(currentPage + "");
        pageInfoLabel.setText("Page " + currentPage + " out of 1");
        for (SegmentGroup sg: reminderGroup.segmentGroups){
            containerPanel.remove(sg.segmentPanel);
        }
        reminderGroup = new ReminderGroup(remindersTitleField, new ArrayList<SegmentGroup>());
        REMINDER = null;

        text = new StringBuilder();

        subPanel2.setVisible(false);
        subPanel1.setVisible(true);
    }

    public static void refresh(ReminderGroup rg){
        for (SegmentGroup sg: rg.segmentGroups){
            int finalIndex = sg.elementGroups.size() - 1;
            for (ElementGroup eg: sg.elementGroups){
                if (sg.elementGroups.get(finalIndex) == eg){
                    break;
                } else {
                    int format = eg.formatCombo.getSelectedIndex();
                    switch (format) {
                        case 0: 
                            eg.subjectField.setBackground(Color.white);
                            eg.subjectField.setEnabled(true);
                            eg.deadlineField.setBackground(Color.white);
                            eg.deadlineField.setEnabled(true);
                            break;
                        case 1:
                            eg.subjectField.setBackground(Color.white);
                            eg.subjectField.setEnabled(true);
                            eg.deadlineField.setBackground(new Color(0xDADAD2));
                            eg.deadlineField.setEnabled(false);
                            break;
                        case 2:
                            eg.subjectField.setBackground(new Color(0xDADAD2));
                            eg.subjectField.setEnabled(false);
                            eg.subjectField.setBorder(ElementGroup.defaultBorder);
                            eg.deadlineField.setBackground(Color.white);
                            eg.deadlineField.setEnabled(true);
                            break;
                        case 3:
                            eg.subjectField.setBackground(new Color(0xDADAD2));
                            eg.subjectField.setEnabled(false);
                            eg.subjectField.setBorder(ElementGroup.defaultBorder);
                            eg.deadlineField.setBackground(new Color(0xDADAD2));
                            eg.deadlineField.setEnabled(false);
                            break;
                    }
                    eg.subjectLabel.setText(sg.emojiCombo.getSelectedItem().toString() + " Subject: ");
                }
            }
        }

        if (currentPage == 1) {
            leftButton.setEnabled(false);
            rightButton.setEnabled(true);
        } else if (currentPage == rg.segmentGroups.size()) {
            rightButton.setEnabled(false);
            leftButton.setEnabled(true);
        }

        if (currentPage > 1 && currentPage < rg.segmentGroups.size()){
            leftButton.setEnabled(true);
            rightButton.setEnabled(true);
        }
        if (rg.segmentGroups.size() == 1){
            leftButton.setEnabled(false); rightButton.setEnabled(false);
        }
        pageNumberLabel.setText(currentPage + "");
        pageInfoLabel.setText("Page " + currentPage + " out of " + rg.segmentGroups.size());

        
        Main.inputPanel.setVisible(false);
        Main.inputPanel.setVisible(true);
    }

    public static boolean textFieldsChecker(ReminderGroup reminderGroup){
        boolean checker = true;
        for (SegmentGroup sg: reminderGroup.segmentGroups){
            if ((sg.titleField.getText().isEmpty() || sg.titleField.getText().contains("_")) || 
            (reminderGroup.titleField.getText().isEmpty() || reminderGroup.titleField.getText().contains("#"))){
                checker = false;
                break;
            }
            int counter = 0;
            for (ElementGroup eg: sg.elementGroups){
                counter++;
                if (counter == sg.elementGroups.size()){
                    break;
                } else {
                    String subject = eg.subjectField.getText(); String content = eg.contentField.getText(); String deadline = eg.deadlineField.getText();
                    switch (eg.formatCombo.getSelectedIndex()){
                        case 0: 
                            if ((subject.isEmpty() || content.isEmpty() || deadline.isEmpty()) || 
                            (subject.contains("_") || content.contains("_") || deadline.contains("_")) ||
                            (subject.contains("~") || content.contains("~") || deadline.contains("~"))){
                                checker = false;
                            } break;
                        case 1: 
                            if (subject.isEmpty() || content.isEmpty() || 
                            (subject.contains("_") || content.contains("_")) ||
                            (subject.contains("~") || content.contains("~"))){
                                checker = false;
                            } break;
                        case 2: 
                            if (content.isEmpty() || deadline.isEmpty() || 
                            (content.contains("_") || deadline.contains("_")) ||
                            (content.contains("~") || deadline.contains("~"))){
                                checker = false;
                            } break;
                        case 3: 
                            if (content.isEmpty() || content.contains("_") || content.contains("~")){
                                checker = false;
                            } break;
                    }
                }
            }
        }
        return checker;
    }

    public static Reminder objectConverter(ReminderGroup reminderGroup){
        ArrayList<Segment> Segments = new ArrayList<Segment>(); 
        for (SegmentGroup sg: reminderGroup.segmentGroups){
            if (sg.elementGroups.isEmpty()){
                Segment S = new Segment(sg.titleField.getText(),sg.formatCombo.getSelectedIndex(), sg.emojiCombo.getSelectedIndex(), null);
                Segments.add(S);
            } else {
                ArrayList<Element> Elements = new ArrayList<Element>();
                for (ElementGroup eg: sg.elementGroups){
                    Element E;
                    SelectedDate sd; String deadline;
                    switch (eg.formatCombo.getSelectedIndex()){
                        case 0: sd = eg.dateChooser.getSelectedDate(); deadline = sd.getMonth() + "-" + sd.getDay() + "-" + sd.getYear();
                        E = new Element(eg.subjectField.getText(), eg.contentField.getText(), parseDate(deadline), eg.formatCombo.getSelectedIndex()); break;
                        case 1:
                        E = new Element(eg.subjectField.getText(), eg.contentField.getText(), null, eg.formatCombo.getSelectedIndex()); break;
                        case 2: sd = eg.dateChooser.getSelectedDate(); deadline = sd.getMonth() + "-" + sd.getDay() + "-" + sd.getYear();
                        E = new Element("", eg.contentField.getText(), parseDate(deadline), eg.formatCombo.getSelectedIndex()); break;
                        case 3: E = new Element("", eg.contentField.getText(), null, eg.formatCombo.getSelectedIndex()); break;
                        default: E = new Element("", eg.contentField.getText(), null, eg.formatCombo.getSelectedIndex()); 
                    }
                    Elements.add(E);
                }
                Elements.remove(Elements.size() - 1);
                Elements = sortElementsByDate(Elements);

                Segment S = new Segment(sg.titleField.getText(),sg.formatCombo.getSelectedIndex(), sg.emojiCombo.getSelectedIndex(), Elements);
                Segments.add(S);
            }
        }
        Reminder reminder = new Reminder(reminderGroup.titleField.getText(), Segments, generateCurrentTime());
        return reminder;
    }
    
    public static StringBuilder objectToText(Reminder reminder){
        StringBuilder str = new StringBuilder();
        String updatedOn = "";
        if (initiateButton.getText().equals("UPDATE REMINDER")){
            updatedOn = "(Last updated on: ⏰" + reminder.dateMade + ")";
        } else {
            updatedOn = "(Created on: ⏰" + reminder.dateMade + ")";
        }
        str.append(updatedOn + "\n\n");
        str.append(reminder.title + ": \n");
        for (Segment i: reminder.segments){
            String emoji = emojis[i.emoji];
            str.append("\n-- " + emoji + " " + i.title + " --\n");
            if (i.elements == null || i.elements.isEmpty()){
                str.append("(none)\n");
            } else {
                for (Element j: i.elements){
                    String deadline;
                    switch (j.format){
                        case 0: deadline = dateFormater(formatDate(j.deadline)); str.append(emoji + " " + j.subject + " - " + j.content + ", ⏰" + deadline + "\n"); break;
                        case 1: str.append(emoji + " " + j.subject + " - " + j.content + "\n"); break;
                        case 2: deadline = dateFormater(formatDate(j.deadline)); str.append(emoji + " " + j.content + ", ⏰" + deadline + "\n"); break;
                        case 3: str.append(emoji + " " + j.content + "\n"); break;
                        default: str.append(emoji + " " + j.content + "\n"); 
                    }
                }
            }
        }
        return str;
    }

    

    public ArrayList<String> reminderToDataList(Reminder reminder){
        ArrayList<String> list = new ArrayList<String>();
        if (initiateButton.getText().equals("UPDATE REMINDER")){
            list.add(reminder.dateMade + "#" + reminder.title + "#" + "updated#using");
        } else {
            if (data.isEmpty()){
                list.add(reminder.dateMade + "#" + reminder.title + "#" + "created#using");
            } else {
                list.add(reminder.dateMade + "#" + reminder.title + "#" + "created#notusing");
            }
        }
        
        StringBuilder compiler = new StringBuilder();
        for (Segment i: reminder.segments){
            compiler.append((i.title + "_" + i.format + "_" + i.emoji + "_"));
            if (i.elements == null || i.elements.isEmpty()){
                compiler.append("(none)");
            } else {
                for (Element j: i.elements){
                    String deadline;
                    switch (j.format){
                        case 0: deadline = formatDate(j.deadline); compiler.append((j.subject + "~" + j.content + "~" + deadline + "~" + j.format + "~~")); break;
                        case 1: compiler.append((j.subject + "~" + j.content + "~" + "null" + "~" + j.format + "~~")); break;
                        case 2: deadline = formatDate(j.deadline); compiler.append(("null" + "~" + j.content + "~" + deadline + "~" + j.format + "~~")); break;
                        case 3: compiler.append("null" + "~" + j.content + "~" + "null" + "~" + j.format + "~~"); break;
                        default: compiler.append("null" + "~" + j.content + "~" + "null" + "~" + j.format + "~~"); 
                    }
                }
            }
            
            list.add(compiler.toString());
            compiler = new StringBuilder();
        }
        return (list);
    }


    public static String decodeForTextArea(ArrayList<String> dataList){
        StringBuilder str = new StringBuilder();
        if (dataList.isEmpty()){
            str.append("\nDatabase is empty. \nCurrently using Reminders will show here");
        } else {
            String[] a = dataList.get(0).split("#");
            String dateMade = a[0]; String reminderTitle = a[1]; String state = a[2];
            if (state.equals("updated")){
                str.append("(Last updated on: ⏰" + dateMade + ")\n\n");
            } else {
                str.append("(Created on: ⏰" + dateMade + ")\n\n");
            }
            str.append(reminderTitle + ": \n");
            for (String d: dataList){
                if (dataList.indexOf(d) == 0){
                    continue;
                } else {
                    String[] x = d.split("_");
                    String segTitle = x[0]; String segFormat = x[1]; String segEmoji = emojis[Integer.parseInt(x[2])];
                    str.append("\n-- " + segEmoji + " " + segTitle + " --\n");
                    if (x[3].equals("(none)")){
                        str.append("(none)\n");
                    } else {
                        ArrayList<String> segElems = new ArrayList<String>(Arrays.asList(x[3].split("~~")));
                        for (String j: segElems){
                            String[] y = j.split("~");
                            String subj = y[0], cont = y[1], dl = y[2], fm = y[3];
                            switch (fm){
                                case "0": dl = dateFormater(dl); str.append(segEmoji + " " + subj + " - " + cont + ", ⏰" + dl + "\n"); break;
                                case "1": str.append(segEmoji + " " + subj + " - " + cont + "\n"); break;
                                case "2": dl = dateFormater(dl); str.append(segEmoji + " " + cont + ", ⏰" + dl + "\n"); break;
                                case "3": str.append(segEmoji + " " + cont + "\n"); break;
                                default: JOptionPane.showMessageDialog(null, "File Reading Error.");
                            } 
                        }
                    }
                }
            }
        }
        return str.toString();
    }

    public static Reminder decodeAsObjects(ArrayList<String> data){
        Reminder reminder = null;
        if (data.isEmpty()){
            return null;
        } else {
            String[] a = data.get(0).split("#");
            String dateMade = a[0]; String reminderTitle = a[1];
            ArrayList<Segment> segments = new ArrayList<Segment>();
            for (String d: data){
                if (data.indexOf(d) == 0){
                    continue;
                } else {
                    String[] x = d.split("_");
                    String segTitle = x[0]; int segFormat = Integer.parseInt(x[1]); 
                    int segEmoji = Integer.parseInt(x[2]);
                    Segment S;
                    if (x[3].equals("(none)")){
                        S = new Segment(segTitle,segFormat,segEmoji,new ArrayList<Element>());
                    } else {
                        ArrayList<String> segElems = new ArrayList<String>(Arrays.asList(x[3].split("~~")));
                        ArrayList<Element> elements = new ArrayList<Element>();
                        for (String j: segElems){
                            String[] y = j.split("~");
                            String subj = y[0], cont = y[1], dl = y[2];
                            int fm = Integer.parseInt(y[3]); 
                            Element E = null;
                            switch (fm){
                                case 0: E = new Element(subj, cont, parseDate(dl), fm); break;
                                case 1: E = new Element(subj, cont, null, fm); break;
                                case 2: E = new Element(null, cont, parseDate(dl), fm); break;  
                                case 3: E = new Element(null, cont, null, fm); break;
                                default: JOptionPane.showMessageDialog(null, "File Reading Error.");
                            }
                            elements.add(E);
                        }
                        S = new Segment(segTitle,segFormat,segEmoji,elements);
                    }
                    segments.add(S);
                }
            }
            reminder = new Reminder(reminderTitle, segments, dateMade);
        }
        return reminder;    
    }

    public static JPanel createNewPanel(int width, int height, Color background, LayoutManager layout){
        JPanel panel = new JPanel();
        panel.setSize(width,height);
        panel.setBackground(background);
        panel.setLayout(layout);
        return panel;
    };

    public static JButton createNewButton(String text, Font font, Insets margin, Color background, Color foreground, String tooltip){
        JButton button = new JButton(text);
        button.setFont(font);
        button.setMargin(margin);
        button.setBorder(new EmptyBorder(0, 0, 0, 0));
        button.setBackground(background);
        button.setForeground(foreground);
        button.setToolTipText(tooltip);
        button.setFocusable(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (button.isEnabled()){
                    button.setBackground(button.getBackground().brighter());
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(background);
            }
        });
        return button;
    }

    public static JButton createNewButtonBevel(String text, Font font, Insets margin, Color background, Color foreground, String tooltip){
        JButton button = createNewButton(text, font, margin, background, foreground, tooltip);
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createRaisedBevelBorder(),
                BorderFactory.createEmptyBorder(8, 16, 8, 16)
        ));
        return button;
    }

    public static String generateCurrentTime(){
        java.util.Date timeFull = new java.util.Date();
        String[] timeString = timeFull.toString().split("\\s+");
        for (String[] m: months){
            if (m[0].equals(timeString[1])){
                timeString[1] = m[1];
                break;
            }
        }
        String time = timeString[3].substring(0, 5);
        String time12Hour = ""; 
        try {
            DateFormat inputFormat = new SimpleDateFormat("HH:mm");
            DateFormat outputFormat = new SimpleDateFormat("hh:mm a");
            Date date = inputFormat.parse(time);
            String t = outputFormat.format(date);
            String[] a = t.split(" ");
            a[1] = a[1].toLowerCase();
            time12Hour = String.join("", a);
            if (time12Hour.startsWith("0")){
                time12Hour = time12Hour.substring(1,time12Hour.length());
            }
        } catch (Exception er) {
            JOptionPane.showMessageDialog(null, "Error: " + er);
        }
        String result = timeString[1] + " " + timeString[2] + " (" + timeString[0] + ") " + time12Hour;
        return result;
    }

    public static JTextArea createNewTextArea(Font font, Insets margin, Color color){
        JTextArea textArea = new JTextArea(){
            @Override
            protected boolean processKeyBinding(KeyStroke ks, KeyEvent e, int condition, boolean pressed) {
                if (e.getKeyCode() == KeyEvent.VK_LEFT || e.getKeyCode() == KeyEvent.VK_RIGHT ||
                        e.getKeyCode() == KeyEvent.VK_UP || e.getKeyCode() == KeyEvent.VK_DOWN) {
                    return true; // Consume the arrow key events
                }
                return super.processKeyBinding(ks, e, condition, pressed);
            }
        };
        textArea.setFont(font);            
        textArea.setMargin(margin);
        textArea.setBackground(color);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);

        return textArea;
    } 

    public static String dateFormater(String deadline){
        String[] arr = deadline.split("-");
        LocalDate date = LocalDate.of(Integer.parseInt(arr[2]), Integer.parseInt(arr[0]), Integer.parseInt(arr[1]));
        DayOfWeek dayOfWeek = date.getDayOfWeek();
        String[] formattedDate = (date.format(DateTimeFormatter.ofPattern("MMM d"))).split(" ");
        for (String[] m: months){
            if (m[0].equals(formattedDate[0])){
                formattedDate[0] = m[1];
                break;
            }
        }
        String abbreviatedDayOfWeek = dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault());
        String output = formattedDate[0] + " " + formattedDate[1] + " (" + abbreviatedDayOfWeek + ")";
        return (output);
    }

    public static int[] convertToIntArray(String[] stringArray) {
        return Arrays.stream(stringArray)
                .mapToInt(Integer::parseInt)
                .toArray();
    }

    public Component duplicateComponent(Component component) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(component);
            oos.close();

            ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
            ObjectInputStream ois = new ObjectInputStream(bais);
            Component duplicate = (Component) ois.readObject();
            ois.close();

            if (duplicate instanceof JPanel){
                JPanel panel = (JPanel) duplicate;
                panel.removeAll();
                return (Component) panel;
            }

            if (duplicate instanceof JButton){
                JButton button = (JButton) duplicate;
                for (ActionListener listener : button.getActionListeners()) {
                    button.removeActionListener(listener);
                }
                return (Component) button;
            }

            return duplicate;
        } catch (IOException | ClassNotFoundException e) {
            JOptionPane.showMessageDialog(null, "Exception: " + e);
            return null;
        }
    }

    public static ArrayList<Element> sortElementsByDate(ArrayList<Element> elements) {
        Collections.sort(elements, new Comparator<Element>() {
            @Override
            public int compare(Element element1, Element element2) {
                if (element1.deadline == null && element2.deadline == null) {
                    return 0; // Both elements have no deadline, consider them equal
                } else if (element1.deadline == null) {
                    return 1; // element1 has no deadline, consider it greater than element2
                } else if (element2.deadline == null) {
                    return -1; // element2 has no deadline, consider it greater than element1
                } else {
                    return element1.deadline.compareTo(element2.deadline);
                }
            }
        });

        return elements;
    }

    public static Date parseDate(String dateString) {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy");
            return dateFormat.parse(dateString);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Exception: " + e);
            return null;
        }
    }

    public static String formatDate(Date date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy");
        return dateFormat.format(date);
    }

    private static void addKeyListeners(JScrollPane scrollPane) {
        JComponent view = (JComponent) scrollPane.getViewport().getView();

        view.setFocusable(true);
        view.requestFocusInWindow();

        view.addKeyListener(new KeyAdapter() {
            private static final int SCROLL_AMOUNT = 100;

            @Override
            public void keyPressed(KeyEvent e) {
                int keyCode = e.getKeyCode();
                int scrollValue = scrollPane.getHorizontalScrollBar().getValue();

                if (keyCode == KeyEvent.VK_LEFT) {
                    scrollPane.getHorizontalScrollBar().setValue(scrollValue - SCROLL_AMOUNT);
                } else if (keyCode == KeyEvent.VK_RIGHT) {
                    scrollPane.getHorizontalScrollBar().setValue(scrollValue + SCROLL_AMOUNT);
                }
            }
        });
    }

    public static ArrayList<String> findCurrentlyUsing(ArrayList<ArrayList<String>> data){
        ArrayList<String> usingCurrently = new ArrayList<String>();
        for (ArrayList<String> dataList: data){
            String[] list = dataList.get(0).split("#");
            if (list[3].equals("using")){
                usingCurrently = dataList;
                break;
            }
        }
        return (usingCurrently);
    }

    public static void refreshCollections(ArrayList<StoredReminder> storedReminders){
        int currentlyUsingIndex = Main.data.indexOf(Main.currentlyUsing);
        StoredReminder currentlyUsingStoredReminder = storedReminders.get(currentlyUsingIndex);
        for (StoredReminder sr: storedReminders){
            sr.containerPanel.setPreferredSize(new Dimension(340, 550));
            sr.containerPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 0));
            sr.textAreaPane.setPreferredSize(new Dimension(320, 490));

        }
        currentlyUsingStoredReminder.containerPanel.setPreferredSize(new Dimension(370, 580));
        currentlyUsingStoredReminder.containerPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.darkGray, 3), 
            BorderFactory.createEmptyBorder(10, 20, 10, 0))
        );
        currentlyUsingStoredReminder.textAreaPane.setPreferredSize(new Dimension(344, 514));

        
    };

    public static void updateCurrentlyUsingData(ArrayList<ArrayList<String>> data){
        for (ArrayList<String> dataList: data){
            String[] list = dataList.get(0).split("#");
            list[3] = "notusing";
            dataList.set(0, String.join("#", list));
        }
        String[] temp = Main.currentlyUsing.get(0).split("#");
        temp[3] = "using";
        Main.currentlyUsing.set(0, String.join("#", temp));
        updateData(data);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                frame = new Main();
            }
        });
    }
    //#endregion
}