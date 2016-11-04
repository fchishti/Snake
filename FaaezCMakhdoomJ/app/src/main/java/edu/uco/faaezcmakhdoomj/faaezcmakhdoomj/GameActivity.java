package edu.uco.faaezcmakhdoomj.faaezcmakhdoomj;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.LinkedList;
import java.util.Random;

public class GameActivity extends Activity {

    ImageButton topLeft, up, topRight, bottomLeft, down, bottomRight;
    TextView scoreField;

    private LinkedList<Point> snake;
    private int direction = Direction.NO_DIRECTION;
    private int score = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        RelativeLayout relativeLayout = (RelativeLayout) findViewById(R.id.gameView);
        final BubbleView bubbleView = new BubbleView(getApplicationContext());

        relativeLayout.addView(bubbleView);

        topLeft = (ImageButton) findViewById(R.id.topLeft);
        up = (ImageButton) findViewById(R.id.topMiddle);
        topRight = (ImageButton) findViewById(R.id.topRight);
        bottomLeft = (ImageButton) findViewById(R.id.bottomLeft);
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

        topRight.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                if (direction != Direction.WEST)
                    direction = Direction.EAST;
            }
        });

        up.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                if (direction != Direction.SOUTH)
                    direction = Direction.NORTH;
            }
        });

        bottomLeft.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                if (direction != Direction.EAST)
                    direction = Direction.WEST;
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
    }

    private class BubbleView extends SurfaceView implements
            SurfaceHolder.Callback {

        private final DisplayMetrics mDisplay;
        private final int mDisplayWidth, mDisplayHeight;
        private int canvasWidth, canvasHeight;
        private final SurfaceHolder mSurfaceHolder;
        private final Paint mPainter = new Paint();
        private Thread mDrawingThread;

        private int moveStep = 2;

//      private int direction = 1; // +1 for +Y, -1 for -Y

        private final int BOX_HEIGHT = 50;
        private final int BOX_WIDTH = 50;
        private final int GRID_WIDTH = 25;
        private final int GRID_HEIGHT = 25;
        private int mX, mY;

        private Point point = new Point();
        private boolean newPoint = true;



        public BubbleView(Context context) {
            super(context);

            // general info about the display; size, density, font scaling, etc
            // the screen size, not canvas size
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
            canvas.drawColor(Color.DKGRAY);
            mPainter.setColor(Color.YELLOW);

            for (Point p : snake)
            {
                canvas.drawRect(p.x , p.y , p.x + BOX_WIDTH, p.y + BOX_HEIGHT,mPainter);
            }

            if(newPoint){
                point = new Point();
                point = GenerateRandomPoint();
                Log.d("Dim",point.x + " | " + canvasWidth + " == " + point.y + " | " + canvasHeight);
                newPoint = false;
            }
            mPainter.setColor(Color.RED);
            canvas.drawRect(point.x,point.y,point.x+50,point.y+50,mPainter);
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
            }
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
            for(int i =0; i < 50; i++){
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