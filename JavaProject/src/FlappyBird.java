import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

public class FlappyBird extends JPanel implements ActionListener, KeyListener {
    // Images
    private Image backgroundImg;
    private Image birdImg;
    private Image topPipeImg; // Separate images for top and bottom pipes
    private Image bottomPipeImg;

    // Bird properties
    private int birdY;
    private int birdVelocity;

    // Pipe properties
    private ArrayList<Pipe> pipes;
    private final int pipeGap = 200;
    private final int pipeWidth = 100;
    private final int pipeHeight = 600;
    private int pipeSpeed = 5;

    // Game state
    private boolean gameOver;

    public FlappyBird() {
        // Load images
        backgroundImg = new ImageIcon("JavaProject/src/flappybirdbg.png").getImage();
        birdImg = new ImageIcon("JavaProject/src/flappybird.png").getImage();
        topPipeImg = new ImageIcon("JavaProject/src/toppipe.png").getImage();
        bottomPipeImg = new ImageIcon("JavaProject/src/bottompipe (1).png").getImage();

        // Set panel properties
        setPreferredSize(new Dimension(800, 600));
        setBackground(Color.WHITE);
        setFocusable(true);
        addKeyListener(this);

        // Initialize bird position and velocity
        birdY = 300;
        birdVelocity = 0;

        // Initialize pipes
        pipes = new ArrayList<>();
        pipes.add(new Pipe(800, generateRandomPipeY()));

        // Start the game loop
        Timer timer = new Timer(20, this); // 20 milliseconds per frame
        timer.start();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        // Draw background
        g.drawImage(backgroundImg, 0, 0, getWidth(), getHeight(), this);
        // Draw bird
        g.drawImage(birdImg, 100, birdY, 50, 50, this);
        // Draw pipes
        for (Pipe pipe : pipes) {
            g.drawImage(topPipeImg, pipe.x, pipe.y, pipeWidth, pipeHeight, this);
            g.drawImage(bottomPipeImg, pipe.x, pipe.y - pipeHeight - pipeGap, pipeWidth, pipeHeight, this);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (!gameOver) {
            // Update game state
            birdY += birdVelocity;
            birdVelocity += 1; // Apply gravity

            // Move pipes
            for (Pipe pipe : pipes) {
                pipe.x -= pipeSpeed;
            }

            // Add new pipe if needed
            if (pipes.get(pipes.size() - 1).x < getWidth() - 300) {
                pipes.add(new Pipe(getWidth(), generateRandomPipeY()));
            }

            // Remove off-screen pipes
            if (pipes.get(0).x + pipeWidth < 0) {
                pipes.remove(0);
            }

            // Check for collisions
            if (birdY > getHeight() || birdY < 0) {
                // Game over
                gameOver();
            } else {
                for (Pipe pipe : pipes) {
                    if (pipe.intersects(100, birdY, 50, 50)) {
                        // Game over
                        gameOver();
                        break;
                    }
                }
            }
        }

        // Repaint the panel
        repaint();
    }

    private int generateRandomPipeY() {
        return (int) (Math.random() * (getHeight() - 200 - pipeGap)) + 100;
    }

    private void restartGame() {
        // Reset bird position and velocity
        birdY = 300;
        birdVelocity = 0;

        // Reset pipes
        pipes.clear();
        pipes.add(new Pipe(800, generateRandomPipeY()));

        // Reset game over state
        gameOver = false;
    }

    private void gameOver() {
        // Set game over state
        gameOver = true;

        // You can add any game over logic here
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyPressed(KeyEvent e) {
        // Handle key events
        if (e.getKeyCode() == KeyEvent.VK_SPACE) {
            if (gameOver) {
                restartGame();
            } else {
                birdVelocity = -15; // Flap the bird
            }
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }

    private static class Pipe {
        int x, y;

        public Pipe(int x, int y) {
            this.x = x;
            this.y = y;
        }

        public boolean intersects(int birdX, int birdY, int birdWidth, int birdHeight) {
            Rectangle birdRect = new Rectangle(birdX, birdY, birdWidth, birdHeight);
            Rectangle topPipeRect = new Rectangle(x, y, 100, 600);
            Rectangle bottomPipeRect = new Rectangle(x, y - 600 - 200, 100, 600);
            return birdRect.intersects(topPipeRect) || birdRect.intersects(bottomPipeRect);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Flappy Bird");
            FlappyBird gamePanel = new FlappyBird();
            frame.setContentPane(gamePanel);
            frame.pack();
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setLocationRelativeTo(null); // Center the window
            frame.setVisible(true);
        });
    }
}
