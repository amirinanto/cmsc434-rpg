package cmsc434.mamirina.mydoodleprojecttest;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void clearCanvas(View v) {
        ((DoodleView)findViewById(R.id.doodle)).clearCanvas();
    }
}
