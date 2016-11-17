package edu.uco.faaezcmakhdoomj.faaezcmakhdoomj;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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

import edu.uco.faaezcmakhdoomj.faaezcmakhdoomj.EndGameDialogFragment.EndGameDialogListener;

public class GameActivity extends Activity implements EndGameDialogListener, NameDialogFragment.NameDialogListener{

    ImageButton topLeft, up, topRight, bottomLeft, down, bottomRight;
    TextView scoreField;

    BubbleView bubbleView;
    RelativeLayout relativeLayout;

    private LinkedList<Point> snake;
    private int direction = Direction.NO_DIRECTION;
    private int score = 0;
    private String name = null;

    DatabaseHelper myDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        myDb = new DatabaseHelper(this);

        relativeLayout = (RelativeLayout) findViewById(R.id.gameView);
        openNameDialog();
        bubbleView = new BubbleView(getApplicationContext());

        relativeLayout.addView(bubbleView);

        topLeft = (ImageButton) findViewById(R.id.topLeft);
        up = (ImageButton) findViewById(R.id.topMiddle);
        //topRight = (ImageButton) findViewById(R.id.topRight);
        //bottomLeft = (ImageButton) findViewById(R.id.bottomLeft);
        down = (ImageButton) findViewById(R.id.bottomMiddle);
        bottomRight = (ImageButton) findViewById(R.id.bottomRight);

        scoreField = (TextView) findViewById(R.id.score);

