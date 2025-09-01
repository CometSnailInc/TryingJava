import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.animation.AnimationTimer;
import javafx.scene.shape.ArcType;

public class StickFigureGame extends Application {
    private static final int WINDOW_WIDTH = 800;
    private static final int WINDOW_HEIGHT = 600;
    private static final int MOVE_SPEED = 3;
    private static final int BALL_RADIUS = 15;
    private static final int BALL_SPEED = 4;
    
    // Stick figure position
    private double playerX = WINDOW_WIDTH / 2;
    private double playerY = WINDOW_HEIGHT / 2;
    
    // Ball position and velocity
    private double ballX = 100;
    private double ballY = 100;
    private double ballVelX = BALL_SPEED;
    private double ballVelY = BALL_SPEED;
    
    // Game state
    private boolean gameOver = false;
    
    // Movement flags
    private boolean upPressed = false;
    private boolean downPressed = false;
    private boolean leftPressed = false;
    private boolean rightPressed = false;
    
    @Override
    public void start(Stage primaryStage) {
        // Create canvas for drawing
        Canvas canvas = new Canvas(WINDOW_WIDTH, WINDOW_HEIGHT);
        GraphicsContext gc = canvas.getGraphicsContext2D();
        
        // Create scene with canvas
        StackPane root = new StackPane();
        root.getChildren().add(canvas);
        Scene scene = new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT);
        
        // Handle key presses
        scene.setOnKeyPressed(this::handleKeyPressed);
        scene.setOnKeyReleased(this::handleKeyReleased);
        
        // Handle restart on game over
        scene.setOnMouseClicked(event -> {
            if (gameOver) {
                restartGame();
            }
        });
        
        // Create game loop
        AnimationTimer gameLoop = new AnimationTimer() {
            @Override
            public void handle(long now) {
                update();
                draw(gc);
            }
        };
        
        // Set up window
        primaryStage.setTitle("Stick Figure Wanderer");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
        
        // Focus on scene so it can receive key events
        canvas.setFocusTraversable(true);
        canvas.requestFocus();
        
