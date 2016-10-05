package com.ashleyfigueira.flappybird;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;

import java.util.Random;

public class FlappyBird extends ApplicationAdapter {

	SpriteBatch batch;
	Texture background;
	Texture[] birds;
	Texture topTube;
	Texture bottomTube;
	Texture gameOver;
	Circle birdCircle;
	Rectangle[] topTubeRectangles;
	Rectangle[] bottomTubeRectangles;
	//ShapeRenderer shapeRenderer;
	Random randGenerantor;

	int gameState = 0;
	int flapState = 0;
	int score = 0;
	int scoringTube = 0;
	BitmapFont font;

	float birdY = 0;
	float velocity = 0;
	float gravity = 1.5f;
	float gap = 400;

	float maxTubeOffset = 0;
	float tubeVelocity = 4;

	int numberOfTubes = 4;
	float[] tubeX = new float[numberOfTubes];
	float[] tubeOffset = new float[numberOfTubes];
	float distanceBetweenTubes;

	@Override
	public void create ()
	{
		batch = new SpriteBatch();
		background = new Texture("bg.png");
		birds = new Texture[2];
		birds[0] = new Texture("bird.png");
		birds[1] = new Texture("bird2.png");
		topTube = new Texture("toptube.png");
		bottomTube = new Texture("bottomtube.png");
		gameOver = new Texture("gameover.png");


		randGenerantor = new Random();
		font = new BitmapFont();
		font.setColor(Color.WHITE);
		font.getData().setScale(10);
		//shapeRenderer = new ShapeRenderer();
		birdCircle = new Circle();
		topTubeRectangles = new Rectangle[numberOfTubes];
		bottomTubeRectangles = new Rectangle[numberOfTubes];

		maxTubeOffset = Gdx.graphics.getHeight()/2 - gap/2 - 100;
		distanceBetweenTubes = Gdx.graphics.getWidth() * 3/4;

		startGame();


	}

	public void startGame()
	{
		//Middle of the screen variable (Y axis)
		birdY = Gdx.graphics.getHeight()/2 - birds[flapState].getHeight()/2;

		//Set tubes position
		for (int i = 0; i < numberOfTubes; i++)
		{
			tubeOffset[i] = (randGenerantor.nextFloat() - 0.5f) * (Gdx.graphics.getHeight() - gap - 200);
			tubeX[i] = Gdx.graphics.getWidth() / 2 - topTube.getWidth() / 2 + Gdx.graphics.getWidth() + i * distanceBetweenTubes;

			//Tub Hitmaps initiators
			topTubeRectangles[i] = new Rectangle();
			bottomTubeRectangles[i] = new Rectangle();
		}
	}

	@Override
	public void render ()
	{
		//Draw background on screen
		batch.begin();
		batch.draw(background, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

		//Game started
		if (gameState == 1)
		{
			if (tubeX[scoringTube] < Gdx.graphics.getWidth()/2)
			{
				score++;

				if (scoringTube < numberOfTubes - 1) {
					scoringTube++;
				} else {
					scoringTube = 0;
				}
			}

			//Tube logic (4 tubes repeating itself - X shift)
			for (int i = 0; i < numberOfTubes; i++)
			{
				if (tubeX[i] < - topTube.getWidth())
				{
					tubeX[i] += numberOfTubes * distanceBetweenTubes;
					tubeOffset[i] = (randGenerantor.nextFloat() - 0.5f) * (Gdx.graphics.getHeight() - gap - 200);
				}
				else {
					tubeX[i] = tubeX[i] - tubeVelocity;
				}

				//Tube displays
				batch.draw(topTube, tubeX[i], Gdx.graphics.getHeight() / 2 + gap / 2 + tubeOffset[i]);
				batch.draw(bottomTube, tubeX[i], Gdx.graphics.getHeight() / 2 - gap / 2 - bottomTube.getHeight() + tubeOffset[i]);

				//Hit boxes for tubes
				topTubeRectangles[i] = new Rectangle(tubeX[i], Gdx.graphics.getHeight() / 2 + gap / 2 + tubeOffset[i], topTube.getWidth(), topTube.getHeight());
				bottomTubeRectangles[i] = new Rectangle(tubeX[i], Gdx.graphics.getHeight() / 2 - gap / 2 - bottomTube.getHeight() + tubeOffset[i], bottomTube.getWidth(), bottomTube.getHeight());
			}

			//Gravity Logic
			if (Gdx.input.justTouched()) {
				velocity = -20;
			}


			//If bird is on screen set gravity if not gameover (no more gravity needed)
			if (birdY > 0) {
				velocity = velocity + gravity;
				birdY -= velocity;
			} else {
				gameState = 2;
			}

		}
		//Game hasnt started
		else if (gameState == 0)
		{
			//Start Game
			if (Gdx.input.justTouched()) {
				gameState = 1;
			}
		}
		//Game stopped (Game over)
		else if (gameState == 2)
		{
			batch.draw(gameOver, Gdx.graphics.getWidth()/2 - gameOver.getWidth()/2, Gdx.graphics.getHeight()/2 - gameOver.getHeight()/2);
			if (Gdx.input.justTouched()) {
				gameState = 1;
				startGame();
				score = 0;
				scoringTube = 0;
				velocity = 0;
			}
		}


		//Flap Wings (change image)
		if (flapState == 0) {
			flapState = 1;
		} else {
			flapState = 0;
		}

		//Draw birds
		batch.draw(birds[flapState], Gdx.graphics.getWidth() / 2 - birds[flapState].getWidth() / 2, birdY);

		//draw font
		font.draw(batch, String.valueOf(score), 100, 200);


		//Hit circle on bird
		birdCircle.set(Gdx.graphics.getWidth() / 2, birdY + birds[flapState].getHeight() / 2, birds[flapState].getWidth() / 2);
		//shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
		//shapeRenderer.setColor(Color.RED);
		//shapeRenderer.circle(birdCircle.x, birdCircle.y, birdCircle.radius);

		for (int i = 0 ; i<numberOfTubes; i++)
		{
			//shapeRenderer.rect(tubeX[i], Gdx.graphics.getHeight() / 2 + gap / 2 + tubeOffset[i], topTube.getWidth(), topTube.getHeight());
			//shapeRenderer.rect(tubeX[i], Gdx.graphics.getHeight() / 2 - gap / 2 - bottomTube.getHeight() + tubeOffset[i], bottomTube.getWidth(), bottomTube.getHeight());

			//Collision
			if (Intersector.overlaps(birdCircle,topTubeRectangles[i]) || Intersector.overlaps(birdCircle,bottomTubeRectangles[i]) ){
				gameState = 2;
			}
		}
		
		//shapeRenderer.end();
		batch.end();

	}
}
