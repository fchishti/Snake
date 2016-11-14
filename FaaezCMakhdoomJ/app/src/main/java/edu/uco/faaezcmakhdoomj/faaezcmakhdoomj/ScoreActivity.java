package edu.uco.faaezcmakhdoomj.faaezcmakhdoomj;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.TextView;

public class ScoreActivity extends Activity {

    TextView name,score,hcounter;
    DatabaseHelper myDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_score);


        int counter = 1;

        myDb = new DatabaseHelper(this);

        name = (TextView) findViewById(R.id.hName);
        score = (TextView) findViewById(R.id.hScore);
        hcounter = (TextView) findViewById(R.id.hCounter);

        Cursor res = myDb.getAllData();
        if(res.getCount() == 0) {
            name.setText("Nothing found");
            return;
        }

        StringBuffer nameBuffer = new StringBuffer();
        StringBuffer scoreBuffer = new StringBuffer();
        StringBuffer counterBuffer = new StringBuffer();

        while (res.moveToNext() && counter < 11) {
            counterBuffer.append(counter+".\n");
            nameBuffer.append(res.getString(1)+"\n");
            scoreBuffer.append(res.getString(2)+"\n");
            counter++;
        }
        name.setText(nameBuffer.toString());
        score.setText(scoreBuffer.toString());
        hcounter.setText(counterBuffer.toString());
    }
}
