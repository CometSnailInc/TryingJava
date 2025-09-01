import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import javafx.animation.AnimationTimer;

public class Pong extends Application {
    private static final int WINDOW_WIDTH = 800;
    private static final int WINDOW_HEIGHT = 600;
    private static final int PADDLE_WIDTH = 20;
    private static final int PADDLE_HEIGHT = 100;
    private static final int BALL_SIZE = 20;
    private static final double PADDLE_SPEED = 5.0;
    private static final double INITIAL_BALL_SPEED = 3.0;
    
    // Game objects
    private double player1Y = WINDOW_HEIGHT / 2 - PADDLE_HEIGHT / 2;
    private double player2Y = WINDOW_HEIGHT / 2 - PADDLE_HEIGHT / 2;
    private double ballX = WINDOW_WIDTH / 2;
    private double ballY = WINDOW_HEIGHT / 2;
    private double ballVelX = INITIAL_BALL_SPEED;
    private double ballVelY = INITIAL_BALL_SPEED;
    
    // Game state
    private int player1Score = 0;
    private int player2Score = 0;
    private boolean gameStarted = false;
    private boolean gameOver = false;
    private final int WINNING_SCORE = 10;
    
    // Controls
    private boolean wPressed = false;
    private boolean sPressed = false;
    private boolean upPressed = false;
    private boolean downPressed = false;
    
    @Override
    public void start(Stage primaryStage) {
        Canvas canvas = new Canvas(WINDOW_WIDTH, WINDOW_HEIGHT);
        GraphicsContext gc = canvas.getGraphicsContext2D();
        
        StackPane root = new StackPane();
        root.getChildren().add(canvas);
        Scene scene = new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT);
        
        // Handle key events
        scene.setOnKeyPressed(this::handleKeyPressed);
        scene.setOnKeyReleased(this::handleKeyReleased);
        
        // Game loop
        AnimationTimer gameLoop = new AnimationTimer() {
            @Override
            public void handle(long now) {
                update();
                draw(gc);
            }
        };
        
        primaryStage.setTitle("Pong - First to 10 Wins!");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
        
        canvas.setFocusTraversable(true);
        canvas.requestFocus();
        
