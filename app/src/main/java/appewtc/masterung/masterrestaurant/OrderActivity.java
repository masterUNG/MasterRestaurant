package appewtc.masterung.masterrestaurant;

import android.os.Build;
import android.os.StrictMode;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;


public class OrderActivity extends ActionBarActivity {

    //Explicit
    private TextView txtShowOfficer;
    private Spinner mySpinner;
    private ListView myListView;
    private String strOfficer, strDesk, strFood, strItem;
    private FoodTABLE objFoodTABLE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);

        objFoodTABLE = new FoodTABLE(this);

        //Initial Widget
        initialWidget();

        //Show Officer
        showOfficer();

        //Show Desk
        showDesk();

        //Synchronize JSON to SQLite
        synchronizeJSONtoSQLite();

    }   // onCreate

    private void synchronizeJSONtoSQLite() {

        //setup policy
        if (Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy myPolicy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(myPolicy);
        }


        InputStream objInputStream = null;
        String strJSON = "";

        // Create InputStream
        try {

            HttpClient objHttpClient = new DefaultHttpClient();
            HttpPost objHttpPost = new HttpPost("http://swiftcodingthai.com/rest/php_get_data_food.php");
            HttpResponse objHttpResponse = objHttpClient.execute(objHttpPost);
            HttpEntity objHttpEntity = objHttpResponse.getEntity();
            objInputStream = objHttpEntity.getContent();

        } catch (Exception e) {
            Log.d("rest", "Error InputStream ==> " + e.toString());
        }


        //Create JSON String
        try {

            BufferedReader objBufferedReader = new BufferedReader(new InputStreamReader(objInputStream, "UTF-8"));
            StringBuilder objStringBuilder = new StringBuilder();
            String strLine = null;

            while ((strLine = objBufferedReader.readLine()) != null ) {
                objStringBuilder.append(strLine);
            }

            objInputStream.close();
            strJSON = objStringBuilder.toString();

        } catch (Exception e) {
            Log.d("rest", "Error Create String ==> " + e.toString());
        }


        //Updata JSON to SQLite
        try {

            final JSONArray objJsonArray = new JSONArray(strJSON);
            for (int i = 0; i < objJsonArray.length(); i++) {

                JSONObject objJSONObject = objJsonArray.getJSONObject(i);
                String strFood = objJSONObject.getString("Food");
                String strPrice = objJSONObject.getString("Price");
                Double douPrice = Double.parseDouble(strPrice);
                objFoodTABLE.addNewDataFood(strFood, douPrice);


            }

        } catch (Exception e) {
            Log.d("rest", "Error Update ==> " + e.toString());
        }



    }   // Synchronize

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
