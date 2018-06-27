package com.eemf.sirgoingfar.journalapp.activities;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;

import com.eemf.sirgoingfar.journalapp.R;

public class CatalogActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalog);

    }

    /*
     * on @value KEYCODE_BACK is pressed, @link onKeyDown(keyCode, event) is called
     * @param keyCode is the key code for the key pressed
     * @param event is the key event
     * @return true or @return the event*/

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event){
        switch(keyCode){
            case KeyEvent.KEYCODE_BACK:
                Intent minimize = new Intent(Intent.ACTION_MAIN);
                minimize.addCategory(Intent.CATEGORY_HOME);
                minimize.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(minimize);
                return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}

