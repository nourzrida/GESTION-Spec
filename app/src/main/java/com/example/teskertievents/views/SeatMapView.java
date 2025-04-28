package com.example.teskertievents.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.example.teskertievents.models.SeatAvailability;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SeatMapView extends View {
    private static final int ROWS = 8;
    private static final int SEATS_PER_ROW = 10;
    private static final String[] ROW_LABELS = {"A", "B", "C", "D", "E", "F", "G", "H"};

    private Paint availableSeatPaint;
    private Paint selectedSeatPaint;
    private Paint reservedSeatPaint;
    private Paint textPaint;
    private Paint stagePaint;

    private int seatSize;
    private int seatSpacing;
    private int rowSpacing;
    private int stageHeight;

    private SeatAvailability seatAvailability;
    private Map<String, Rect> seatRects = new HashMap<>();
    private List<String> selectedSeats = new ArrayList<>();

    private OnSeatSelectedListener listener;

    public interface OnSeatSelectedListener {
        void onSeatSelected(String seatId, boolean isSelected);
    }

    public SeatMapView(Context context) {
        super(context);
        init();
    }

    public SeatMapView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public SeatMapView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        // Initialize paints
        availableSeatPaint = new Paint();
        availableSeatPaint.setColor(Color.parseColor("#4CAF50"));
        availableSeatPaint.setStyle(Paint.Style.FILL);

        selectedSeatPaint = new Paint();
        selectedSeatPaint.setColor(Color.parseColor("#2196F3"));
        selectedSeatPaint.setStyle(Paint.Style.FILL);

        reservedSeatPaint = new Paint();
        reservedSeatPaint.setColor(Color.parseColor("#E91E63"));
        reservedSeatPaint.setStyle(Paint.Style.FILL);

        textPaint = new Paint();
        textPaint.setColor(Color.WHITE);
        textPaint.setTextSize(24);
        textPaint.setTextAlign(Paint.Align.CENTER);

        stagePaint = new Paint();
        stagePaint.setColor(Color.parseColor("#9E9E9E"));
        stagePaint.setStyle(Paint.Style.FILL);

        // Set default dimensions
        seatSize = 60;
        seatSpacing = 10;
        rowSpacing = 20;
        stageHeight = 80;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = (seatSize + seatSpacing) * SEATS_PER_ROW + seatSpacing;
        int height = stageHeight + (seatSize + rowSpacing) * ROWS + rowSpacing;
        
        setMeasuredDimension(width, height);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // Draw stage
        canvas.drawRect(0, 0, getWidth(), stageHeight, stagePaint);
        canvas.drawText("STAGE", getWidth() / 2, stageHeight / 2 + 8, textPaint);

        // Draw seats
        seatRects.clear();
        for (int row = 0; row < ROWS; row++) {
            for (int seat = 0; seat < SEATS_PER_ROW; seat++) {
                String seatId = ROW_LABELS[row] + (seat + 1);
                
                int left = (seat * (seatSize + seatSpacing)) + seatSpacing;
                int top = stageHeight + (row * (seatSize + rowSpacing)) + rowSpacing;
                int right = left + seatSize;
                int bottom = top + seatSize;
                
                Rect seatRect = new Rect(left, top, right, bottom);
                seatRects.put(seatId, seatRect);
                
                // Determine seat color
                Paint paint;
                if (selectedSeats.contains(seatId)) {
                    paint = selectedSeatPaint;
                } else if (seatAvailability != null && seatAvailability.isSeatReserved(seatId)) {
                    paint = reservedSeatPaint;
                } else {
                    paint = availableSeatPaint;
                }
                
                // Draw seat
                canvas.drawRect(seatRect, paint);
                
                // Draw seat label
                canvas.drawText(seatId, seatRect.centerX(), seatRect.centerY() + 8, textPaint);
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            float x = event.getX();
            float y = event.getY();
            
            for (Map.Entry<String, Rect> entry : seatRects.entrySet()) {
                String seatId = entry.getKey();
                Rect rect = entry.getValue();
                
                if (rect.contains((int) x, (int) y)) {
                    // Check if seat is available
                    if (seatAvailability != null && seatAvailability.isSeatReserved(seatId)) {
                        return true; // Seat is already reserved
                    }
                    
                    // Toggle seat selection
                    boolean isSelected = toggleSeat(seatId);
                    
                    // Notify listener
                    if (listener != null) {
                        listener.onSeatSelected(seatId, isSelected);
                    }
                    
                    invalidate();
                    return true;
                }
            }
        }
        
        return super.onTouchEvent(event);
    }

    public void setSeatAvailability(SeatAvailability seatAvailability) {
        this.seatAvailability = seatAvailability;
        invalidate();
    }

    public void setOnSeatSelectedListener(OnSeatSelectedListener listener) {
        this.listener = listener;
    }

    public boolean toggleSeat(String seatId) {
        if (selectedSeats.contains(seatId)) {
            selectedSeats.remove(seatId);
            return false;
        } else {
            selectedSeats.add(seatId);
            return true;
        }
    }

    public List<String> getSelectedSeats() {
        return new ArrayList<>(selectedSeats);
    }
}
