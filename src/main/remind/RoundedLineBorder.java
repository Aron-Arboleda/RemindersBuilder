package main.remind;

import javax.swing.border.*;
import java.awt.*;

public class RoundedLineBorder extends LineBorder {
private int cornerRadius;

public RoundedLineBorder(Color color, int thickness, int cornerRadius) {
    super(color, thickness);
    this.cornerRadius = cornerRadius;
}

@Override
public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
    Graphics2D g2 = (Graphics2D) g.create();
    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    g2.setStroke(new BasicStroke(getThickness()));
    g2.setColor(getLineColor());

    int arc = cornerRadius * 2;
    g2.drawRoundRect(x, y, width - 1, height - 1, arc, arc);

    g2.dispose();
}
}