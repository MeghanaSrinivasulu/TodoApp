package com.example.user1.todo;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.message.BasicNameValuePair;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class AddTaskActivity extends Activity {

    EditText taskDescription;
    TextView taskDate;
    ImageView submitTask;
    String description;
    int day, month, year;
    int pickerDay, pickerMonth, pickerYear;
    List<NameValuePair> nameValuePairs;
    String jsonStr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);

        taskDescription = (EditText) findViewById(R.id.editTextTaskDescription);
        submitTask = (ImageView) findViewById(R.id.imageViewSubmit);
        taskDate = (TextView) findViewById(R.id.TextViewAddDate);

        final Calendar c = Calendar.getInstance();
        pickerYear = c.get(Calendar.YEAR);
        pickerMonth = c.get(Calendar.MONTH);
        pickerDay = c.get(Calendar.DAY_OF_MONTH);

        taskDate.setText(pickerDay + "-" + (pickerMonth + 1) + "-" + pickerYear);

        day = pickerDay;
        month = pickerMonth + 1;
        year = pickerYear;

        taskDate.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                DatePickerDialog dpd = new DatePickerDialog(
                        AddTaskActivity.this,
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view,
                                                  int yearPicker, int monthOfYear,
                                                  int dayOfMonth) {
                                // Display Selected date in textbox
                                taskDate.setText(dayOfMonth + "-"
                                        + (monthOfYear + 1) + "-" + yearPicker);

                                day = dayOfMonth;
                                month = monthOfYear + 1;
                                year = yearPicker;
                            }
                        }, pickerYear, pickerMonth, pickerDay);
                dpd.show();

            }

        });

        submitTask.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                description = taskDescription.getText().toString();

                nameValuePairs = new ArrayList<NameValuePair>();
                nameValuePairs.add(new BasicNameValuePair("description",
                        description));
                nameValuePairs.add(new BasicNameValuePair("day", Integer
                        .toString(day)));
                nameValuePairs.add(new BasicNameValuePair("month", Integer
                        .toString(month)));
                nameValuePairs.add(new BasicNameValuePair("year", Integer
                        .toString(year)));
                nameValuePairs.add(new BasicNameValuePair("status", "active"));

                new getJson().execute();

            }
        });
    }

    public class getJson extends AsyncTask<Void, Void, Void> {

        ProgressDialog pd;

        @Override
        protected void onPreExecute() {
            pd = new ProgressDialog(AddTaskActivity.this);
            pd.setTitle("Processing...");
            pd.setMessage("Please Wait.");
            pd.setIndeterminate(true);
            pd.setCancelable(false);
            pd.show();
        }

        @Override
        protected Void doInBackground(Void... params) {

            try {
                ServiceHandler sh = new ServiceHandler();
                String url = "http://1-dot-macro-gadget-785.appspot.com/addtask";
                int method = 1;
                sh.makeServiceCall(url, method, nameValuePairs);

            } catch (ParseException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            if (pd != null) {
                pd.dismiss();
            }
            Toast.makeText(AddTaskActivity.this, "Task added successfully!",
                    Toast.LENGTH_LONG).show();
            Intent intent = new Intent(AddTaskActivity.this, MainActivity.class);
            intent.setFlags(android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }

    }

}
