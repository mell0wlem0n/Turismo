package com.example.turismo;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import android.widget.CalendarView;

import java.util.Calendar;
import java.util.HashSet;

public class CustomCalendarView extends CalendarView {

    private HashSet<Long> eventDates;

    public CustomCalendarView(Context context) {
        super(context);
    }

    public CustomCalendarView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomCalendarView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setEventDates(HashSet<Long> eventDates) {
        this.eventDates = eventDates;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (eventDates != null) {
            for (Long date : eventDates) {
                if (isSameDay(date, getDate())) {
                    // Draw a circle or highlight the date
                    drawCircleOnDate(canvas, date);
                }
            }
        }
    }

    private boolean isSameDay(long date1, long date2) {
        Calendar calendar1 = Calendar.getInstance();
        calendar1.setTimeInMillis(date1);

        Calendar calendar2 = Calendar.getInstance();
        calendar2.setTimeInMillis(date2);

        return calendar1.get(Calendar.YEAR) == calendar2.get(Calendar.YEAR) &&
                calendar1.get(Calendar.MONTH) == calendar2.get(Calendar.MONTH) &&
                calendar1.get(Calendar.DAY_OF_MONTH) == calendar2.get(Calendar.DAY_OF_MONTH);
    }

    private void drawCircleOnDate(Canvas canvas, long date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(date);

        // Calculate the position for the circle
        int x = calculateX(calendar);
        int y = calculateY(calendar);

        // Draw the circle
        Paint paint = new Paint();
        paint.setColor(Color.RED);
        paint.setStyle(Paint.Style.FILL);

        canvas.drawCircle(x, y, 10, paint);
    }

    private int calculateX(Calendar calendar) {
        // Calculate the x position for the circle
        return 0;
    }

    private int calculateY(Calendar calendar) {
        // Calculate the y position for the circle
        return 0;
    }
}
