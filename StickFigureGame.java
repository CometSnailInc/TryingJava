import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.animation.AnimationTimer;

public class StickFigureGame extends Application {
    private static final int WINDOW_WIDTH = 800;
    private static final int WINDOW_HEIGHT = 600;
    private static final int MOVE_SPEED = 3;
    
    // Stick figure position
    private double playerX = WINDOW_WIDTH / 2;
    private double playerY = WINDOW_HEIGHT / 2;
    
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
    }
    
    private void draw(GraphicsContext gc) {
        // Clear screen with light blue background
        gc.setFill(Color.LIGHTBLUE);
        gc.fillRect(0, 0, WINDOW_WIDTH, WINDOW_HEIGHT);
        
        // Draw stick figure
        drawStickFigure(gc, playerX, playerY);
        
        // Draw instructions
        gc.setFill(Color.BLACK);
        gc.fillText("Use arrow keys or WASD to move the stick figure around!", 20, 30);
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
        // Smile
        gc.strokeArc(x - 4, y - 14, 8, 6, 0, -180, false);
    }
    
    public static void main(String[] args) {
        launch(args);
    }
}