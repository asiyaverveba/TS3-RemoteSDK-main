package com.enhancell.remotesample;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import javax.media.jai.PerspectiveTransform;

public class TestButtonView extends View {

    private final Context _context;
    private final RectF _backgroundArea;
    private final Paint _backgroundPaint;
    private final Paint _progressPaint;
    private final Paint _textPaint;
    private final Rect _textBounds;
    private String _buttonText;
    private String _buttonSubText;
    private int _progress;
    private PerspectiveTransform _progressValuesToPixels;
    private PerspectiveTransform _progressPixelsToValues;
    private final double[] _valuePoint;
    private final double[] _pixelPoint;
    private @Nullable OnClickListener _clickListener;

    public TestButtonView(@NonNull Context context, AttributeSet attrs) {
        super(context, attrs);

        _context = context;
        _backgroundArea = new RectF();
        _textBounds = new Rect();
        _clickListener = null;

        _backgroundPaint = new Paint();
        _backgroundPaint.setStyle(Paint.Style.FILL);
        _backgroundPaint.setAntiAlias(true);

        _textPaint = new Paint();
        _textPaint.setAntiAlias(true);
        _textPaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));

        _progressPaint = new Paint();
        _progressPaint.setStyle(Paint.Style.FILL);
        _progressPaint.setColor(Color.BLACK);
        _progressPaint.setAntiAlias(true);
        _progressPaint.setAlpha(50);

        _valuePoint = new double[2];
        _pixelPoint = new double[2];

        setProgress(0);
        setState(true);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        _backgroundArea.set(0, 0, w, h);
        _textPaint.setTextSize(_backgroundArea.height() * 0.4f);

        updateTransforms();
    }

    @Override
    public void onDraw(final Canvas canvas) {
        super.onDraw(canvas);

        canvas.save();
        canvas.drawRect(_backgroundArea, _backgroundPaint);
        canvas.restore();

        drawText(canvas);

        drawProgress(canvas);
    }

    private void drawText(final Canvas canvas) {
        float textHeightDiv = _buttonSubText.isEmpty() ? 2f : 3f;

        canvas.save();
        _textPaint.setColor(Color.WHITE);
        _textPaint.getTextBounds(_buttonText, 0, _buttonText.length(), _textBounds);
        float titleHeight = _textBounds.height();
        canvas.drawText(_buttonText, (_backgroundArea.width() / 2f) - (_textBounds.width() / 2f), (_backgroundArea.height() / textHeightDiv) + (titleHeight / 2f), _textPaint);
        if (!_buttonSubText.isEmpty()) {
            _textPaint.setColor(Color.BLACK);
            float textSize = _textPaint.getTextSize();
            _textPaint.setTextSize(textSize * 0.5f);
            _textPaint.getTextBounds(_buttonSubText, 0, _buttonSubText.length(), _textBounds);
            canvas.drawText(_buttonSubText, (_backgroundArea.width() / 2f) - (_textBounds.width() / 2f), (_backgroundArea.height() / textHeightDiv) + (titleHeight * 1.1f) + (_textBounds.height() / 2f), _textPaint);
            _textPaint.setTextSize(textSize);
        }
        canvas.restore();
    }

    private void drawProgress(final Canvas canvas) {
        if (_progress == 0)
            return;

        double[] valuePoint = { _progress, 0 };

        _progressValuesToPixels.transform(valuePoint, 0, _pixelPoint, 0, 1);

        float progress = (float) _pixelPoint[0];

        RectF progressRect = new RectF(_backgroundArea.left, _backgroundArea.top, progress, _backgroundArea.bottom);

        canvas.save();
        canvas.drawRect(progressRect, _progressPaint);
        canvas.restore();
    }

    public synchronized void setState(boolean start) {
        if (start ) {
            _progress = 0;
        }
        int color = Color.parseColor(start  ? "#a4c639" : "#ed1111");
        _backgroundPaint.setColor(color);
        _buttonText = start ? "Start Tests" : "Stop Tests";
        setSubText("");
        postInvalidate();
    }

    public synchronized void setSubText(@NonNull String text) {
        _buttonSubText = text;
        postInvalidate();
    }

    public synchronized void setProgress(int value) {
        _progress = value;
        postInvalidate();
    }

    public void setClickListener(OnClickListener listener) {
        _clickListener = listener;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(final MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN: {
                if (_clickListener != null) {
                    _clickListener.onClick(null);
                }
                return true;
            }
        }
        return super.onTouchEvent(event);
    }

    private void updateTransforms() {
        double[] values = {
                0, 1,
                100, 1,
                0, 0,
                100, 0
        };

        double[] pixels = {
                _backgroundArea.left, _backgroundArea.top,
                _backgroundArea.right, _backgroundArea.top,
                _backgroundArea.left, _backgroundArea.bottom,
                _backgroundArea.right, _backgroundArea.bottom
        };

        _progressValuesToPixels = PerspectiveTransform.getQuadToQuad(
                values[0], values[1], values[2], values[3], values[4], values[5], values[6], values[7],
                pixels[0], pixels[1], pixels[2], pixels[3], pixels[4], pixels[5], pixels[6], pixels[7]
        );

        _progressPixelsToValues = PerspectiveTransform.getQuadToQuad(
                pixels[0], pixels[1], pixels[2], pixels[3], pixels[4], pixels[5], pixels[6], pixels[7],
                values[0], values[1], values[2], values[3], values[4], values[5], values[6], values[7]
        );
    }
}
