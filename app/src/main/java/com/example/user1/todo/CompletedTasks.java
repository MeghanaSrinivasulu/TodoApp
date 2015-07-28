package com.example.user1.todo;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class CompletedTasks extends Fragment {
    String[] taskDescriptions, taskDates, taskIds, taskStatus;
    ArrayList<String> selectedIds = new ArrayList<String>();
    ListView showTasks;
    ImageView deleteTasks;
    ArrayList<Long> selectedItems = new ArrayList<Long>();
    ArrayList<String> selectedTasks = new ArrayList<String>();
    ArrayList<String> selectedDates = new ArrayList<String>();
    List<NameValuePair> tasksTobeDeleted = new ArrayList<NameValuePair>();
    LinearLayout iconLayout;
    String completed = "completed";


    public CompletedTasks() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_completed_tasks, container, false);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        try {
            new getTasks().execute("http://1-dot-macro-gadget-785.appspot.com/gettodos?status="+completed).get();
            super.onCreate(savedInstanceState);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        showTasks = (ListView) getView().findViewById(R.id.listViewCompletedTasks);
        deleteTasks = (ImageView)getView().findViewById(R.id.iconViewCompletedTasks).findViewById(R.id.imageViewDeleteCompletedTasks);
        showTasks.setAdapter(new CustomAdapter(getActivity(), taskDescriptions, taskDates, taskIds));
        iconLayout = (LinearLayout)getView().findViewById(R.id.iconViewCompletedTasks);

        deleteTasks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new deleteItems().execute();
            }
        });

        showTasks.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                if(selectedItems.contains(id)){
                    view.setBackgroundResource(0);
                    selectedItems.remove(id);
                    selectedTasks.remove(taskDescriptions[position]);
                    selectedDates.remove(taskDates[position]);
                    selectedIds.remove(taskIds[position]);

                }
                else{
                    selectedItems.add(id);
                    selectedTasks.add(taskDescriptions[position]);
                    selectedDates.add(taskDates[position]);
                    selectedIds.add(taskIds[position]);
                    int color = Color.argb(255, 255, 175, 64);
                    view.setBackgroundColor(color);

                }
                if(selectedItems.size() == 1){

                    iconLayout.setVisibility(View.VISIBLE);
                    deleteTasks.setVisibility(View.VISIBLE);

                }
                else if(selectedItems.size() > 1){
                    iconLayout.setVisibility(View.VISIBLE);
                    deleteTasks.setVisibility(View.VISIBLE);
                }
                else if(selectedItems.size()==0){
                    deleteTasks.setVisibility(View.INVISIBLE);
                }
            }
        });
        super.onActivityCreated(savedInstanceState);
    }

    public class getTasks extends AsyncTask<String, Void, Void> {

        ProgressDialog pd;

        @Override
        protected Void doInBackground(String... url) {
            Log.d("Todo", "getTasks");
            String list_url;
            if(url[0] == null){
                list_url = "http://1-dot-macro-gadget-785.appspot.com/gettodos";
            }
            else
            {
                list_url = url[0];
            }
            int method = 2;
            try {
                ServiceHandler sh = new ServiceHandler();
                String jsonStr = sh.makeServiceCall(list_url, method);
                JSONObject jsonObj = new JSONObject(jsonStr);
                JSONArray jsonArray = jsonObj.getJSONArray("listoftodos");
                taskDescriptions = new String[jsonArray.length()];
                taskDates = new String[jsonArray.length()];
                taskIds = new String[jsonArray.length()];
                taskStatus = new String[jsonArray.length()];

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jobjTask = (JSONObject) jsonArray.get(i);
                    String desc = (String) jobjTask.get("description");
                    String todoDate = (String) jobjTask.get("date");
                    String key = (String) jobjTask.get("id");
                    String dtOne = todoDate.substring(0, 10);
                    String dtTwo = todoDate.substring(24, todoDate.length());
                    String finalDate = dtOne + " " + dtTwo;
                    String status = (String) jobjTask.get("status");
                    taskDescriptions[i] = desc;
                    taskDates[i] = finalDate;
                    taskIds[i] = key;
                    taskStatus[i] = status;
                }
            } catch (JSONException e) {

                e.printStackTrace();
            }

            return null;
        }



    }

    public class CustomAdapter extends ArrayAdapter<String> {

        Context c;
        String[] taskDescriptions;
        String[] taskDates;
        String[] taskIds;

        public CustomAdapter(Context c, String[] taskDescriptions,
                             String[] taskDates, String[] taskIds) {

            super(c, R.layout.list_item, R.id.textViewTask, taskDescriptions);

            this.c = c;
            this.taskDescriptions = taskDescriptions;
            this.taskDates = taskDates;
            this.taskIds = taskIds;
        }

        public View getView(int position, View ConvertView, ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) getActivity().getBaseContext().getSystemService(c.LAYOUT_INFLATER_SERVICE);
            View row = inflater.inflate(R.layout.list_item, parent, false);
            TextView taskDescription = (TextView) row.findViewById(R.id.textViewTask);
            TextView taskDate = (TextView) row.findViewById(R.id.textViewTaskDate);

            taskDescription.setText(taskDescriptions[position]);
            taskDate.setText(taskDates[position]);

            SimpleDateFormat formatter = new SimpleDateFormat("MMM dd yyyy");
            // Date dtHighlight = formatter.parse(dates[position]);
            String dtTobeComparedSysDate = formatter.format(new Date());
            String dtToBeCompared = taskDates[position];
            String subCompare = dtToBeCompared.substring(4,
                    dtToBeCompared.length());
            String statusForComparision = taskStatus[position];
            if(selectedIds != null){
                if(selectedIds.contains(taskIds[position])){
                    int color = Color.argb(255, 255, 175, 64);
                    row.setBackgroundColor(color);
                }
            }
            if (subCompare.equals(dtTobeComparedSysDate)) {

                taskDate.setText("Today");
            }
            if(statusForComparision.equals("completed")){
                taskDescription.setPaintFlags(taskDescription.getPaintFlags()| Paint.STRIKE_THRU_TEXT_FLAG);
            }
            return row;
        }
    }
    public class deleteItems extends AsyncTask<Void, Void, Void>{

        ProgressDialog pd;
        protected void onPreExecute() {

            pd = new ProgressDialog(getActivity());
            pd.setTitle("Processing...");
            pd.setMessage("Please wait.");
            pd.setCancelable(false);
            pd.setIndeterminate(true);
            pd.show();
        }
        @Override
        protected Void doInBackground(Void... params) {
            String url = "http://1-dot-macro-gadget-785.appspot.com/deltasks";
            int method = 1;
            tasksTobeDeleted.add(new BasicNameValuePair("tasksToDelete", selectedIds.toString()));
            selectedIds.removeAll(selectedIds);
            selectedDates.removeAll(selectedDates);
            selectedTasks.removeAll(selectedTasks);
            selectedItems.removeAll(selectedItems);
            ServiceHandler sh = new ServiceHandler();
            sh.makeServiceCall(url, method,tasksTobeDeleted);
            return null;
        }
        @Override
        protected void onPostExecute(Void result) {

            if(pd!=null){
                pd.dismiss();
            }
            Toast.makeText(getActivity(), "Task deleted successfully",
                    Toast.LENGTH_LONG).show();

            deleteTasks.setVisibility(View.INVISIBLE);

            Intent intent = new Intent(getActivity(),MainActivity.class);
            startActivity(intent);
        }
    }
}
