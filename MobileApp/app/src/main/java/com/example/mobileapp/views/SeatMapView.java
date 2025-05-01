package com.example.mobileapp.views;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

import com.example.mobileapp.R;
import com.example.mobileapp.models.SeatAvailability;
import com.example.mobileapp.models.TicketType;
import com.example.mobileapp.utils.LogUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class SeatMapView extends View {
    private static final String TAG = "SeatMapView";
    private static final int ROWS = 10;
    private static final int COLS = 15;
    private static final int SEAT_MARGIN = 4;

    private Paint availablePaint;
    private Paint selectedPaint;
    private Paint reservedPaint;
    private Paint vipPaint;
    private Paint premiumPaint;
    private Paint standardPaint;
    private Paint textPaint;
    private Paint legendPaint;
    private Paint legendTextPaint;

    private float seatWidth;
    private float seatHeight;
    private float startX;
    private float startY;

    private boolean[][] reservedSeats = new boolean[ROWS][COLS];
    private boolean[][] selectedSeats = new boolean[ROWS][COLS];
    private int[][] seatTypes = new int[ROWS][COLS]; // 0: Standard, 1: Premium, 2: VIP

    private List<TicketType> ticketTypes = new ArrayList<>();
    private Map<Integer, Integer> selectedSeatsCount = new HashMap<>();
    private int maxSeatsPerType = 5;

    private OnSeatSelectedListener onSeatSelectedListener;

    public interface OnSeatSelectedListener {
        void onSeatSelected(int row, int col, boolean isSelected, int seatType);
    }

    public SeatMapView(Context context) {
        super(context);
        init();
    }

    public SeatMapView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public SeatMapView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        availablePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        availablePaint.setColor(Color.LTGRAY);
        availablePaint.setStyle(Paint.Style.FILL);

        selectedPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        selectedPaint.setColor(Color.GREEN);
        selectedPaint.setStyle(Paint.Style.FILL);

        reservedPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        reservedPaint.setColor(Color.RED);
        reservedPaint.setStyle(Paint.Style.FILL);

        vipPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        vipPaint.setColor(getResources().getColor(R.color.vipSeat));
        vipPaint.setStyle(Paint.Style.STROKE);
        vipPaint.setStrokeWidth(4f);

        premiumPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        premiumPaint.setColor(getResources().getColor(R.color.premiumSeat));
        premiumPaint.setStyle(Paint.Style.STROKE);
        premiumPaint.setStrokeWidth(4f);

        standardPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        standardPaint.setColor(getResources().getColor(R.color.standardSeat));
        standardPaint.setStyle(Paint.Style.STROKE);
        standardPaint.setStrokeWidth(4f);

        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setColor(Color.BLACK);
        textPaint.setTextSize(24f);
        textPaint.setTextAlign(Paint.Align.CENTER);

        legendPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        legendPaint.setStyle(Paint.Style.FILL);

        legendTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        legendTextPaint.setColor(Color.BLACK);
        legendTextPaint.setTextSize(30f);

        // Generate random reserved seats and seat types
        generateRandomSeats();

        // We'll call checkLocallyReservedSeats from the activity
    }

    private void generateRandomSeats() {
        Random random = new Random();

        // VIP seats (first 2 rows)
        for (int row = 0; row < 2; row++) {
            for (int col = 0; col < COLS; col++) {
                seatTypes[row][col] = 2; // VIP
                reservedSeats[row][col] = random.nextFloat() < 0.3f; // 30% chance of being reserved
            }
        }

        // Premium seats (next 3 rows)
        for (int row = 2; row < 5; row++) {
            for (int col = 0; col < COLS; col++) {
                seatTypes[row][col] = 1; // Premium
                reservedSeats[row][col] = random.nextFloat() < 0.4f; // 40% chance of being reserved
            }
        }

        // Standard seats (remaining rows)
        for (int row = 5; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {
                seatTypes[row][col] = 0; // Standard
                reservedSeats[row][col] = random.nextFloat() < 0.2f; // 20% chance of being reserved
            }
        }
    }

    public void setEventId(int eventId) {
        if (eventId > 0) {
            checkLocallyReservedSeats(getContext(), eventId);
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        // Calculate seat dimensions
        float availableWidth = w * 0.9f; // Use 90% of width
        float availableHeight = h * 0.7f; // Use 70% of height for seats

        seatWidth = (availableWidth - (COLS - 1) * SEAT_MARGIN) / COLS;
        seatHeight = (availableHeight - (ROWS - 1) * SEAT_MARGIN) / ROWS;

        // Center the seat map
        startX = (w - availableWidth) / 2;
        startY = (h - availableHeight) / 2;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // Draw stage at the top
        Paint stagePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        stagePaint.setColor(Color.DKGRAY);
        stagePaint.setStyle(Paint.Style.FILL);

        float stageWidth = getWidth() * 0.7f;
        float stageHeight = getHeight() * 0.05f;
        float stageX = (getWidth() - stageWidth) / 2;
        float stageY = startY / 2 - stageHeight / 2;

        RectF stageRect = new RectF(stageX, stageY, stageX + stageWidth, stageY + stageHeight);
        canvas.drawRect(stageRect, stagePaint);

        // Draw "STAGE" text
        textPaint.setTextSize(stageHeight * 0.8f);
        canvas.drawText("STAGE", stageRect.centerX(), stageRect.centerY() + stageHeight / 4, textPaint);

        // Draw seats
        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {
                float left = startX + col * (seatWidth + SEAT_MARGIN);
                float top = startY + row * (seatHeight + SEAT_MARGIN);
                float right = left + seatWidth;
                float bottom = top + seatHeight;

                RectF seatRect = new RectF(left, top, right, bottom);

                // Draw base seat color based on status
                if (reservedSeats[row][col]) {
                    canvas.drawRoundRect(seatRect, 8, 8, reservedPaint);
                } else if (selectedSeats[row][col]) {
                    canvas.drawRoundRect(seatRect, 8, 8, selectedPaint);
                } else {
                    canvas.drawRoundRect(seatRect, 8, 8, availablePaint);
                }

                // Draw seat border based on type
                switch (seatTypes[row][col]) {
                    case 2: // VIP
                        canvas.drawRoundRect(seatRect, 8, 8, vipPaint);
                        break;
                    case 1: // Premium
                        canvas.drawRoundRect(seatRect, 8, 8, premiumPaint);
                        break;
                    case 0: // Standard
                        canvas.drawRoundRect(seatRect, 8, 8, standardPaint);
                        break;
                }

                // Draw seat number
                String seatNumber = String.format("%c%d", 'A' + row, col + 1);
                textPaint.setTextSize(Math.min(seatWidth, seatHeight) * 0.4f);
                canvas.drawText(seatNumber, seatRect.centerX(), seatRect.centerY() + textPaint.getTextSize() / 3, textPaint);
            }
        }

        // Draw legend
        drawLegend(canvas);
    }

    private void drawLegend(Canvas canvas) {
        float legendStartX = startX;
        float legendStartY = startY + ROWS * (seatHeight + SEAT_MARGIN) + 50;
        float legendItemWidth = 30;
        float legendItemHeight = 30;
        float legendTextOffset = 40;
        float legendItemSpacing = 150;

        // Available seat
        legendPaint.setColor(Color.LTGRAY);
        canvas.drawRect(legendStartX, legendStartY, legendStartX + legendItemWidth, legendStartY + legendItemHeight, legendPaint);
        canvas.drawText("Available", legendStartX + legendItemWidth + 10, legendStartY + legendItemHeight - 5, legendTextPaint);

        // Selected seat
        legendPaint.setColor(Color.GREEN);
        canvas.drawRect(legendStartX + legendItemSpacing, legendStartY, legendStartX + legendItemSpacing + legendItemWidth, legendStartY + legendItemHeight, legendPaint);
        canvas.drawText("Selected", legendStartX + legendItemSpacing + legendItemWidth + 10, legendStartY + legendItemHeight - 5, legendTextPaint);

        // Reserved seat
        legendPaint.setColor(Color.RED);
        canvas.drawRect(legendStartX + 2 * legendItemSpacing, legendStartY, legendStartX + 2 * legendItemSpacing + legendItemWidth, legendStartY + legendItemHeight, legendPaint);
        canvas.drawText("Reserved", legendStartX + 2 * legendItemSpacing + legendItemWidth + 10, legendStartY + legendItemHeight - 5, legendTextPaint);

        // Second row of legend
        float secondRowY = legendStartY + legendItemHeight + 30;

        // VIP seat
        legendPaint.setColor(Color.LTGRAY);
        canvas.drawRect(legendStartX, secondRowY, legendStartX + legendItemWidth, secondRowY + legendItemHeight, legendPaint);
        legendPaint.setStyle(Paint.Style.STROKE);
        legendPaint.setStrokeWidth(4f);
        legendPaint.setColor(getResources().getColor(R.color.vipSeat));
        canvas.drawRect(legendStartX, secondRowY, legendStartX + legendItemWidth, secondRowY + legendItemHeight, legendPaint);
        legendPaint.setStyle(Paint.Style.FILL);
        canvas.drawText("VIP", legendStartX + legendItemWidth + 10, secondRowY + legendItemHeight - 5, legendTextPaint);

        // Premium seat
        legendPaint.setColor(Color.LTGRAY);
        canvas.drawRect(legendStartX + legendItemSpacing, secondRowY, legendStartX + legendItemSpacing + legendItemWidth, secondRowY + legendItemHeight, legendPaint);
        legendPaint.setStyle(Paint.Style.STROKE);
        legendPaint.setStrokeWidth(4f);
        legendPaint.setColor(getResources().getColor(R.color.premiumSeat));
        canvas.drawRect(legendStartX + legendItemSpacing, secondRowY, legendStartX + legendItemSpacing + legendItemWidth, secondRowY + legendItemHeight, legendPaint);
        legendPaint.setStyle(Paint.Style.FILL);
        canvas.drawText("Premium", legendStartX + legendItemSpacing + legendItemWidth + 10, secondRowY + legendItemHeight - 5, legendTextPaint);

        // Standard seat
        legendPaint.setColor(Color.LTGRAY);
        canvas.drawRect(legendStartX + 2 * legendItemSpacing, secondRowY, legendStartX + 2 * legendItemSpacing + legendItemWidth, secondRowY + legendItemHeight, legendPaint);
        legendPaint.setStyle(Paint.Style.STROKE);
        legendPaint.setStrokeWidth(4f);
        legendPaint.setColor(getResources().getColor(R.color.standardSeat));
        canvas.drawRect(legendStartX + 2 * legendItemSpacing, secondRowY, legendStartX + 2 * legendItemSpacing + legendItemWidth, secondRowY + legendItemHeight, legendPaint);
        legendPaint.setStyle(Paint.Style.FILL);
        canvas.drawText("Standard", legendStartX + 2 * legendItemSpacing + legendItemWidth + 10, secondRowY + legendItemHeight - 5, legendTextPaint);
    }

    public void checkLocallyReservedSeats(Context context, int eventId) {
        SharedPreferences prefs = context.getSharedPreferences("TeskertiPrefs", Context.MODE_PRIVATE);
        String reservedSeatsStr = prefs.getString("reserved_seats_" + eventId, "");

        if (!reservedSeatsStr.isEmpty()) {
            String[] seatIds = reservedSeatsStr.split(",");
            for (String seatId : seatIds) {
                try {
                    // Format is like "A1", "B2", etc.
                    char rowChar = seatId.charAt(0);
                    int rowIndex = rowChar - 'A';
                    int colIndex = Integer.parseInt(seatId.substring(1)) - 1;

                    // Mark as reserved
                    if (rowIndex >= 0 && rowIndex < ROWS && colIndex >= 0 && colIndex < COLS) {
                        reservedSeats[rowIndex][colIndex] = true;
                    }
                } catch (Exception e) {
                    LogUtils.error("SeatMapView");
                }
            }
            invalidate(); // Redraw with new reserved seats
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            float x = event.getX();
            float y = event.getY();

            // Check if touch is within seat map area
            if (x >= startX && x <= startX + COLS * (seatWidth + SEAT_MARGIN) &&
                    y >= startY && y <= startY + ROWS * (seatHeight + SEAT_MARGIN)) {

                // Calculate row and column
                int col = (int) ((x - startX) / (seatWidth + SEAT_MARGIN));
                int row = (int) ((y - startY) / (seatHeight + SEAT_MARGIN));

                // Ensure row and column are within bounds
                if (row >= 0 && row < ROWS && col >= 0 && col < COLS) {
                    // Check if seat is not reserved
                    if (!reservedSeats[row][col]) {
                        int seatType = seatTypes[row][col];

                        // Toggle seat selection
                        if (selectedSeats[row][col]) {
                            // Deselect seat
                            selectedSeats[row][col] = false;

                            // Update count
                            Integer count = selectedSeatsCount.get(seatType);
                            if (count != null && count > 0) {
                                selectedSeatsCount.put(seatType, count - 1);
                            }

                            if (onSeatSelectedListener != null) {
                                onSeatSelectedListener.onSeatSelected(row, col, false, seatType);
                            }
                        } else {
                            // Check if we've reached the maximum number of seats for this type
                            Integer count = selectedSeatsCount.get(seatType);
                            if (count == null) {
                                count = 0;
                            }

                            if (count < maxSeatsPerType) {
                                // Select seat
                                selectedSeats[row][col] = true;

                                // Update count
                                selectedSeatsCount.put(seatType, count + 1);

                                if (onSeatSelectedListener != null) {
                                    onSeatSelectedListener.onSeatSelected(row, col, true, seatType);
                                }
                            } else {
                                LogUtils.debug(TAG + ": " + "Maximum seats reached for type: " + seatType);
                            }
                        }

                        // Redraw the view
                        invalidate();
                        return true;
                    }
                }
            }
        }

        return super.onTouchEvent(event);
    }

    public void setTicketTypes(List<TicketType> ticketTypes) {
        this.ticketTypes = ticketTypes;
        selectedSeatsCount.clear();

        // Initialize seat counts for each type
        for (TicketType type : ticketTypes) {
            selectedSeatsCount.put(type.getId(), 0);
        }
    }

    public void setMaxSeatsPerType(int maxSeats) {
        this.maxSeatsPerType = maxSeats;
    }

    public void setOnSeatSelectedListener(OnSeatSelectedListener listener) {
        this.onSeatSelectedListener = listener;
    }

    public List<SeatAvailability> getSelectedSeats() {
        List<SeatAvailability> seats = new ArrayList<>();

        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {
                if (selectedSeats[row][col]) {
                    SeatAvailability seat = new SeatAvailability();
                    seat.setRow(row);
                    seat.setColumn(col);
                    seat.setSeatNumber(String.format("%c%d", 'A' + row, col + 1));
                    seat.setTicketTypeId(seatTypes[row][col]);
                    seats.add(seat);
                }
            }
        }

        return seats;
    }

    public void clearSelection() {
        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {
                selectedSeats[row][col] = false;
            }
        }

        for (Integer key : selectedSeatsCount.keySet()) {
            selectedSeatsCount.put(key, 0);
        }

        invalidate();
    }

    public Map<Integer, Integer> getSelectedSeatsCount() {
        return selectedSeatsCount;
    }
}
