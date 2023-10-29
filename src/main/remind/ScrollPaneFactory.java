package main.remind;

import javax.swing.*;
import javax.swing.plaf.basic.BasicScrollBarUI;
import java.awt.*;

public class ScrollPaneFactory extends BasicScrollBarUI {
    private static final int THUMB_SIZE = 8;
    private static final int THUMB_BORDER_SIZE = 2;
    private final Color trackColor;
    private final Color thumbColor;
    private final Color thumbHoverColor;
    private final Color thumbPressedColor;

    public ScrollPaneFactory(Component component) {
        this.trackColor = component.getBackground();
        this.thumbColor = trackColor.darker();
        this.thumbHoverColor = thumbColor.darker();
        this.thumbPressedColor = thumbHoverColor.darker();
    }

    static JScrollPane newScrollPane(Component component) {
        JScrollPane scrollPane = new JScrollPane();
        scrollPane.getVerticalScrollBar().setUI(new ScrollPaneFactory(component));
        scrollPane.getHorizontalScrollBar().setUI(new ScrollPaneFactory(component));
        scrollPane.setViewportView(component);
        JPanel corner = new JPanel();
        corner.setBackground(component.getBackground());
        scrollPane.setCorner(JScrollPane.LOWER_RIGHT_CORNER, corner);

        return scrollPane;
    }

    @Override
    protected void configureScrollBarColors() {
        // Customize scrollbar colors
        LookAndFeel.installColors(scrollbar, "ScrollBar.background", "ScrollBar.foreground");
    }

    @Override
    protected JButton createDecreaseButton(int orientation) {
        return createEmptyButton();
    }

    @Override
    protected JButton createIncreaseButton(int orientation) {
        return createEmptyButton();
    }

    private JButton createEmptyButton() {
        JButton button = new JButton();
        button.setPreferredSize(new Dimension(0, 0));
        return button;
    }

    @Override
    protected void paintTrack(Graphics g, JComponent c, Rectangle trackBounds) {
        g.setColor(trackColor);
        g.fillRect(trackBounds.x, trackBounds.y, trackBounds.width, trackBounds.height);
    }

    @Override
    protected void paintThumb(Graphics g, JComponent c, Rectangle thumbBounds) {
        Graphics2D g2 = (Graphics2D) g.create();

        // Draw thumb background
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        boolean isThumbRollover = false, isThumbPressed = false;
        try {
            isThumbRollover = scrollbar.getMousePosition() != null && thumbBounds.contains(scrollbar.getMousePosition());
            isThumbPressed = scrollbar.getValueIsAdjusting();
        } catch (Exception e) {
            
        }
        Color color = thumbColor;
        if (isThumbPressed) {
            color = thumbPressedColor;
        } else if (isThumbRollover) {
            color = thumbHoverColor;
        }

        g2.setPaint(color);
        g2.fillRoundRect(thumbBounds.x + THUMB_BORDER_SIZE, thumbBounds.y + THUMB_BORDER_SIZE,
                thumbBounds.width - THUMB_BORDER_SIZE * 2, thumbBounds.height - THUMB_BORDER_SIZE * 2,
                THUMB_SIZE, THUMB_SIZE);

        g2.dispose();
    }
}