        // Start the game loop
        gameLoop.start();
    }
    
    private void handleKeyPressed(KeyEvent event) {
        if (gameOver) return; // Don't move if game is over
        
        switch (event.getCode()) {
            case UP:
            case W:
                upPressed = true;
                break;
            case DOWN:
            case S:
                downPressed = true;
                break;
            case LEFT:
            case A:
                leftPressed = true;
                break;
            case RIGHT:
            case D:
                rightPressed = true;
                break;
        }
    }
    
    private void handleKeyReleased(KeyEvent event) {
        switch (event.getCode()) {
            case UP:
            case W:
                upPressed = false;
                break;
            case DOWN:
            case S:
                downPressed = false;
                break;
            case LEFT:
            case A:
                leftPressed = false;
                break;
            case RIGHT:
            case D:
                rightPressed = false;
                break;
        }
    }
    
    private void update() {
        if (gameOver) return; // Don't update if game is over
        
        // Update player position based on pressed keys
        if (upPressed && playerY > 20) {
            playerY -= MOVE_SPEED;
        }
        if (downPressed && playerY < WINDOW_HEIGHT - 20) {
            playerY += MOVE_SPEED;
        }
        if (leftPressed && playerX > 20) {
            playerX -= MOVE_SPEED;
        }
        if (rightPressed && playerX < WINDOW_WIDTH - 20) {
            playerX += MOVE_SPEED;
        }
        
        // Update ball position
        ballX += ballVelX;
        ballY += ballVelY;
        
        // Ball bouncing off walls
        if (ballX <= BALL_RADIUS || ballX >= WINDOW_WIDTH - BALL_RADIUS) {
            ballVelX *= -1;
        }
        if (ballY <= BALL_RADIUS || ballY >= WINDOW_HEIGHT - BALL_RADIUS) {
            ballVelY *= -1;
        }
        
        // Keep ball in bounds
        ballX = Math.max(BALL_RADIUS, Math.min(WINDOW_WIDTH - BALL_RADIUS, ballX));
        ballY = Math.max(BALL_RADIUS, Math.min(WINDOW_HEIGHT - BALL_RADIUS, ballY));
        
        // Check collision between player and ball
        double distance = Math.sqrt(Math.pow(playerX - ballX, 2) + Math.pow(playerY - ballY, 2));
        if (distance < BALL_RADIUS + 10) { // 10 is approximate radius of stick figure
            gameOver = true;
        }
    }
    
    private void draw(GraphicsContext gc) {
        // Clear screen with light blue background
        gc.setFill(Color.LIGHTBLUE);
        gc.fillRect(0, 0, WINDOW_WIDTH, WINDOW_HEIGHT);
        
        if (gameOver) {
            // Draw game over screen
            gc.setFill(Color.RED);
            gc.fillRect(0, 0, WINDOW_WIDTH, WINDOW_HEIGHT);
            
            gc.setFill(Color.WHITE);
            gc.fillText("GAME OVER!", WINDOW_WIDTH / 2 - 50, WINDOW_HEIGHT / 2 - 20);
            gc.fillText("Click to restart", WINDOW_WIDTH / 2 - 50, WINDOW_HEIGHT / 2 + 10);
            
            // Still draw the stick figure and ball at final positions
            drawStickFigure(gc, playerX, playerY);
            drawBall(gc, ballX, ballY);
        } else {
            // Draw stick figure
            drawStickFigure(gc, playerX, playerY);
            
            // Draw bouncing ball
            drawBall(gc, ballX, ballY);
            
            // Draw instructions
            gc.setFill(Color.BLACK);
            gc.fillText("Use arrow keys or WASD to avoid the red ball!", 20, 30);
        }
    }
    
    private void drawStickFigure(GraphicsContext gc, double x, double y) {
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(3);
        
        // Head (circle)
        gc.strokeOval(x - 8, y - 20, 16, 16);
        
        // Body (vertical line)
        gc.strokeLine(x, y - 4, x, y + 20);
        
        // Arms (horizontal line)
        gc.strokeLine(x - 12, y + 5, x + 12, y + 5);
        
        // Left leg
        gc.strokeLine(x, y + 20, x - 8, y + 35);
        
        // Right leg
        gc.strokeLine(x, y + 20, x + 8, y + 35);
        
        // Optional: Add a simple face
        gc.setFill(Color.BLACK);
        // Eyes
        gc.fillOval(x - 5, y - 16, 2, 2);
        gc.fillOval(x + 3, y - 16, 2, 2);
        // Smile (using arc with correct method signature)
        gc.strokeArc(x - 4, y - 14, 8, 6, 0, 180, ArcType.OPEN);
    }
    
    private void drawBall(GraphicsContext gc, double x, double y) {
        gc.setFill(Color.RED);
        gc.fillOval(x - BALL_RADIUS, y - BALL_RADIUS, BALL_RADIUS * 2, BALL_RADIUS * 2);
        
        // Add a white highlight to make it look more 3D
        gc.setFill(Color.WHITE);
        gc.fillOval(x - BALL_RADIUS + 3, y - BALL_RADIUS + 3, 6, 6);
    }
    
    private void restartGame() {
        // Reset game state
        gameOver = false;
        
        // Reset player position
        playerX = WINDOW_WIDTH / 2;
        playerY = WINDOW_HEIGHT / 2;
        
        // Reset ball position and velocity
        ballX = 100;
        ballY = 100;
        ballVelX = BALL_SPEED;
        ballVelY = BALL_SPEED;
        
        // Reset movement flags
        upPressed = false;
        downPressed = false;
        leftPressed = false;
        rightPressed = false;
    }
    
    public static void main(String[] args) {
        launch(args);
    }
}