import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.animation.AnimationTimer;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ClaudeSansBattle extends Application {
    private static final int WINDOW_WIDTH = 800;
    private static final int WINDOW_HEIGHT = 600;
    private static final int PLAYER_SIZE = 8;
    private static final double PLAYER_SPEED = 3.0;
    
    // Battle box boundaries
    private static final int BOX_LEFT = 200;
    private static final int BOX_RIGHT = 600;
    private static final int BOX_TOP = 300;
    private static final int BOX_BOTTOM = 500;
    
    // Player (heart) position
    private double playerX = (BOX_LEFT + BOX_RIGHT) / 2;
    private double playerY = (BOX_TOP + BOX_BOTTOM) / 2;
    
    // Movement
    private boolean leftPressed = false;
    private boolean rightPressed = false;
    private boolean upPressed = false;
    private boolean downPressed = false;
    
    // Game state
    private int playerHP = 20;
    private int maxHP = 20;
    private boolean gameOver = false;
    private boolean playerTurn = true;
    private double turnTimer = 0;
    private int attackPhase = 0;
    
    // Attacks
    private List<Bone> bones = new ArrayList<>();
    private List<GasterBlaster> blasters = new ArrayList<>();
    private Random random = new Random();
    private double attackTimer = 0;
    
    // Sans dialogue
    private String currentDialogue = "it's a beautiful day outside.";
    private double dialogueTimer = 0;
    private String[] dialogues = {
        "it's a beautiful day outside.",
        "birds are singing, flowers are blooming...",
        "on days like these, kids like you...",
        "Should be burning in hell.",
        "heh. always wondered why people never use their strongest attack first.",
        "here we go."
    };
    private int dialogueIndex = 0;
    
    @Override
    public void start(Stage primaryStage) {
        Canvas canvas = new Canvas(WINDOW_WIDTH, WINDOW_HEIGHT);
        GraphicsContext gc = canvas.getGraphicsContext2D();
        
        StackPane root = new StackPane();
        root.getChildren().add(canvas);
        Scene scene = new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT);
        
        scene.setOnKeyPressed(this::handleKeyPressed);
        scene.setOnKeyReleased(this::handleKeyReleased);
        
        AnimationTimer gameLoop = new AnimationTimer() {
            @Override
            public void handle(long now) {
                update();
                draw(gc);
            }
        };
        
        primaryStage.setTitle("Bad Time Simulator");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
        
        canvas.setFocusTraversable(true);
        canvas.requestFocus();
        
        gameLoop.start();
    }
    
    private void handleKeyPressed(KeyEvent event) {
        switch (event.getCode()) {
            case LEFT:
            case A:
                if (!gameOver) leftPressed = true;
                break;
            case RIGHT:
            case D:
                if (!gameOver) rightPressed = true;
                break;
            case UP:
            case W:
                if (!gameOver) upPressed = true;
                break;
            case DOWN:
            case S:
                if (!gameOver) downPressed = true;
                break;
            case SPACE:
                if (playerTurn && !gameOver) {
                    startSansAttack();
                }
                break;
            case R:
                if (gameOver) {
                    restart();
                }
                break;
        }
    }
    
    private void handleKeyReleased(KeyEvent event) {
        switch (event.getCode()) {
            case LEFT:
            case A:
                leftPressed = false;
                break;
            case RIGHT:
            case D:
                rightPressed = false;
                break;
            case UP:
            case W:
                upPressed = false;
                break;
            case DOWN:
            case S:
                downPressed = false;
                break;
        }
    }
    
    private void update() {
        if (gameOver) return;
        
        if (playerTurn) {
            updatePlayerTurn();
        } else {
            updateSansAttack();
        }
    }
    
    private void updatePlayerTurn() {
        dialogueTimer += 0.016; // ~60fps
        
        if (dialogueTimer > 2.0 && dialogueIndex < dialogues.length - 1) {
            dialogueIndex++;
            currentDialogue = dialogues[dialogueIndex];
            dialogueTimer = 0;
        }
    }
    
    private void updateSansAttack() {
        // Update player movement
        double newX = playerX;
        double newY = playerY;
        
        if (leftPressed) newX -= PLAYER_SPEED;
        if (rightPressed) newX += PLAYER_SPEED;
        if (upPressed) newY -= PLAYER_SPEED;
        if (downPressed) newY += PLAYER_SPEED;
        
        // Keep player in battle box
        newX = Math.max(BOX_LEFT + PLAYER_SIZE, Math.min(BOX_RIGHT - PLAYER_SIZE, newX));
        newY = Math.max(BOX_TOP + PLAYER_SIZE, Math.min(BOX_BOTTOM - PLAYER_SIZE, newY));
        
        playerX = newX;
        playerY = newY;
        
        // Update attacks
        attackTimer += 0.016;
        
        // Spawn attacks based on phase
        if (attackTimer > 0.5) {
            spawnAttack();
            attackTimer = 0;
        }
        
        // Update bones
        for (int i = bones.size() - 1; i >= 0; i--) {
            Bone bone = bones.get(i);
            bone.update();
            
            // Check collision
            if (bone.collidesWith(playerX, playerY, PLAYER_SIZE)) {
                takeDamage();
            }
            
            // Remove if off screen
            if (bone.isOffScreen()) {
                bones.remove(i);
            }
        }
        
        // Update blasters
        for (int i = blasters.size() - 1; i >= 0; i--) {
            GasterBlaster blaster = blasters.get(i);
            blaster.update();
            
            // Check collision with beam
            if (blaster.beamCollidesWith(playerX, playerY, PLAYER_SIZE)) {
                takeDamage();
            }
            
            if (blaster.isFinished()) {
                blasters.remove(i);
            }
        }
        
        turnTimer += 0.016;
        if (turnTimer > 8.0) { // Attack lasts 8 seconds
            endSansAttack();
        }
    }
    
    private void startSansAttack() {
        playerTurn = false;
        turnTimer = 0;
        attackTimer = 0;
        attackPhase++;
        bones.clear();
        blasters.clear();
        currentDialogue = "";
    }
    
    private void endSansAttack() {
        playerTurn = true;
        turnTimer = 0;
        currentDialogue = "* Sans is sparing you.";
        
        if (attackPhase >= 3) {
            currentDialogue = "what? you think i'm just gonna stand there and take it?";
        }
    }
    
    private void spawnAttack() {
        switch (attackPhase % 4) {
            case 1:
                spawnBoneWall();
                break;
            case 2:
                spawnBoneSlam();
                break;
            case 3:
                spawnGasterBlaster();
                break;
            case 0:
                spawnRandomBones();
                break;
        }
    }
    
    private void spawnBoneWall() {
        for (int i = 0; i < 8; i++) {
            if (i != 3 && i != 4) { // Leave a gap
                bones.add(new Bone(BOX_LEFT - 50, BOX_TOP + i * 25, 2, 0, 20, 20));
            }
        }
    }
    
    private void spawnBoneSlam() {
        double targetX = playerX;
        bones.add(new Bone(targetX - 10, BOX_TOP - 100, 0, 3, 20, 80));
    }
    
    private void spawnGasterBlaster() {
        double x = BOX_LEFT - 100;
        double y = playerY;
        // Angle pointing right (0 radians = pointing right)
        blasters.add(new GasterBlaster(x, y, 0));
    }
    
    private void spawnRandomBones() {
        if (random.nextDouble() < 0.3) {
            double x = random.nextDouble() * (BOX_RIGHT - BOX_LEFT) + BOX_LEFT;
            bones.add(new Bone(x, BOX_BOTTOM + 20, 0, -2, 15, 40));
        }
    }
    
    private void takeDamage() {
        playerHP -= 1;
        if (playerHP <= 0) {
            gameOver = true;
            currentDialogue = "geeettttttt dunked on!!!";
        }
    }
    
    private void restart() {
        gameOver = false;
        playerTurn = true;
        playerHP = maxHP;
        attackPhase = 0;
        dialogueIndex = 0;
        currentDialogue = dialogues[0];
        dialogueTimer = 0;
        turnTimer = 0;
        attackTimer = 0;
        playerX = (BOX_LEFT + BOX_RIGHT) / 2;
        playerY = (BOX_TOP + BOX_BOTTOM) / 2;
        bones.clear();
        blasters.clear();
    }
    
    private void draw(GraphicsContext gc) {
        // Black background
        gc.setFill(Color.BLACK);
        gc.fillRect(0, 0, WINDOW_WIDTH, WINDOW_HEIGHT);
        
        // Draw battle box
        gc.setStroke(Color.WHITE);
        gc.setLineWidth(3);
        gc.strokeRect(BOX_LEFT, BOX_TOP, BOX_RIGHT - BOX_LEFT, BOX_BOTTOM - BOX_TOP);
        
        if (!playerTurn || gameOver) {
            // Draw attacks
            for (Bone bone : bones) {
                bone.draw(gc);
            }
            
            for (GasterBlaster blaster : blasters) {
                blaster.draw(gc);
            }
        }
        
        // Draw player (red heart)
        gc.setFill(Color.RED);
        if (!gameOver) {
            gc.fillRect(playerX - PLAYER_SIZE/2, playerY - PLAYER_SIZE/2, PLAYER_SIZE, PLAYER_SIZE);
        }
        
        // Draw UI
        drawUI(gc);
    }
    
    private void drawUI(GraphicsContext gc) {
        // HP bar
        gc.setFill(Color.WHITE);
        gc.setFont(Font.font("Monospace", FontWeight.BOLD, 16));
        gc.fillText("HP", 50, 550);
        
        // HP bar background
        gc.setFill(Color.RED);
        gc.fillRect(80, 535, 200, 20);
        
        // Current HP
        gc.setFill(Color.YELLOW);
        double hpWidth = (double) playerHP / maxHP * 200;
        gc.fillRect(80, 535, hpWidth, 20);
        
        // HP numbers
        gc.setFill(Color.WHITE);
        gc.fillText(playerHP + " / " + maxHP, 290, 550);
        
        // Dialogue box
        gc.setStroke(Color.WHITE);
        gc.setLineWidth(2);
        gc.strokeRect(50, 50, WINDOW_WIDTH - 100, 100);
        
        gc.setFill(Color.WHITE);
        gc.setFont(Font.font("Monospace", 14));
        gc.fillText(currentDialogue, 70, 90);
        
        if (playerTurn && !gameOver) {
            gc.fillText("Press SPACE to continue", 70, 120);
        }
        
        if (gameOver) {
            gc.setFill(Color.RED);
            gc.setFont(Font.font("Monospace", FontWeight.BOLD, 24));
            gc.fillText("GAME OVER", WINDOW_WIDTH/2 - 60, WINDOW_HEIGHT/2);
            gc.setFill(Color.WHITE);
            gc.setFont(Font.font("Monospace", 14));
            gc.fillText("Press R to restart", WINDOW_WIDTH/2 - 60, WINDOW_HEIGHT/2 + 30);
        }
    }
    
    // Bone class for attacks
    class Bone {
        double x, y, velX, velY, width, height;
        
        Bone(double x, double y, double velX, double velY, double width, double height) {
            this.x = x;
            this.y = y;
            this.velX = velX;
            this.velY = velY;
            this.width = width;
            this.height = height;
        }
        
        void update() {
            x += velX;
            y += velY;
        }
        
        void draw(GraphicsContext gc) {
            gc.setFill(Color.WHITE);
            gc.fillRect(x, y, width, height);
        }
        
        boolean collidesWith(double px, double py, double pSize) {
            return px + pSize/2 > x && px - pSize/2 < x + width &&
                   py + pSize/2 > y && py - pSize/2 < y + height;
        }
        
        boolean isOffScreen() {
            return x < -100 || x > WINDOW_WIDTH + 100 || y < -100 || y > WINDOW_HEIGHT + 100;
        }
    }
    
    // Gaster Blaster class
    class GasterBlaster {
        double x, y, angle;
        double timer = 0;
        boolean firing = false;
        
        GasterBlaster(double x, double y, double angle) {
            this.x = x;
            this.y = y;
            this.angle = angle;
        }
        
        void update() {
            timer += 0.016;
            if (timer > 1.0) {
                firing = true;
            }
        }
        
        void draw(GraphicsContext gc) {
            gc.setFill(Color.WHITE);
            // Simple blaster representation
            gc.fillOval(x - 15, y - 15, 30, 30);
            
            if (firing && timer < 2.0) {
                // Draw beam going horizontally to the right
                gc.setFill(Color.CYAN);
                double beamLength = 400;
                gc.fillRect(x, y - 5, beamLength, 10);
            }
        }
        
        boolean beamCollidesWith(double px, double py, double pSize) {
            if (!firing || timer >= 2.0) return false;
            
            // Horizontal beam collision going to the right
            return py + pSize/2 > y - 5 && py - pSize/2 < y + 5 &&
                   px + pSize/2 > x && px - pSize/2 < x + 400;
        }
        
        boolean isFinished() {
            return timer > 3.0;
        }
    }
    
    public static void main(String[] args) {
        launch(args);
    }
}