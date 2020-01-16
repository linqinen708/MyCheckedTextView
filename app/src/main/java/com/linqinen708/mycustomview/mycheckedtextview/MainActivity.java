package com.linqinen708.mycustomview.mycheckedtextview;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private MyCheckedTextView mtv001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mtv001 = findViewById(R.id.mtv_001);

    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_enable:
                mtv001.setEnabled(true);
                break;
            case R.id.tv_unable:
                mtv001.setEnabled(false);
                break;
            case R.id.mtv_001:
                mtv001.toggle();
                break;
            default:
                break;
        }
    }
}
