package com.example.user1.todo;

import android.app.Activity;
import android.content.Intent;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
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
import org.apache.http.ParseException;

/**
 * Created by User 1 on 06-06-2015.
 */
public class EditActivity extends Activity {


    ImageView edSubmit;
    EditText editTask;
    TextView editDate;
    DatePicker datePicker;
    HashMap<String, String> nameToNum = new HashMap<String, String>();
    String description;
    int day, month, year;
    List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
    String jsonStr, subMonth, subDate, subYear, subMonNum;
    String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_task);

        nameToNum.put("Jan", "01");
        nameToNum.put("Feb", "02");
        nameToNum.put("Mar", "03");
        nameToNum.put("Apr", "04");
        nameToNum.put("May", "05");
        nameToNum.put("Jun", "06");
        nameToNum.put("Jul", "07");
        nameToNum.put("Aug", "08");
        nameToNum.put("Sep", "09");
        nameToNum.put("Oct", "10");
        nameToNum.put("Nov", "11");
        nameToNum.put("Dec", "12");

        edSubmit = (ImageView) findViewById(R.id.imageViewEdSubmit);
        editTask = (EditText) findViewById(R.id.editTextExTask);
        editDate = (TextView) findViewById(R.id.textViewPickDate);
        Bundle extras = getIntent().getExtras();
        String desc = extras.getString("description");
        String date = extras.getString("date");
        subMonth = date.substring(4, 7);
        subDate = date.substring(8, 10);
        subYear = date.substring(11, date.length());
        subMonNum = nameToNum.get(subMonth);
        id = extras.getString("id");

        editTask.setText(desc);
        editDate.setText(subDate + "-" + subMonNum + "-" + subYear);
        nameValuePairs.add(new BasicNameValuePair("oldDesc", desc));
        nameValuePairs.add(new BasicNameValuePair("oldDate", date));
        nameValuePairs.add(new BasicNameValuePair("id", id));

        day = Integer.parseInt(subDate);
        month = Integer.parseInt(subMonNum);
        year = Integer.parseInt(subYear);

        editDate.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub

                DatePickerDialog dpd = new DatePickerDialog(EditActivity.this,
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view,
                                                  int yearPicker, int monthOfYear,
                                                  int dayOfMonth) {
                                // TODO Auto-generated method stub
                                editDate.setText(dayOfMonth + "-"
                                        + (monthOfYear + 1) + "-" + yearPicker);
                                day = dayOfMonth;
                                month = monthOfYear + 1;
                                year = yearPicker;

                            }
                        }, Integer.parseInt(subYear), Integer
                        .parseInt(subMonNum) - 1, Integer
                        .parseInt(subDate));
                dpd.show();
            }
        });

        edSubmit.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                description = editTask.getText().toString();
                Log.d("todo", description);

                nameValuePairs.add(new BasicNameValuePair("description",
                        description));
                nameValuePairs.add(new BasicNameValuePair("day", Integer
                        .toString(day)));
                nameValuePairs.add(new BasicNameValuePair("month", Integer
                        .toString(month)));
                nameValuePairs.add(new BasicNameValuePair("year", Integer
                        .toString(year)));

                new getEdJson().execute();
            }
        });



    }

    public class getEdJson extends AsyncTask<Void, Void, Void> {


        ProgressDialog pd;
        @Override
        protected void onPreExecute() {
            pd = new ProgressDialog(EditActivity.this);
            pd.setTitle("Processing...");
            pd.setMessage("Please Wait.");
            pd.setIndeterminate(true);
            pd.setCancelable(false);
            pd.show();
        }
        @Override
        protected Void doInBackground(Void... params) {
            // TODO Auto-generated method stub

            try {

                ServiceHandler sh = new ServiceHandler();
                String url = "http://1-dot-macro-gadget-785.appspot.com/edittask";
                int method = 1;
                sh.makeServiceCall(url, method, nameValuePairs);
            } catch (ParseException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            if(pd!=null){
                pd.dismiss();
            }

            Toast.makeText(EditActivity.this, "Task edited successfully",
                    Toast.LENGTH_LONG).show();
            Intent intent = new Intent(EditActivity.this, MainActivity.class);
            intent.setFlags(android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }

    }



}
