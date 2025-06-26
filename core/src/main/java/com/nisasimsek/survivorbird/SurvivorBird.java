package com.nisasimsek.survivorbird;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;

import java.util.Random;

/**
 * {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms.
 */

public class SurvivorBird extends ApplicationAdapter {
    SpriteBatch batch;
    Texture background, bird, bee1, bee2, bee3;

    float birdX = 0, birdY = 0;
    int gameState = 0;
    float velocity = 0;
    float gravity = 0.35f;
    float jumpForce = -8.5f;
    float baseEnemyVelocity = 3.5f;
    float enemyVelocity = 4f;

    Random random;
    int score = 0;
    int highScore = 0;

    BitmapFont font, font2;

    Circle birdCircle;
    int numberOfEnemies = 4;
    float[] enemyX = new float[numberOfEnemies];
    float[] enemyOffSet = new float[numberOfEnemies];
    float[] enemyOffSet2 = new float[numberOfEnemies];
    float[] enemyOffSet3 = new float[numberOfEnemies];
    float distance = 0;

    boolean[] passedEnemy;

    Circle[] enemyCircles;
    Circle[] enemyCircles2;
    Circle[] enemyCircles3;

    float birdWidth, birdHeight, beeWidth, beeHeight;

    @Override
    public void create() {
        batch = new SpriteBatch();
        background = new Texture("background.png");
        bird = new Texture("bird.png");
        bee1 = new Texture("bee.png");
        bee2 = new Texture("bee.png");
        bee3 = new Texture("bee.png");

        distance = Gdx.graphics.getWidth() / 2f;
        random = new Random();

        birdX = Gdx.graphics.getWidth() / 2f - bird.getWidth() / 2f;
        birdY = Gdx.graphics.getHeight() / 3f;

        birdWidth = Gdx.graphics.getWidth() / 15f;
        birdHeight = Gdx.graphics.getHeight() / 12f;
        beeWidth = Gdx.graphics.getWidth() / 18f;
        beeHeight = Gdx.graphics.getHeight() / 15f;

        birdCircle = new Circle();
        enemyCircles = new Circle[numberOfEnemies];
        enemyCircles2 = new Circle[numberOfEnemies];
        enemyCircles3 = new Circle[numberOfEnemies];

        passedEnemy = new boolean[numberOfEnemies];

        font = new BitmapFont();
        font.setColor(Color.WHITE);
        font.getData().setScale(4);

        font2 = new BitmapFont();
        font2.setColor(Color.WHITE);
        font2.getData().setScale(6);

        for (int i = 0; i < numberOfEnemies; i++) {
            enemyOffSet[i] = getRandomOffset();
            enemyOffSet2[i] = getRandomOffset();
            enemyOffSet3[i] = getRandomOffset();
            enemyX[i] = Gdx.graphics.getWidth() + i * distance;

            enemyCircles[i] = new Circle();
            enemyCircles2[i] = new Circle();
            enemyCircles3[i] = new Circle();

            passedEnemy[i] = false;
        }
    }

    private float getRandomOffset() {
        return (random.nextFloat() - 0.5f) * (Gdx.graphics.getHeight() - 200);
    }

    @Override
    public void render() {
        batch.begin();
        batch.draw(background, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        if (gameState == 1) {

            enemyVelocity = baseEnemyVelocity + score * 0.03f;

            if (Gdx.input.justTouched()) {
                velocity = jumpForce;
            }

            for (int i = 0; i < numberOfEnemies; i++) {


                if (enemyX[i] + beeWidth < birdX && !passedEnemy[i]) {
                    score++;
                    passedEnemy[i] = true;
                }


                if (enemyX[i] < -beeWidth * 3) {
                    enemyX[i] += numberOfEnemies * distance;
                    enemyOffSet[i] = getRandomOffset();
                    enemyOffSet2[i] = getRandomOffset();
                    enemyOffSet3[i] = getRandomOffset();
                    passedEnemy[i] = false;
                } else {
                    enemyX[i] -= enemyVelocity;
                }

                float baseY = Gdx.graphics.getHeight() / 2f;

                batch.draw(bee1, enemyX[i], baseY + enemyOffSet[i], beeWidth, beeHeight);
                batch.draw(bee2, enemyX[i] + beeWidth + 10, baseY + enemyOffSet2[i], beeWidth, beeHeight);
                batch.draw(bee3, enemyX[i] + 2 * (beeWidth + 10), baseY + enemyOffSet3[i], beeWidth, beeHeight);

                enemyCircles[i].set(enemyX[i] + beeWidth / 2f, baseY + enemyOffSet[i] + beeHeight / 2f, beeWidth / 2f);
                enemyCircles2[i].set(enemyX[i] + beeWidth + 10 + beeWidth / 2f, baseY + enemyOffSet2[i] + beeHeight / 2f, beeWidth / 2f);
                enemyCircles3[i].set(enemyX[i] + 2 * (beeWidth + 10) + beeWidth / 2f, baseY + enemyOffSet3[i] + beeHeight / 2f, beeWidth / 2f);
            }

            if (birdY > 0) {
                velocity += gravity;
                birdY -= velocity;
            } else {
                gameState = 2;
            }

        } else if (gameState == 0) {
            if (Gdx.input.justTouched()) {
                gameState = 1;
            }
        } else if (gameState == 2) {
            if (score > highScore) {
                highScore = score;
            }

            font2.draw(batch, "Game Over! Tap To Play Again!", 100, Gdx.graphics.getHeight() / 2f);
            font2.draw(batch, "Score: " + score, 100, Gdx.graphics.getHeight() / 2f - 100);
            font2.draw(batch, "High Score: " + highScore, 100, Gdx.graphics.getHeight() / 2f - 180);

            if (Gdx.input.justTouched()) {
                gameState = 1;
                birdY = Gdx.graphics.getHeight() / 3f;
                velocity = 0;
                score = 0;

                for (int i = 0; i < numberOfEnemies; i++) {
                    enemyOffSet[i] = getRandomOffset();
                    enemyOffSet2[i] = getRandomOffset();
                    enemyOffSet3[i] = getRandomOffset();
                    enemyX[i] = Gdx.graphics.getWidth() + i * distance;

                    enemyCircles[i] = new Circle();
                    enemyCircles2[i] = new Circle();
                    enemyCircles3[i] = new Circle();
                    passedEnemy[i] = false;
                }
            }
        }

        batch.draw(bird, birdX, birdY, birdWidth, birdHeight);
        font.draw(batch, "Score: " + score, 100, 200);
        font.draw(batch, "High Score: " + highScore, 100, 260);
        batch.end();

        birdCircle.set(birdX + birdWidth / 2f, birdY + birdHeight / 2f, birdWidth / 2f);

        for (int i = 0; i < numberOfEnemies; i++) {
            if (Intersector.overlaps(birdCircle, enemyCircles[i]) ||
                Intersector.overlaps(birdCircle, enemyCircles2[i]) ||
                Intersector.overlaps(birdCircle, enemyCircles3[i])) {
                gameState = 2;
            }
        }
    }

    @Override
    public void dispose() {
        batch.dispose();
        background.dispose();
        bird.dispose();
        bee1.dispose();
        bee2.dispose();
        bee3.dispose();
        font.dispose();
        font2.dispose();
    }
}
