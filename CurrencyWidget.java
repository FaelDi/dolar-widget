import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CurrencyWidget extends JFrame {
    private JLabel brlLabel;
    private JLabel statusLabel;
    private JLabel timeLabel;
    private JButton refreshButton;
    private Timer autoRefreshTimer;
    private Timer clockTimer;
    private Point mouseDownCompCoords;
    private static final String API_URL = "https://api.fixer.io/latest?access_key=YOUR_API_KEY&base=USD&symbols=BRL";
    private static final String BACKUP_API = "https://api.exchangerate-api.com/v4/latest/USD";
    
    private static final Color BG_COLOR = new Color(15, 15, 35);
    private static final Color CARD_COLOR = new Color(25, 25, 45);
    private static final Color ACCENT_COLOR = new Color(0, 255, 127);
    private static final Color TEXT_COLOR = new Color(220, 220, 220);
    private static final Color BORDER_COLOR = new Color(60, 60, 100);
    
    public CurrencyWidget() {
        initializeGUI();
        loadExchangeRates();
        
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                if (autoRefreshTimer != null) {
                    autoRefreshTimer.stop();
                }
                if (clockTimer != null) {
                    clockTimer.stop();
                }
                System.exit(0);
            }
        });
    }
    
    private void initializeGUI() {
        setTitle("USD to BRL");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setUndecorated(true);
        setAlwaysOnTop(true);
        setSize(350, 160);
        setLocationRelativeTo(null);
        setResizable(false);
        
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        mainPanel.setBackground(BG_COLOR);
        mainPanel.setBorder(new CompoundBorder(
            new LineBorder(BORDER_COLOR, 2, true),
            new EmptyBorder(15, 15, 15, 15)
        ));
        
        JPanel headerPanel = createHeaderPanel();
        JPanel centerPanel = createCenterPanel();
        JPanel footerPanel = createFooterPanel();
        
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(centerPanel, BorderLayout.CENTER);
        mainPanel.add(footerPanel, BorderLayout.SOUTH);
        
        add(mainPanel);
        
        setupDragAndDrop();
        setupTimers();
    }
    
    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(BG_COLOR);
        
        JLabel titleLabel = new JLabel(" CURRENCY MONITOR", SwingConstants.LEFT);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        titleLabel.setForeground(ACCENT_COLOR);
        
        timeLabel = new JLabel("", SwingConstants.RIGHT);
        timeLabel.setFont(new Font("Consolas", Font.PLAIN, 10));
        timeLabel.setForeground(TEXT_COLOR);
        updateClock();
        
        JButton closeButton = new JButton("X");
        closeButton.setFont(new Font("Arial", Font.BOLD, 12));
        closeButton.setForeground(Color.WHITE);
        closeButton.setBackground(new Color(220, 53, 69));
        closeButton.setBorder(BorderFactory.createEmptyBorder(2, 6, 2, 6));
        closeButton.setFocusPainted(false);
        closeButton.addActionListener(e -> System.exit(0));
        
        panel.add(titleLabel, BorderLayout.WEST);
        panel.add(timeLabel, BorderLayout.CENTER);
        panel.add(closeButton, BorderLayout.EAST);
        
        return panel;
    }
    
    private JPanel createCenterPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(CARD_COLOR);
        panel.setBorder(new CompoundBorder(
            new LineBorder(BORDER_COLOR, 1, true),
            new EmptyBorder(20, 20, 20, 20)
        ));
        
        brlLabel = new JLabel("Loading exchange rate...", SwingConstants.CENTER);
        brlLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        brlLabel.setForeground(ACCENT_COLOR);
        brlLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel currencyLabel = new JLabel("USD -> BRL", SwingConstants.CENTER);
        currencyLabel.setFont(new Font("Consolas", Font.PLAIN, 12));
        currencyLabel.setForeground(TEXT_COLOR);
        currencyLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        panel.add(Box.createVerticalGlue());
        panel.add(brlLabel);
        panel.add(Box.createRigidArea(new Dimension(0, 5)));
        panel.add(currencyLabel);
        panel.add(Box.createVerticalGlue());
        
        return panel;
    }
    
    private JPanel createFooterPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(BG_COLOR);
        
        statusLabel = new JLabel(" Connecting...", SwingConstants.LEFT);
        statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        statusLabel.setForeground(new Color(255, 193, 7));
        
        refreshButton = new JButton("R");
        refreshButton.setFont(new Font("Arial", Font.PLAIN, 12));
        refreshButton.setForeground(TEXT_COLOR);
        refreshButton.setBackground(CARD_COLOR);
        refreshButton.setBorder(new LineBorder(BORDER_COLOR, 1));
        refreshButton.setFocusPainted(false);
        refreshButton.setPreferredSize(new Dimension(30, 25));
        refreshButton.addActionListener(e -> loadExchangeRates());
        
        panel.add(statusLabel, BorderLayout.WEST);
        panel.add(refreshButton, BorderLayout.EAST);
        
        return panel;
    }
    
    private void setupDragAndDrop() {
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                mouseDownCompCoords = e.getPoint();
            }
        });
        
        addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                Point currCoords = e.getLocationOnScreen();
                setLocation(currCoords.x - mouseDownCompCoords.x, currCoords.y - mouseDownCompCoords.y);
            }
        });
    }
    
    private void setupTimers() {
        autoRefreshTimer = new Timer(300000, e -> loadExchangeRates());
        autoRefreshTimer.start();
        
        clockTimer = new Timer(1000, e -> updateClock());
        clockTimer.start();
    }
    
    private void updateClock() {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        timeLabel.setText(sdf.format(new Date()));
    }
    
    private void loadExchangeRates() {
        statusLabel.setText("• Updating...");
        statusLabel.setForeground(new Color(255, 193, 7));
        
        SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
            private String brlRate = "Error";
            private boolean success = false;
            
            @Override
            protected Void doInBackground() throws Exception {
                try {
                    URL url = new URL(BACKUP_API);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");
                    connection.setConnectTimeout(5000);
                    connection.setReadTimeout(5000);
                    
                    BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String line;
                    
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    reader.close();
                    
                    String jsonResponse = response.toString();
                    
                    Pattern brlPattern = Pattern.compile("\"BRL\":(\\d+\\.\\d+)");
                    Matcher brlMatcher = brlPattern.matcher(jsonResponse);
                    
                    if (brlMatcher.find()) {
                        double rate = Double.parseDouble(brlMatcher.group(1));
                        brlRate = String.format("%.2f", rate);
                        success = true;
                    }
                    
                } catch (Exception e) {
                    e.printStackTrace();
                    brlRate = "Error";
                }
                return null;
            }
            
            @Override
            protected void done() {
                if (success) {
                    brlLabel.setText("R$ " + brlRate);
                    statusLabel.setText(" Connected");
                    statusLabel.setForeground(new Color(40, 167, 69));
                } else {
                    brlLabel.setText("Connection Error");
                    statusLabel.setText(" Offline");
                    statusLabel.setForeground(new Color(220, 53, 69));
                }
            }
        };
        
        worker.execute();
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                
                new CurrencyWidget().setVisible(true);
            }
        });
    }
}