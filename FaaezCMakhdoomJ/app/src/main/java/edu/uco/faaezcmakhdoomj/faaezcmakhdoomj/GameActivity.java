package edu.uco.faaezcmakhdoomj.faaezcmakhdoomj;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.Shader;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.LinkedList;
import java.util.Random;

import edu.uco.faaezcmakhdoomj.faaezcmakhdoomj.PauseDialogFragment.PauseDialogListener;
import edu.uco.faaezcmakhdoomj.faaezcmakhdoomj.EndGameDialogFragment.EndGameDialogListener;

public class GameActivity extends Activity implements PauseDialogListener, EndGameDialogListener, NameDialogFragment.NameDialogListener {

    DatabaseHelper myDb;
    ImageButton topLeft, up, topRight, bottomLeft, down, bottomRight, pause;
    TextView scoreField;
    BubbleView bubbleView;
    RelativeLayout relativeLayout;

    private LinkedList<Point> snake;
    private LinkedList<Rect> wall;
    private int direction = Direction.NO_DIRECTION;
    private int score = 0;
    private String name = null;
    public int speed = 10;
    public boolean autoSpeed = false, walls = false, pauseGame = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        myDb = new DatabaseHelper(this);
        relativeLayout = (RelativeLayout) findViewById(R.id.gameView);
        openNameDialog();                                               //Ask for name before anything else
        bubbleView = new BubbleView(getApplicationContext());
        relativeLayout.addView(bubbleView);

        Cursor res = myDb.getConfigData();                              //Set all the configurations

        while(res.moveToNext()){
            autoSpeed = res.getString(1).equals("1") ? true : false;
            if(autoSpeed == false){
                speed = Integer.parseInt(res.getString(0));
            }
            walls = res.getString(2).equals("1") ? true : false;
        }

        topLeft = (ImageButton) findViewById(R.id.topLeft);
        up = (ImageButton) findViewById(R.id.topMiddle);
        down = (ImageButton) findViewById(R.id.bottomMiddle);
        bottomRight = (ImageButton) findViewById(R.id.bottomRight);
        pause = (ImageButton) findViewById(R.id.btnpause);

        scoreField = (TextView) findViewById(R.id.score);

