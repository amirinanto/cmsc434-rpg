package cmsc434.mamirina.mydoodleprojecttest;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

public class DoodleView extends View {

    private Paint _paint = new Paint();
    private Path _path = new Path();

    public DoodleView(Context context) {
        super(context);
        init(null,0);
    }

    public DoodleView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public DoodleView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs, defStyleAttr);
    }

    private void init(AttributeSet attrs, int i) {
        _paint.setColor(Color.CYAN);
        _paint.setStyle(Paint.Style.STROKE);
        _paint.setAntiAlias(true);


    }

    public void clearCanvas() {
        _path.reset();
        invalidate();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float   x = event.getX(),
                y = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                _path.moveTo(x,y);
                break;
            case MotionEvent.ACTION_MOVE:
                _path.lineTo(x,y);
                break;
            case MotionEvent.ACTION_UP:
                break;
        }
        invalidate();
        return true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawLine(0,0,getWidth(),getHeight(),_paint);
        canvas.drawPath(_path, _paint);
    }


}
