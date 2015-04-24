package com.jasonzue.cityselection;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.widget.Toast;


public class MainActivity extends ActionBarActivity {

    private CitySelectView selectView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        selectView = (CitySelectView) findViewById(R.id.cityview);
        selectView.setOnResultSelectListener(
                new CitySelectView.OnResultSelectListener() {
                    @Override
                    public void onResultSelected(boolean isFinish, String provience, String city, String district) {

                        String result = provience + city + district;
                        if (isFinish) {
                            Toast.makeText(MainActivity.this, "选择完成:结果 :" + result, Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(MainActivity.this, "选择未完成:结果 :" + result, Toast.LENGTH_SHORT).show();
                        }
                    }
                }

        );


    }
}
