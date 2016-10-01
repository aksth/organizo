package com.swifeen.organizo;


import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;


/**
 * A simple {@link Fragment} subclass.
 */
public class RemindersFragment extends Fragment implements AdapterView.OnItemClickListener{

    private static final int ACTIVITY_CREATE=0;
    private static final int ACTIVITY_EDIT=1;
    private static final int DELETE_ID = Menu.FIRST + 1;

    private RemindersDbAdapter mDbHelper;

    public RemindersFragment() {
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

        mDbHelper = new RemindersDbAdapter(getActivity());
        mDbHelper.open();

        View view = inflater.inflate(R.layout.fragment_reminders, container, false);
        ListView remindersList = (ListView)view.findViewById(android.R.id.list);


        registerForContextMenu(remindersList);
        remindersList.setOnItemClickListener(this);

        //========we have to set empty view for ListView manually, since we're using fragments==========
        TextView emptyText = (TextView)view.findViewById(android.R.id.empty);
        remindersList.setEmptyView(emptyText);

        //============ button part ======
        FloatingActionButton fab;
        fab = (FloatingActionButton)view.findViewById(R.id.button_add_reminders);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(getActivity(), "Hello World", Toast.LENGTH_SHORT).show();
                createReminder();
            }
        });
        //===================

        Cursor remindersCursor = mDbHelper.fetchAllReminders();
        getActivity().startManagingCursor(remindersCursor);

        // Create an array to specify the fields we want to display in the list (only TITLE)
        String[] from = new String[]{RemindersDbAdapter.KEY_TITLE};

        // and an array of the fields we want to bind those fields to (in this case just text1)
        int[] to = new int[]{R.id.text1};

        // Now create a simple cursor adapter and set it to display
        SimpleCursorAdapter reminders =
                new SimpleCursorAdapter(getActivity(), R.layout.reminder_row, remindersCursor, from, to);
        remindersList.setAdapter(reminders);

        // Inflate the layout for this fragment
        return view;
    }

    private void createReminder() {
        Intent i = new Intent(getActivity(), ReminderEditActivity.class);
        startActivityForResult(i, ACTIVITY_CREATE);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.add(0, DELETE_ID, 0, R.string.menu_delete_reminder);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case DELETE_ID:

                AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
                mDbHelper.deleteReminder(info.id);

                //fillData();

                View view = getActivity().findViewById(android.R.id.list);
                ListView remindersList = (ListView)view.findViewById(android.R.id.list);

                Cursor remindersCursor = mDbHelper.fetchAllReminders();
                getActivity().startManagingCursor(remindersCursor);

                // Create an array to specify the fields we want to display in the list (only TITLE)
                String[] from = new String[]{RemindersDbAdapter.KEY_TITLE};

                // and an array of the fields we want to bind those fields to (in this case just text1)
                int[] to = new int[]{R.id.text1};

                // Now create a simple cursor adapter and set it to display
                SimpleCursorAdapter reminders =
                        new SimpleCursorAdapter(getActivity(), R.layout.reminder_row, remindersCursor, from, to);
                remindersList.setAdapter(reminders);

                return true;
        }
        return super.onContextItemSelected(item);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view,
                            int position, long id) {

        //super.onListItemClick(parent, view, position, id);

        Intent i = new Intent(getActivity(), NoteEdit.class);

        i.putExtra(RemindersDbAdapter.KEY_ROWID, id);
        startActivityForResult(i, ACTIVITY_EDIT);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        View view = getActivity().findViewById(android.R.id.list);
        ListView remindersList = (ListView)view.findViewById(android.R.id.list);

        Cursor remindersCursor = mDbHelper.fetchAllReminders();
        getActivity().startManagingCursor(remindersCursor);

        // Create an array to specify the fields we want to display in the list (only TITLE)
        String[] from = new String[]{RemindersDbAdapter.KEY_TITLE};

        // and an array of the fields we want to bind those fields to (in this case just text1)
        int[] to = new int[]{R.id.text1};

        // Now create a simple cursor adapter and set it to display
        SimpleCursorAdapter reminders =
                new SimpleCursorAdapter(getActivity(), R.layout.reminder_row, remindersCursor, from, to);
        remindersList.setAdapter(reminders);

    }

    //==============================================

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
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