        gameLoop.start();
    }
    
    private void handleKeyPressed(KeyEvent event) {
        switch (event.getCode()) {
            // Player 1 controls (W/S)
            case W:
                wPressed = true;
                break;
            case S:
                sPressed = true;
                break;
            // Player 2 controls (Up/Down arrows)
            case UP:
                upPressed = true;
                break;
            case DOWN:
                downPressed = true;
                break;
            // Game controls
            case SPACE:
                if (!gameStarted && !gameOver) {
                    gameStarted = true;
                }
                break;
            case R:
                if (gameOver) {
                    restart();
                }
                break;
            case ESCAPE:
                if (gameStarted && !gameOver) {
                    gameStarted = false; // Pause
                }
                break;
        }
    }
    
    private void handleKeyReleased(KeyEvent event) {
        switch (event.getCode()) {
            case W:
                wPressed = false;
                break;
            case S:
                sPressed = false;
                break;
            case UP:
                upPressed = false;
                break;
            case DOWN:
                downPressed = false;
                break;
        }
    }
    
    private void update() {
        if (!gameStarted || gameOver) return;
        
        // Update paddles
        updatePaddles();
        
        // Update ball
        updateBall();
        
        // Check for scoring
        checkScoring();
        
        // Check for game over
        if (player1Score >= WINNING_SCORE || player2Score >= WINNING_SCORE) {
            gameOver = true;
        }
    }
    
    private void updatePaddles() {
        // Player 1 (left paddle)
        if (wPressed && player1Y > 0) {
            player1Y -= PADDLE_SPEED;
        }
        if (sPressed && player1Y < WINDOW_HEIGHT - PADDLE_HEIGHT) {
            player1Y += PADDLE_SPEED;
        }
        
        // Player 2 (right paddle)
        if (upPressed && player2Y > 0) {
            player2Y -= PADDLE_SPEED;
        }
        if (downPressed && player2Y < WINDOW_HEIGHT - PADDLE_HEIGHT) {
            player2Y += PADDLE_SPEED;
        }
        
        // Keep paddles in bounds
        player1Y = Math.max(0, Math.min(WINDOW_HEIGHT - PADDLE_HEIGHT, player1Y));
        player2Y = Math.max(0, Math.min(WINDOW_HEIGHT - PADDLE_HEIGHT, player2Y));
    }
    
    private void updateBall() {
        ballX += ballVelX;
        ballY += ballVelY;
        
        // Ball collision with top and bottom walls
        if (ballY <= 0 || ballY >= WINDOW_HEIGHT - BALL_SIZE) {
            ballVelY = -ballVelY;
            ballY = Math.max(0, Math.min(WINDOW_HEIGHT - BALL_SIZE, ballY));
        }
        
        // Ball collision with paddles
        checkPaddleCollision();
    }
    
    private void checkPaddleCollision() {
        // Left paddle collision (Player 1)
        if (ballX <= PADDLE_WIDTH && 
            ballY + BALL_SIZE >= player1Y && 
            ballY <= player1Y + PADDLE_HEIGHT &&
            ballVelX < 0) {
            
            ballVelX = -ballVelX;
            ballX = PADDLE_WIDTH;
            
            // Add some spin based on where ball hits paddle
            double paddleCenter = player1Y + PADDLE_HEIGHT / 2;
            double hitPosition = (ballY + BALL_SIZE / 2 - paddleCenter) / (PADDLE_HEIGHT / 2);
            ballVelY += hitPosition * 2; // Add spin
            
            // Speed up slightly
            ballVelX *= 1.05;
            ballVelY *= 1.05;
        }
        
        // Right paddle collision (Player 2)
        if (ballX + BALL_SIZE >= WINDOW_WIDTH - PADDLE_WIDTH && 
            ballY + BALL_SIZE >= player2Y && 
            ballY <= player2Y + PADDLE_HEIGHT &&
            ballVelX > 0) {
            
            ballVelX = -ballVelX;
            ballX = WINDOW_WIDTH - PADDLE_WIDTH - BALL_SIZE;
            
            // Add some spin based on where ball hits paddle
            double paddleCenter = player2Y + PADDLE_HEIGHT / 2;
            double hitPosition = (ballY + BALL_SIZE / 2 - paddleCenter) / (PADDLE_HEIGHT / 2);
            ballVelY += hitPosition * 2; // Add spin
            
            // Speed up slightly
            ballVelX *= 1.05;
            ballVelY *= 1.05;
        }
        
        // Limit ball speed
        double maxSpeed = 8.0;
        if (Math.abs(ballVelX) > maxSpeed) {
            ballVelX = ballVelX > 0 ? maxSpeed : -maxSpeed;
        }
        if (Math.abs(ballVelY) > maxSpeed) {
            ballVelY = ballVelY > 0 ? maxSpeed : -maxSpeed;
        }
    }
    
    private void checkScoring() {
        // Player 2 scores (ball went off left side)
        if (ballX < 0) {
            player2Score++;
            resetBall(false); // Ball starts going toward player 1
        }
        
        // Player 1 scores (ball went off right side)
        if (ballX > WINDOW_WIDTH) {
            player1Score++;
            resetBall(true); // Ball starts going toward player 2
        }
    }
    
    private void resetBall(boolean towardPlayer2) {
        ballX = WINDOW_WIDTH / 2;
        ballY = WINDOW_HEIGHT / 2;
        ballVelX = towardPlayer2 ? INITIAL_BALL_SPEED : -INITIAL_BALL_SPEED;
        ballVelY = (Math.random() - 0.5) * 4; // Random Y velocity
    }
    
    private void restart() {
        player1Score = 0;
        player2Score = 0;
        gameStarted = false;
        gameOver = false;
        player1Y = WINDOW_HEIGHT / 2 - PADDLE_HEIGHT / 2;
        player2Y = WINDOW_HEIGHT / 2 - PADDLE_HEIGHT / 2;
        resetBall(Math.random() > 0.5);
    }
    
    private void draw(GraphicsContext gc) {
        // Clear screen with black background
        gc.setFill(Color.BLACK);
        gc.fillRect(0, 0, WINDOW_WIDTH, WINDOW_HEIGHT);
        
        // Draw center line
        gc.setStroke(Color.WHITE);
        gc.setLineWidth(2);
        gc.setLineDashes(10, 10);
        gc.strokeLine(WINDOW_WIDTH / 2, 0, WINDOW_WIDTH / 2, WINDOW_HEIGHT);
        gc.setLineDashes(null); // Reset line dashes
        
        // Draw paddles
        gc.setFill(Color.WHITE);
        gc.fillRect(0, player1Y, PADDLE_WIDTH, PADDLE_HEIGHT); // Player 1 paddle
        gc.fillRect(WINDOW_WIDTH - PADDLE_WIDTH, player2Y, PADDLE_WIDTH, PADDLE_HEIGHT); // Player 2 paddle
        
        // Draw ball
        if (gameStarted && !gameOver) {
            gc.fillRect(ballX, ballY, BALL_SIZE, BALL_SIZE);
        }
        
        // Draw scores
        gc.setFont(Font.font("Arial", FontWeight.BOLD, 48));
        gc.setTextAlign(TextAlignment.CENTER);
        gc.fillText(String.valueOf(player1Score), WINDOW_WIDTH / 4, 60);
        gc.fillText(String.valueOf(player2Score), 3 * WINDOW_WIDTH / 4, 60);
        
        // Draw game state messages
        gc.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        
        if (!gameStarted && !gameOver) {
            gc.fillText("PONG", WINDOW_WIDTH / 2, WINDOW_HEIGHT / 2 - 60);
            gc.setFont(Font.font("Arial", 16));
            gc.fillText("Player 1: W/S keys", WINDOW_WIDTH / 2, WINDOW_HEIGHT / 2 - 20);
            gc.fillText("Player 2: UP/DOWN arrows", WINDOW_WIDTH / 2, WINDOW_HEIGHT / 2);
            gc.fillText("Press SPACE to start", WINDOW_WIDTH / 2, WINDOW_HEIGHT / 2 + 40);
            gc.fillText("First to " + WINNING_SCORE + " wins!", WINDOW_WIDTH / 2, WINDOW_HEIGHT / 2 + 80);
        }
        
        if (gameOver) {
            String winner = player1Score >= WINNING_SCORE ? "Player 1 Wins!" : "Player 2 Wins!";
            gc.setFont(Font.font("Arial", FontWeight.BOLD, 36));
            gc.fillText(winner, WINDOW_WIDTH / 2, WINDOW_HEIGHT / 2 - 20);
            gc.setFont(Font.font("Arial", 18));
            gc.fillText("Press R to restart", WINDOW_WIDTH / 2, WINDOW_HEIGHT / 2 + 20);
        }
        
        // Draw controls reminder
        if (gameStarted && !gameOver) {
            gc.setFont(Font.font("Arial", 12));
            gc.fillText("ESC to pause", WINDOW_WIDTH / 2, WINDOW_HEIGHT - 20);
        }
    }
    
    public static void main(String[] args) {
        launch(args);
    }
}