        topLeft.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                if (direction != Direction.EAST)
                    direction = Direction.WEST;
            }
        });


        up.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                if (direction != Direction.SOUTH)
                    direction = Direction.NORTH;
            }
        });


        down.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                if (direction != Direction.NORTH)
                    direction = Direction.SOUTH;
            }
        });

        bottomRight.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                if (direction != Direction.WEST)
                    direction = Direction.EAST;
            }
        });

        pause.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                openPauseDialog();
            if (pauseGame){
                pauseGame = false;
            }
            else{
                pauseGame = true;
            }
            }
        });
    }

    public void openPauseDialog() {
        PauseDialogFragment d = new PauseDialogFragment();
        Bundle bundle = new Bundle();

        d.setArguments(bundle);
        d.show(getFragmentManager(), "GamePaused");
    }
    @Override
    public void onPauseDialogPositiveClick() {
        relativeLayout.removeView(bubbleView);
        bubbleView = new BubbleView(getApplicationContext());
        relativeLayout.addView(bubbleView);
        pauseGame = false;
    }

    @Override
    public void onPauseDialogNegativeClick() {
        pauseGame = false;
    }

    public void openNameDialog(){
        NameDialogFragment d = new NameDialogFragment();
        d.show(getFragmentManager(), "Name");
    }

    @Override
    public void onNameDialogPositiveClick(String _name){
        this.name = _name;
        if(name.equals("")){
            Toast.makeText(this,"Please enter a name",Toast.LENGTH_SHORT).show();
            openNameDialog();
        } else {
            Toast.makeText(this,name,Toast.LENGTH_SHORT).show();
        }
    }

    public void openDialog(int score){
        EndGameDialogFragment d = new EndGameDialogFragment();

        Bundle bundle = new Bundle();
        bundle.putInt("index", score);

        d.setArguments(bundle);
        d.show(getFragmentManager(), "Game Over!");
    }

    @Override
    public void onEndGameDialogPositiveClick() {
        AddData();
        relativeLayout.removeView(bubbleView);
        bubbleView = new BubbleView(getApplicationContext());
        relativeLayout.addView(bubbleView);
    }

    @Override
    public void onEndGameDialogNegativeClick(){
        AddData();
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        finish();
        startActivity(intent);
    }

    private void UpdateScore(int score){

        final int _score = score;

        this.runOnUiThread(
                new Runnable() {
                    public void run() {
                        scoreField.setText("Score : "+ _score);
                    }
                }
        );
    }

    private  void AddData() {
        boolean isInserted = myDb.insertData(name, ""+score);
        if(isInserted == true)
            Log.d("DB","insterted");
        else
            Log.d("DB","not insterted");
    }



    private class BubbleView extends SurfaceView implements
            SurfaceHolder.Callback {

/*========================= Variable Declaration ========================== */

        private final DisplayMetrics mDisplay;
        private final int mDisplayWidth, mDisplayHeight;
        private int canvasWidth, canvasHeight;
        private final SurfaceHolder mSurfaceHolder;
        private final Paint mPainter = new Paint();
        private final Paint mBackgroungPainter = new Paint();
        private Thread mDrawingThread;

        private LinkedList<Rect> wall = new LinkedList<Rect>();

        private int moveStep = 2;

        private final int BOX_HEIGHT = 50;
        private final int BOX_WIDTH = 50;
        private final int GRID_WIDTH = 25;
        private final int GRID_HEIGHT = 25;
        private int mX, mY;

        private Point point = new Point();
        private Point bonusPoint = new Point();
        private boolean newPoint = true;
        private boolean newBonusPoint = true;

        private int speedCounter = 1;
        private int bonusCounter = 0;

        private Bitmap cherry = BitmapFactory.decodeResource(getResources(), R.mipmap.cherry);
        private Bitmap diamond = BitmapFactory.decodeResource(getResources(), R.mipmap.diamond);
        private Bitmap headWest = BitmapFactory.decodeResource(getResources(), R.mipmap.headwest);
        private Bitmap headEast = BitmapFactory.decodeResource(getResources(), R.mipmap.headeast);
        private Bitmap headNorth = BitmapFactory.decodeResource(getResources(), R.mipmap.headnorth);
        private Bitmap headSouth = BitmapFactory.decodeResource(getResources(), R.mipmap.headsouth);
        private Bitmap tailWest = BitmapFactory.decodeResource(getResources(), R.mipmap.tailwest);
        private Bitmap tailEast = BitmapFactory.decodeResource(getResources(), R.mipmap.taileast);
        private Bitmap tailNorth = BitmapFactory.decodeResource(getResources(), R.mipmap.tailnorth);
        private Bitmap tailSouth = BitmapFactory.decodeResource(getResources(), R.mipmap.tailsouth);
        private Bitmap bodyHorizontal = BitmapFactory.decodeResource(getResources(), R.mipmap.bodyhorizontal);
        private Bitmap bodyVertical = BitmapFactory.decodeResource(getResources(), R.mipmap.bodyvertical);

/*==================================================================================================*/

/*======================================== Default constructor =====================================*/

        public BubbleView(Context context) {
            super(context);

            mDisplay = new DisplayMetrics();
            GameActivity.this.getWindowManager().getDefaultDisplay()
                    .getMetrics(mDisplay);
            mDisplayWidth = mDisplay.widthPixels;
            mDisplayHeight = mDisplay.heightPixels;

            // the image at the center of the screen at the beginning
            mX = mDisplayWidth / 2;
            mY = mDisplayHeight / 2;

            snake = new LinkedList<Point>();
            wall = new LinkedList<Rect>();
            GenerateDefaultSnake();

            mSurfaceHolder = getHolder();
            mSurfaceHolder.addCallback(this);
        }

/*==================================================================================================*/

/*=============================================Draw Method==========================================*/

        private void drawBubble(Canvas canvas) {
            //canvas.drawColor(Color.DKGRAY);
            mBackgroungPainter.setShader(new LinearGradient(0,0,0,getHeight(), ContextCompat.getColor(getContext(), R.color.lightgreen)
                    ,ContextCompat.getColor(getContext(), R.color.Darkgreen), Shader.TileMode.CLAMP));

            canvas.drawPaint(mBackgroungPainter);
            drawSnake(canvas);
            if(bonusCounter == 5){
                drawBonus(canvas);
            } else {
                drawPoint(canvas);
            }
            if(walls == true){
                drawWalls(canvas);
            }
        }

//-------------------------------- draw snake ----------------------------------------------------//

         private void drawSnake(Canvas canvas){
             mPainter.setColor(Color.YELLOW);
             for(Point p: snake){
                 canvas.drawRect(p.x, p.y, p.x + BOX_WIDTH, p.y + BOX_HEIGHT, mPainter);
             }
         }

//---------------------------------- draw point --------------------------------------------------//

        private void drawPoint(Canvas canvas){
            if(newPoint){
                point = new Point();
                point = GenerateRandomPoint();
                Log.d("Dim",point.x + " | " + canvasWidth + " == " + point.y + " | " + canvasHeight);
                newPoint = false;
            }
            mPainter.setColor(Color.RED);
            //canvas.drawRect(point.x,point.y,point.x+50,point.y+50,mPainter);
            canvas.drawBitmap(Bitmap.createScaledBitmap(cherry, 50, 50, false),point.x,point.y,null);
        }

//-------------------------------------- Draw Walls ----------------------------------------------//

        private void drawWalls(Canvas canvas){
            for(Rect brick : wall){
               canvas.drawRect(brick,mPainter);
            }
        }

//--------------------------------------- Draw bonus ----------------------------------------------//

        private void drawBonus(Canvas canvas){
            if(newPoint){
                point = new Point();
                point = GenerateRandomPoint();
                Log.d("Dim",point.x + " | " + canvasWidth + " == " + point.y + " | " + canvasHeight);
                newPoint = false;
            }
            mPainter.setColor(Color.BLUE);
            //canvas.drawRect(point.x,point.y,point.x+70,point.y+70,mPainter);
            canvas.drawBitmap(Bitmap.createScaledBitmap(diamond, 50, 50, false),point.x,point.y,null);
        }

/*==================================================================================================*/

/*==============================================Move Method=========================================*/
        private void move() {

            if(!pauseGame){
                if(direction != Direction.NO_DIRECTION) {
                    if (snake.getFirst().y >= canvasHeight) {                                       //Down
                        snake.getFirst().y = 0;
                    } else if (snake.getFirst().y <= 0) {                                           //Up
                        snake.getFirst().y = canvasHeight;
                    }

                    if (snake.getFirst().x >= canvasWidth) {                                        //Right
                        snake.getFirst().x = 0;
                    } else if (snake.getFirst().x <= 0) {                                           //Left
                        snake.getFirst().x = canvasWidth;
                    }

                    Point head = snake.peekFirst();
                    Point newPoint = head;
                    switch (direction) {                                                            //speed == 10
                        case Direction.NORTH:
                            newPoint = new Point(head.x, head.y - speed);
                            break;
                        case Direction.SOUTH:
                            newPoint = new Point(head.x, head.y + speed);
                            break;
                        case Direction.WEST:
                            newPoint = new Point(head.x - speed, head.y);
                            break;
                        case Direction.EAST:
                            newPoint = new Point(head.x + speed, head.y);
                            break;
                    }
                    snake.remove(snake.peekLast());
                    snake.push(newPoint);
                }
            }
        }

/*==================================================================================================*/

/*==========================================Collision Detection=====================================*/

        private void detectCollision(){
            Rect snakeRect = new Rect(snake.getFirst().x, snake.getFirst().y,
                    snake.getFirst().x+50, snake.getFirst().y +50);

            Rect pointRect;
            if(bonusCounter == 5){
                pointRect = new Rect(point.x , point.y, point.x + 70, point.y + 70);
            } else {
                pointRect = new Rect(point.x,point.y,point.x+50,point.y+50);
            }

            if(snakeRect.intersect(pointRect)){

                newPoint = true;

                if(bonusCounter == 5){
                    score += 5;
                    bonusCounter = 0;
                } else {
                    score += 1;
                    bonusCounter += 1;
                }

                if(autoSpeed == true) {
                    speedCounter += 1;;
                    if (speedCounter == 5) {
                        speedCounter = 0;
                        speed += 5;
                    }
                }

                Point last = snake.getLast();
                for(int i = 0; i < 5; i++) {
                    snake.add(new Point(last.x, last.y));
                }
            }

            int counter = 0;
            for(Point p : snake){
                if(counter != 0){
                    Rect bodyRect = new Rect(p.x, p.y, p.x+50, p.y +50);
                    if(snakeRect.left == bodyRect.left &&
                            snakeRect.top == bodyRect.top &&
                            snakeRect.right == bodyRect.right &&
                            snakeRect.bottom == bodyRect.bottom){
                        gameOver();
                    }
                }
                counter++;
            }

            if(walls == true) {
                if(snake.getFirst().y > (canvasHeight - 50) ||
                        snake.getFirst().y < 50 ||
                        snake.getFirst().x > (canvasWidth - 50)||
                        snake.getFirst().x < 50){
                    gameOver();
                }
            }
        }

/*==================================================================================================*/
        private void gameOver(){
            mDrawingThread.interrupt();
            openDialog(score);
        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width,
                                   int height) {
            canvasWidth = width;
            canvasHeight = height;
            GenerateWalls();
        }

        @Override
        public void surfaceCreated(SurfaceHolder holder) {

            mDrawingThread = new Thread(new Runnable() {
                public void run() {
                long previousFrameTime = System.currentTimeMillis();
                Canvas canvas = null;
                while (!Thread.currentThread().isInterrupted()) {

                    canvas = mSurfaceHolder.lockCanvas();
                    if (null != canvas) {
                        long currentTime = System.currentTimeMillis();
                        double elapsedTime = currentTime - previousFrameTime;
                        //Log.d(TAG, elapsedTime+"");
                        moveStep = (int) (elapsedTime / 5) + 5;
                        move();
                        drawBubble(canvas);
                        detectCollision();
                        UpdateScore(score);
                        previousFrameTime = currentTime;
                        mSurfaceHolder.unlockCanvasAndPost(canvas);
                    }
                }
                }
            });
            mDrawingThread.start();
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
            if (null != mDrawingThread)
                mDrawingThread.interrupt();
        }

        public void GenerateDefaultSnake()
        {
            snake.clear();
            score = 0;
            int counter = 0;
            for(int i = 0; i < 50; i++){
                snake.add(new Point(mX+counter,mY));
                counter += 50; //Always equal to speed
            }
            direction = Direction.WEST;
        }

        private Point GenerateRandomPoint(){
            Random rand = new Random();
            int randomHeight = rand.nextInt(((canvasHeight - 60) - 0) + 1) + 0;
            int randomWidth = rand.nextInt(((canvasWidth - 60) - 0) + 1) + 0;
            Point randomPoint = new Point(randomWidth,randomHeight);
            return randomPoint;
        }

        private void GenerateWalls(){
            for(int i  = 0; i <= canvasHeight; i += 50) {
                wall.add(new Rect(0,i,50,50));
                wall.add(new Rect(canvasWidth, i, canvasWidth-50, 50));
            }

            for(int i  = 0; i <= canvasWidth; i += 50) {
                wall.add(new Rect(i, 0, 50, 50));
                wall.add(new Rect(i, canvasHeight, 50, canvasHeight-50));
            }
        }
    }
}