package edu.uco.faaezcmakhdoomj.faaezcmakhdoomj;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button newGame = (Button) findViewById(R.id.newGame);
        Button score = (Button) findViewById(R.id.score);
        Button option = (Button) findViewById(R.id.option);

        newGame.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent intent = new Intent(getApplicationContext(), GameActivity.class);
                startActivity(intent);
            }
        });

        score.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent intent = new Intent(getApplicationContext(), ScoreActivity.class);
                startActivity(intent);
            }
        });

        option.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent intent = new Intent(getApplicationContext(), OptionActivity.class);
                startActivity(intent);
            }
        });
    }
}
