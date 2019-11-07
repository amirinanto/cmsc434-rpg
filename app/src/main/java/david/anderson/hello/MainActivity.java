package david.anderson.hello;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button myTryButton = findViewById(R.id.btn_try);
        myTryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), ((Button)v).getText().toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void helloButtonOnClick(View view) {
        Toast.makeText(getApplicationContext(), "hello button get clicked", Toast.LENGTH_SHORT).show();
    }
}
