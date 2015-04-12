package appewtc.masterung.masterrestaurant;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;


public class OrderActivity extends ActionBarActivity {

    //Explicit
    private TextView txtShowOfficer;
    private Spinner mySpinner;
    private ListView myListView;
    private String strOfficer, strDesk, strFood, strItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);

        //Initial Widget
        initialWidget();

        //Show Officer
        showOfficer();

        //Show Desk
        showDesk();

    }   // onCreate

    private void showDesk() {
        final String strSpinnerDesk[] = getResources().getStringArray(R.array.desk);
        ArrayAdapter<String> objArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, strSpinnerDesk);
        mySpinner.setAdapter(objArrayAdapter);

        mySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                strDesk = strSpinnerDesk[i];
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                strDesk = strSpinnerDesk[0];
            }
        });

    }

    private void showOfficer() {
        strOfficer = getIntent().getExtras().getString("Officer");
        txtShowOfficer.setText(strOfficer);
    }

    private void initialWidget() {
        txtShowOfficer = (TextView) findViewById(R.id.txtShowofficer);
        mySpinner = (Spinner) findViewById(R.id.spinner);
        myListView = (ListView) findViewById(R.id.listView);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_order, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}   // Main Class
