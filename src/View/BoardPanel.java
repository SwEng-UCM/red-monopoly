package View;

import Controller.Controller;
import Model.Player;
import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BoardPanel extends JPanel {
    private final Controller controller;

    // Images for tiles
    private Image cornerImage;
    private Image propertyUpDownImage;
    private Image propertyLeftRightImage;
    private Image backgroundImage;

    // Manual overrides for specific tiles
    private final Map<Integer, Image> manualTileImages = new HashMap<>();

    protected boolean useImageDesign = true;
    protected final Map<Integer, JLabel> tileLabels = new HashMap<>();

    protected final Map<Player, Color> playerColors = new HashMap<>();
    private static final Color[] PLAYER_COLORS = {
            new Color(231, 76, 60),
            new Color(52, 152, 219),
            new Color(46, 204, 113),
            new Color(241, 196, 15),
            new Color(230, 126, 34),
            new Color(155, 89, 182),
            new Color(26, 188, 156),
            new Color(233, 30, 99)
    };

    public static final int DESIGN_SIZE = 728;
    private final JWindow zoomWindow = new JWindow();
    private final JLabel zoomLabel = new JLabel();

    public BoardPanel(Controller controller) {
        this.controller = controller;
        setLayout(null);
        setPreferredSize(new Dimension(DESIGN_SIZE, DESIGN_SIZE));

        // Load default images from classpath
        loadImages();

        zoomWindow.getContentPane().add(zoomLabel);
        zoomWindow.setAlwaysOnTop(true);
        zoomWindow.setBackground(new Color(0, 0, 0, 0));
        zoomWindow.setType(Window.Type.POPUP);

        initPlayerColors();
        initTiles();
    }

    private void loadImages() {
        // Background
        URL bgUrl = getClass().getResource("/background_board.png");
        if (bgUrl != null) {
            try {
                backgroundImage = ImageIO.read(bgUrl);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // Corner
        URL cornerUrl = getClass().getResource("/GULAG.png");
        if (cornerUrl != null) {
            try {
                cornerImage = ImageIO.read(cornerUrl);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // Properties
        URL propUDUrl = getClass().getResource("/property_norilsk.png");
        if (propUDUrl != null) {
            try {
                propertyUpDownImage = ImageIO.read(propUDUrl);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        URL propLRUrl = getClass().getResource("/property_norilsk_left.png");
        if (propLRUrl != null) {
            try {
                propertyLeftRightImage = ImageIO.read(propLRUrl);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void initPlayerColors() {
        List<Player> players = controller.getAllPlayers();
        for (int i = 0; i < players.size(); i++) {
            playerColors.put(players.get(i), PLAYER_COLORS[i % PLAYER_COLORS.length]);
        }
    }

    private void initTiles() {
        List<?> tiles = controller.getBoardTiles();
        if (tiles.size() != 40) {
            System.err.println("Warning: Expected 40 tiles, got " + tiles.size());
        }
        for (int i = 0; i < tiles.size(); i++) {
            JLabel tileLabel = createTileLabel(i);
            tileLabels.put(i, tileLabel);
            add(tileLabel);
        }
    }

    private JLabel createTileLabel(int index) {
        JLabel label = new JLabel("", SwingConstants.CENTER);
        label.setOpaque(true);
        label.setBorder(BorderFactory.createLineBorder(Color.BLACK));

        if (!useImageDesign) {
            // Fallback: simple colored labels
            label.setBackground(Color.LIGHT_GRAY);
            label.setText(String.valueOf(index));
        } else {
            label.addMouseListener(new MouseAdapter() {
                private final double ZOOM_FACTOR = 2.0;
                @Override public void mouseEntered(MouseEvent e) {
                    int w = label.getWidth(), h = label.getHeight();
                    Image img = manualTileImages.getOrDefault(index, getRawTileImage(index));
                    int zw = (int)(w * ZOOM_FACTOR), zh = (int)(h * ZOOM_FACTOR);
                    ImageIcon zoomIcon = new ImageIcon(img.getScaledInstance(zw, zh, Image.SCALE_SMOOTH));
                    zoomLabel.setIcon(zoomIcon);
                    zoomWindow.setSize(zw, zh);
                    Point loc = label.getLocationOnScreen();
                    zoomWindow.setLocation(loc.x + w/2, loc.y + h/2);
                    zoomWindow.setVisible(true);
                }
                @Override public void mouseExited(MouseEvent e) {
                    zoomWindow.setVisible(false);
                }
            });
        }
        return label;
    }

    private Image getRawTileImage(int index) {
        if (manualTileImages.containsKey(index)) {
            return manualTileImages.get(index);
        }
        if (index % 10 == 0) {
            return cornerImage;
        }
        if ((index > 0 && index < 10) || (index > 20 && index < 30)) {
            return propertyUpDownImage;
        }
        return propertyLeftRightImage;
    }

    @Override
    public void doLayout() {
        super.doLayout();
        int pw = getWidth(), ph = getHeight();
        double scale = Math.min(pw/(double)DESIGN_SIZE, ph/(double)DESIGN_SIZE);
        int bs = (int)(DESIGN_SIZE * scale);
        int ox = (pw - bs)/2, oy = (ph - bs)/2;

        for (int i = 0; i < 40; i++) {
            Rectangle db = getDesignBoundsForTile(i);
            int x = (int)(db.x*scale) + ox;
            int y = (int)(db.y*scale) + oy;
            int w = (int)(db.width*scale), h = (int)(db.height*scale);
            JLabel lbl = tileLabels.get(i);
            lbl.setBounds(x, y, w, h);
            if (useImageDesign) {
                lbl.setIcon(getTileImageIcon(i, w, h));
                lbl.setText("");
            }
        }
    }

    private Rectangle getDesignBoundsForTile(int index) {
        if (index >= 0 && index <= 10) {
            if (index == 0) return new Rectangle(DESIGN_SIZE - 112, DESIGN_SIZE - 112, 112, 112);
            if (index == 10) return new Rectangle(0, DESIGN_SIZE - 112, 112, 112);
            int x = (DESIGN_SIZE - 112) - (index * 56);
            return new Rectangle(x, DESIGN_SIZE - 112, 56, 112);
        } else if (index >= 11 && index <= 19) {
            int offset = index - 10;
            int y = DESIGN_SIZE - 112 - (offset * 56);
            return new Rectangle(0, y, 112, 56);
        } else if (index >= 20 && index <= 30) {
            if (index == 20) return new Rectangle(0, 0, 112, 112);
            if (index == 30) return new Rectangle(DESIGN_SIZE - 112, 0, 112, 112);
            int offset = index - 20;
            int x = 112 + (offset - 1) * 56;
            return new Rectangle(x, 0, 56, 112);
        } else if (index >= 31 && index <= 39) {
            int offset = index - 30;
            int y = 112 + (offset - 1) * 56;
            return new Rectangle(DESIGN_SIZE - 112, y, 112, 56);
        }
        return new Rectangle(0, 0, 0, 0);
    }

    private ImageIcon getTileImageIcon(int index, int w, int h) {
        Image img = manualTileImages.getOrDefault(index, getRawTileImage(index));
        return new ImageIcon(img.getScaledInstance(w, h, Image.SCALE_SMOOTH));
    }

    public JLabel getTileLabel(int tileIndex) {
        return tileLabels.get(tileIndex);
    }

    public Color getPlayerColor(Player player) {
        return playerColors.getOrDefault(player, Color.BLACK);
    }

    public void setManualTileImage(int index, Image img) {
        manualTileImages.put(index, img);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (backgroundImage != null) {
            int pw = getWidth(), ph = getHeight();
            double scale = Math.min(pw/(double)DESIGN_SIZE, ph/(double)DESIGN_SIZE);
            int bs = (int)(DESIGN_SIZE * scale);
            int ox = (pw - bs)/2, oy = (ph - bs)/2;
            g.drawImage(backgroundImage, ox, oy, bs, bs, this);
        }
    }
}