        topLeft.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                if (direction != Direction.EAST)
                    direction = Direction.WEST;
            }
        });

      /*  topRight.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                if (direction != Direction.WEST)
                    direction = Direction.EAST;
            }
        }); */

        up.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                if (direction != Direction.SOUTH)
                    direction = Direction.NORTH;
            }
        });

       /* bottomLeft.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                if (direction != Direction.EAST)
                    direction = Direction.WEST;
            }
        }); */

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

        private final DisplayMetrics mDisplay;
        private final int mDisplayWidth, mDisplayHeight;
        private int canvasWidth, canvasHeight;
        private final SurfaceHolder mSurfaceHolder;
        private final Paint mPainter = new Paint();
        private final Paint mBackgroungPainter = new Paint();
        private Thread mDrawingThread;

        private int moveStep = 2;

        private final int BOX_HEIGHT = 50;
        private final int BOX_WIDTH = 50;
        private final int GRID_WIDTH = 25;
        private final int GRID_HEIGHT = 25;
        private int mX, mY;

        private Point point = new Point();
        private boolean newPoint = true;

        Bitmap cherry = BitmapFactory.decodeResource(getResources(), R.mipmap.cherry);
        Bitmap headWest = BitmapFactory.decodeResource(getResources(), R.mipmap.headwest);
        Bitmap headEast = BitmapFactory.decodeResource(getResources(), R.mipmap.headeast);
        Bitmap headNorth = BitmapFactory.decodeResource(getResources(), R.mipmap.headnorth);
        Bitmap headSouth = BitmapFactory.decodeResource(getResources(), R.mipmap.headsouth);
        Bitmap tailWest = BitmapFactory.decodeResource(getResources(), R.mipmap.tailwest);
        Bitmap tailEast = BitmapFactory.decodeResource(getResources(), R.mipmap.taileast);
        Bitmap tailNorth = BitmapFactory.decodeResource(getResources(), R.mipmap.tailnorth);
        Bitmap tailSouth = BitmapFactory.decodeResource(getResources(), R.mipmap.tailsouth);
        Bitmap bodyHorizontal = BitmapFactory.decodeResource(getResources(), R.mipmap.bodyhorizontal);
        Bitmap bodyVertical = BitmapFactory.decodeResource(getResources(), R.mipmap.bodyvertical);

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
            GenerateDefaultSnake();

            mSurfaceHolder = getHolder();
            mSurfaceHolder.addCallback(this);
        }

        private void drawBubble(Canvas canvas) {
            //canvas.drawColor(Color.DKGRAY);
            //mPainter.setStyle(Paint.Style.FILL);
            //Bitmap bmp = BitmapFactory.decodeResource(getResources(), R.drawable.grass_tiled);
            //canvas.drawBitmap(bmp,0,0,null);
            //mPainter.setShader(new BitmapShader(bmp, Shader.TileMode.REPEAT, Shader.TileMode.REPEAT));
           //----- canvas.drawColor(ContextCompat.getColor(getContext(), R.color.back_green));
            //canvas.drawPaint(mPainter);

            mBackgroungPainter.setShader(new LinearGradient(0,0,0,getHeight(), ContextCompat.getColor(getContext(), R.color.lightgreen)
                    ,ContextCompat.getColor(getContext(), R.color.Darkgreen), Shader.TileMode.CLAMP));

            canvas.drawPaint(mBackgroungPainter);

            mPainter.setColor(Color.YELLOW);

            int snakecounter = 1;

            if(direction == Direction.EAST){
                canvas.drawBitmap(Bitmap.createScaledBitmap(headEast, 50, 50, false),snake.getFirst().x,snake.getFirst().y,null);
            } else if (direction == Direction.WEST){
                canvas.drawBitmap(Bitmap.createScaledBitmap(headWest, 50, 50, false),snake.getFirst().x,snake.getFirst().y,null);
            } else if (direction == Direction.SOUTH){
                canvas.drawBitmap(Bitmap.createScaledBitmap(headSouth, 50, 50, false),snake.getFirst().x,snake.getFirst().y,null);
            } else if (direction == Direction.NORTH){
                canvas.drawBitmap(Bitmap.createScaledBitmap(headNorth, 50, 50, false),snake.getFirst().x,snake.getFirst().y,null);
            }

            for (Point p : snake)
            {
                if(direction == Direction.EAST || direction == Direction.WEST){
                    if(snakecounter != 1 && snakecounter != snake.size()) {
                        //canvas.drawRect(p.x, p.y, p.x + BOX_WIDTH, p.y + BOX_HEIGHT, mPainter);
                        canvas.drawBitmap(Bitmap.createScaledBitmap(bodyHorizontal, 50, 50, false), p.x, p.y, null);
                    }
                } else {
                    if(snakecounter != 1 && snakecounter != snake.size()) {
                        canvas.drawBitmap(Bitmap.createScaledBitmap(bodyVertical, 50, 50, false), p.x, p.y, null);
                    }
                }
                snakecounter++;
            }

            if(direction == Direction.EAST){
                canvas.drawBitmap(Bitmap.createScaledBitmap(tailEast, 50, 50, false),snake.getLast().x,snake.getLast().y,null);
            } else if (direction == Direction.WEST){
                canvas.drawBitmap(Bitmap.createScaledBitmap(tailWest, 50, 50, false),snake.getLast().x,snake.getLast().y,null);
            } else if (direction == Direction.SOUTH){
                canvas.drawBitmap(Bitmap.createScaledBitmap(tailSouth, 50, 50, false),snake.getLast().x,snake.getLast().y,null);
            } else if (direction == Direction.NORTH){
                canvas.drawBitmap(Bitmap.createScaledBitmap(tailNorth, 50, 50, false),snake.getLast().x,snake.getLast().y,null);
            }

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

        private void move() {

            if(direction != Direction.NO_DIRECTION){
                if (snake.getFirst().y >= canvasHeight) {     //Down
                    snake.getFirst().y = 0;
                } else if (snake.getFirst().y <= 0) {     //Up
                    snake.getFirst().y = canvasHeight;
                }

                if (snake.getFirst().x >= canvasWidth) {  //Right
                    snake.getFirst().x = 0;
                } else if (snake.getFirst().x <= 0) {     //Left
                    snake.getFirst().x = canvasWidth;
                }

                Point head = snake.peekFirst();
                Point newPoint = head;
                switch (direction) { //speed == 10
                    case Direction.NORTH:
                        newPoint = new Point(head.x, head.y - 10);
                        break;
                    case Direction.SOUTH:
                        newPoint = new Point(head.x, head.y + 10);
                        break;
                    case Direction.WEST:
                        newPoint = new Point(head.x - 10, head.y);
                        break;
                    case Direction.EAST:
                        newPoint = new Point(head.x + 10, head.y);
                        break;
                }
                snake.remove(snake.peekLast());
                snake.push(newPoint);
            }
        }

        private void detectCollision(){
            Rect snakeRect = new Rect(snake.getFirst().x, snake.getFirst().y,
                    snake.getFirst().x+50, snake.getFirst().y +50);

            Rect pointRect = new Rect(point.x,point.y,point.x+50,point.y+50);

            if(snakeRect.intersect(pointRect)){
                newPoint = true;
                score += 1;
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
        }

        private void gameOver(){
            mDrawingThread.interrupt();
            openDialog(score);
        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width,
                                   int height) {
            canvasWidth = width;
            canvasHeight = height;
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
                            detectCollision();
                            UpdateScore(score);
                            move();
                            drawBubble(canvas);
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
                counter += 10; //Always equal to speed
            }
            direction = Direction.WEST;
        }

        private Point GenerateRandomPoint(){
            Random rand = new Random();
            int randomHeight = rand.nextInt((canvasHeight - 0) + 1) + 0;
            int randomWidth = rand.nextInt((canvasWidth - 0) + 1) + 0;
            Point randomPoint = new Point(randomWidth,randomHeight);
            return randomPoint;
        }
    }
}