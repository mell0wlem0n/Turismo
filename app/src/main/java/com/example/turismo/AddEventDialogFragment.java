package com.example.turismo;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.google.api.client.util.DateTime;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Calendar;

public class AddEventDialogFragment extends DialogFragment {

    private TextInputEditText summaryInput;
    private TextInputEditText locationInput;
    private TextInputEditText startDateTimeInput;
    private TextInputEditText endDateTimeInput;
    private TextInputEditText reasonInput;
    private Button addButton;

    private Calendar startCalendar;
    private Calendar endCalendar;

    private AddEventListener listener;

    public interface AddEventListener {
        void onEventAdded(String summary, String location, DateTime startDateTime, DateTime endDateTime, String reason);
    }
    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            // Set the size of the dialog
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.WRAP_CONTENT;
            dialog.getWindow().setLayout(width, height);
        }
    }
    public AddEventDialogFragment(AddEventListener listener) {
        this.listener = listener;
        this.startCalendar = Calendar.getInstance();
        this.endCalendar = Calendar.getInstance();
    }

    public AddEventDialogFragment(AddEventListener listener, String name, String location) {
        this.listener = listener;
        this.startCalendar = Calendar.getInstance();
        this.endCalendar = Calendar.getInstance();
        this.startCalendar.setTimeInMillis(System.currentTimeMillis());
        this.endCalendar.setTimeInMillis(System.currentTimeMillis());

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_event_dialog, container, false);

        summaryInput = view.findViewById(R.id.summaryInput);
        locationInput = view.findViewById(R.id.locationInput);
        startDateTimeInput = view.findViewById(R.id.startDateTimeInput);
        endDateTimeInput = view.findViewById(R.id.endDateTimeInput);
        reasonInput = view.findViewById(R.id.reasonInput);
        addButton = view.findViewById(R.id.addButton);

        startDateTimeInput.setOnClickListener(v -> pickDateTime(startDateTimeInput, startCalendar));
        endDateTimeInput.setOnClickListener(v -> pickDateTime(endDateTimeInput, endCalendar));

        addButton.setOnClickListener(v -> {
            String summary = summaryInput.getText().toString().trim();
            String location = locationInput.getText().toString().trim();
            String reason = reasonInput.getText().toString().trim();

            if (summary.isEmpty() || location.isEmpty() || startDateTimeInput.getText().toString().isEmpty() || endDateTimeInput.getText().toString().isEmpty() || reason.isEmpty()) {
                Toast.makeText(getContext(), "Please fill in all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            DateTime startDateTime = new DateTime(startCalendar.getTime());
            DateTime endDateTime = new DateTime(endCalendar.getTime());

            listener.onEventAdded(summary, location, startDateTime, endDateTime, reason);
            dismiss();
        });

        return view;
    }

    private void pickDateTime(TextInputEditText editText, Calendar calendar) {
        DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(),
                (view, year, month, dayOfMonth) -> {
                    calendar.set(year, month, dayOfMonth);
                    TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(),
                            (view1, hourOfDay, minute) -> {
                                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                                calendar.set(Calendar.MINUTE, minute);
                                editText.setText(DateFormat.format("yyyy-MM-dd HH:mm", calendar));
                            }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true);
                    timePickerDialog.show();
                }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }
}
