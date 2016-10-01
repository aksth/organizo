package com.swifeen.organizo;


import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.internal.view.ContextThemeWrapper;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class ChecklistsFragment extends Fragment {

    protected TaskerDbHelper db;
    List<Task> list;
    MyAdapter adapt;

    private static final int DELETE_ID = Menu.FIRST + 1;

    public ChecklistsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_checklists, container, false);
        ListView listTask = (ListView) view.findViewById(R.id.listView1);

        registerForContextMenu(listTask);

        //listeners for button
        //============ button part ======
        FloatingActionButton fab;
        fab = (FloatingActionButton)view.findViewById(R.id.button_add_checklists);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(getActivity(), "Hello World", Toast.LENGTH_SHORT).show();
                addTaskNow(v);
            }
        });
        //========= end button ==========

        //========we have to set empty view for ListView manually, since we're using fragments==========
        TextView emptyText = (TextView)view.findViewById(android.R.id.empty);
        listTask.setEmptyView(emptyText);
        //=====================================

        db = new TaskerDbHelper(getActivity());
        list = db.getAllTasks();
        adapt = new MyAdapter(getActivity(), R.layout.checklists_inner_view, list);

        listTask.setAdapter(adapt);

        // Inflate the layout for this fragment
        return view;
    }

    private class MyAdapter extends ArrayAdapter<Task> {

        Context context;
        List<Task> taskList = new ArrayList<Task>();
        int layoutResourceId;

        public MyAdapter(Context context, int layoutResourceId,
                         List<Task> objects) {
            super(context, layoutResourceId, objects);
            this.layoutResourceId = layoutResourceId;
            this.taskList = objects;
            this.context = context;
        }

        /**
         * This method will DEFINe what the view inside the list view will
         * finally look like Here we are going to code that the checkbox state
         * is the status of task and check box text is the task name
         */
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            CheckBox chk = null;
            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) context
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.checklists_inner_view,
                        parent, false);
                chk = (CheckBox) convertView.findViewById(R.id.chkStatus);
                convertView.setTag(chk);

                chk.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        CheckBox cb = (CheckBox) v;
                        Task changeTask = (Task) cb.getTag();

                        changeTask.setStatus(cb.isChecked() == true ? 1 : 0);
                        db.updateTask(changeTask);

                        /*
                        Toast.makeText(
                                getActivity().getApplicationContext(),
                                "Clicked on Checkbox: " + cb.getText() + " is "
                                        + cb.isChecked(), Toast.LENGTH_LONG)
                                .show();
                        */

                        String cbox;

                        if(cb.isChecked()) {
                            cbox = "Checked!";
                        }
                        else {
                            cbox = "Unchecked!";
                        }

                        Toast.makeText(
                                getActivity().getApplicationContext(),
                                cbox, Toast.LENGTH_LONG)
                                .show();
                    }
                });
            } else {
                chk = (CheckBox) convertView.getTag();
            }
            Task current = taskList.get(position);

            // globally
            TextView textView = (TextView)convertView.findViewById(R.id.taskname);
            //in your OnCreate() method
            textView.setText(current.getTaskName());
            //chk.setText(current.getTaskName());

            chk.setChecked(current.getStatus() == 1 ? true : false);
            chk.setTag(current);
            Log.d("listener", String.valueOf(current.getId()));
            return convertView;
        }

    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.add(0, DELETE_ID, 0, R.string.menu_todo_delete);

    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch(item.getItemId()) {

            case DELETE_ID:
                AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();

                db.deleteTask(info.id);

                //refresh view
                View view = getActivity().findViewById(R.id.listView1);
                ListView listTask = (ListView) view.findViewById(R.id.listView1);
                db = new TaskerDbHelper(getActivity());
                list = db.getAllTasks();
                adapt = new MyAdapter(getActivity(), R.layout.checklists_inner_view, list);
                listTask.setAdapter(adapt);
                //end of refresh

                return true;
        }

        return super.onContextItemSelected(item);
    }

    public void addTaskNow(View v) {

        AlertDialog.Builder todoTaskBuilder = new AlertDialog.Builder(getActivity());
        todoTaskBuilder.setTitle("Add Todo Task Item");
        todoTaskBuilder.setMessage("describe the Todo task...");
        final EditText todoET = new EditText(getActivity());
        todoTaskBuilder.setView(todoET);
        todoTaskBuilder.setPositiveButton("Add Task", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String todoTaskInput = todoET.getText().toString();

                Task task = new Task(todoTaskInput, 0);
                db.addTask(task);
                Log.d("tasker", "data added");
                adapt.add(task);
                adapt.notifyDataSetChanged();
            }
        });

        todoTaskBuilder.setNegativeButton("Cancel", null);

        todoTaskBuilder.create().show();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
}
