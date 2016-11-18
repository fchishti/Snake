package edu.uco.faaezcmakhdoomj.faaezcmakhdoomj;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.SeekBar;
import android.widget.Toast;

public class OptionActivity extends Activity {

    private int speed = 10;
    private boolean autoSpeed = false;
    private boolean walls = false;

    DatabaseHelper myDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_option);

        CheckBox autoSpeedCheck = (CheckBox) findViewById(R.id.autospeedcheck);
        CheckBox wallsCheck = (CheckBox) findViewById(R.id.wallscheck);
        SeekBar speedBar = (SeekBar) findViewById(R.id.speedseekbar);

        myDb = new DatabaseHelper(this);

        Cursor res = myDb.getConfigData();

        if(res.getCount() == 0) {
            Toast.makeText(getApplicationContext(),"Something went wrong",Toast.LENGTH_SHORT).show();
            return;
        }

        while (res.moveToNext()){
            speed = Integer.parseInt(res.getString(0));
            speedBar.setProgress(speed);

            if(res.getString(1).equals("1")){
                autoSpeed = true;
                autoSpeedCheck.setChecked(true);
            }

            if(res.getString(2).equals("1")) {
                walls = true;
                wallsCheck.setChecked(true);
            }
        }

        autoSpeedCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(((CheckBox) v).isChecked()){
                    autoSpeed = true;
                } else {
                    autoSpeed = false;
                }
                addData();
            }
        });

        wallsCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(((CheckBox) v).isChecked()){
                    walls = true;
                } else {
                    walls = false;
                }
                addData();
            }
        });

        speedBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                speed = progress;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                Toast.makeText(getApplicationContext(),"Speed set to : " + speed,Toast.LENGTH_SHORT).show();
                addData();
            }
        });
    }

    private void addData() {
        boolean isInserted = myDb.insertConfigData(speed,autoSpeed,walls);
        if(isInserted == false){
            Toast.makeText(getApplicationContext(),"Could not insert",Toast.LENGTH_SHORT).show();
        }
    }
}
