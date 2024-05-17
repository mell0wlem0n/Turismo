package com.example.turismo;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.google.api.client.util.DateTime;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Calendar;

public class AddEventDialogFragment extends DialogFragment {

    private final AddEventCallback callback;
    private String preFilledName;
    private String preFilledLocation;

    public interface AddEventCallback {
        void onAddEvent(String summary, String location, DateTime startDateTime, DateTime endDateTime);
    }

    public AddEventDialogFragment(AddEventCallback callback) {
        this.callback = callback;
    }

    public AddEventDialogFragment(AddEventCallback callback, String name, String location) {
        this.callback = callback;
        this.preFilledName = name;
        this.preFilledLocation = location;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_event_dialog, container, false);

        TextInputEditText summaryInput = view.findViewById(R.id.summaryInput);
        TextInputEditText locationInput = view.findViewById(R.id.locationInput);
        TextInputEditText startDateInput = view.findViewById(R.id.startDateInput);
        TextInputEditText startTimeInput = view.findViewById(R.id.startTimeInput);
        TextInputEditText endDateInput = view.findViewById(R.id.endDateInput);
        TextInputEditText endTimeInput = view.findViewById(R.id.endTimeInput);
        Button addButton = view.findViewById(R.id.addButton);

        // Pre-fill inputs if provided
        if (preFilledName != null) {
            summaryInput.setText(preFilledName);
        }
        if (preFilledLocation != null) {
            locationInput.setText(preFilledLocation);
        }

        Calendar calendar = Calendar.getInstance();

        startDateInput.setOnClickListener(v -> {
            DatePickerDialog datePicker = new DatePickerDialog(getContext(), (view1, year, month, dayOfMonth) -> {
                startDateInput.setText(dayOfMonth + "/" + (month + 1) + "/" + year);
                calendar.set(year, month, dayOfMonth);
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
            datePicker.show();
        });

        startTimeInput.setOnClickListener(v -> {
            TimePickerDialog timePicker = new TimePickerDialog(getContext(), (view12, hourOfDay, minute) -> {
                startTimeInput.setText(hourOfDay + ":" + minute);
                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                calendar.set(Calendar.MINUTE, minute);
            }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true);
            timePicker.show();
        });

        endDateInput.setOnClickListener(v -> {
            DatePickerDialog datePicker = new DatePickerDialog(getContext(), (view1, year, month, dayOfMonth) -> {
                endDateInput.setText(dayOfMonth + "/" + (month + 1) + "/" + year);
                calendar.set(year, month, dayOfMonth);
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
            datePicker.show();
        });

        endTimeInput.setOnClickListener(v -> {
            TimePickerDialog timePicker = new TimePickerDialog(getContext(), (view12, hourOfDay, minute) -> {
                endTimeInput.setText(hourOfDay + ":" + minute);
                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                calendar.set(Calendar.MINUTE, minute);
            }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true);
            timePicker.show();
        });

        addButton.setOnClickListener(v -> {
            String summary = summaryInput.getText().toString();
            String location = locationInput.getText().toString();
            DateTime startDateTime = new DateTime(calendar.getTimeInMillis());
            DateTime endDateTime = new DateTime(calendar.getTimeInMillis());

            callback.onAddEvent(summary, location, startDateTime, endDateTime);
            dismiss();
        });

        return view;
    }
}
