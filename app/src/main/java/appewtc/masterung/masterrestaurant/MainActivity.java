package appewtc.masterung.masterrestaurant;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

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


public class MainActivity extends ActionBarActivity {

    //Declaring
    private UserTABLE objUserTABLE;
    private FoodTABLE objFoodTABLE;
    private EditText edtUser, edtPassword;
    private String strUserChoose, strPasswordChoose, strPasswordTrue, strName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Initial Widget
        initialWidget();

        //Create or Connected SQLite
        objUserTABLE = new UserTABLE(this);
        objFoodTABLE = new FoodTABLE(this);

        //Test Add New Value
        //testAddValue();

        //Delete All Data
        deleteAllData();

        //Synchronize mySQL to SQLite
        synMySQLtoSQLite();

    }   // Main Method

    //Event Click Login
    public void clickLogin(View view) {

        strUserChoose = edtUser.getText().toString().trim();
        strPasswordChoose = edtPassword.getText().toString().trim();

        if (strUserChoose.equals("") || strPasswordChoose.equals("") ) {

            //Have Space Status
            MyAlertDialog objMyAlertDialog = new MyAlertDialog();
            objMyAlertDialog.myDialog(MainActivity.this, "Have Space", "Please Fill All Every blank");

        } else {

            //Check User
            checkUser();

        }

    }   // clickLogin

    private void checkUser() {
        try {

            String strMyResult[] = objUserTABLE.searchUser(strUserChoose);
            strPasswordTrue = strMyResult[2];
            strName = strMyResult[3];

            Log.d("rest", "Welcome " + strMyResult[3]);

            //Check Password
            checkPassword();

        } catch (Exception e) {

            //No This User Status
            MyAlertDialog objMyAlertDialog = new MyAlertDialog();
            objMyAlertDialog.myDialog(MainActivity.this, "No This User", "No " + strUserChoose + " in my Database");

        }
    }

    private void checkPassword() {
        if (strPasswordChoose.equals(strPasswordTrue)) {

            welcomeOfficer();

        } else {
            MyAlertDialog objMyAlertDialog = new MyAlertDialog();
            objMyAlertDialog.myDialog(MainActivity.this, "Password False", "Please Try Again Password False");
        }
    }

    private void welcomeOfficer() {

        AlertDialog.Builder objBuilder = new AlertDialog.Builder(this);
        objBuilder.setIcon(R.drawable.restaurant);
        objBuilder.setTitle("ยินดีต้อนรับ");
        objBuilder.setMessage("ยินดีต้อนรับ คุณ" + strName + "\n" + "สู่ระบบของเรา");
        objBuilder.setCancelable(false);
        objBuilder.setPositiveButton("ตกลง", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                //Intent to OrderActivity
                Intent objIntent = new Intent(MainActivity.this, OrderActivity.class);
                objIntent.putExtra("Officer", strName);
                startActivity(objIntent);
                dialogInterface.dismiss();
                deleteAllData();
                finish();
            }
        });
        objBuilder.setNegativeButton("ไม่ตกลง", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                edtUser.setText("");
                edtPassword.setText("");
                dialogInterface.dismiss();
            }
        });
        objBuilder.show();
    }

    private void initialWidget() {
        edtUser = (EditText) findViewById(R.id.editText);
        edtPassword = (EditText) findViewById(R.id.editText2);
    }

    private void deleteAllData() {

        SQLiteDatabase objSqLiteDatabase = openOrCreateDatabase("Restaurant.db", MODE_PRIVATE, null);
        objSqLiteDatabase.delete("userTABLE", null, null);
        objSqLiteDatabase.delete("foodTABLE", null, null);

    }

    private void synMySQLtoSQLite() {

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
            HttpPost objHttpPost = new HttpPost("http://swiftcodingthai.com/rest/php_get_data_master.php");
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
                String strUser = objJSONObject.getString("User");
                String strPassword = objJSONObject.getString("Password");
                String strName = objJSONObject.getString("Name");
                objUserTABLE.addNewDataUser(strUser, strPassword, strName);

            }

        } catch (Exception e) {
            Log.d("rest", "Error Update ==> " + e.toString());
        }


    }   // synMySQLtoSQLite

    private void testAddValue() {
        objUserTABLE.addNewDataUser("testUser", "testPass", "testName");
        objFoodTABLE.addNewDataFood("testFood", 123.0);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
