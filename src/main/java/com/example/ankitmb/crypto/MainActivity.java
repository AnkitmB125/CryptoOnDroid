package com.example.ankitmb.crypto;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    SwipeRefreshLayout swipeRefreshLayout;
    TextView textView;



    private String TAG = MainActivity.class.getSimpleName();
    private ProgressDialog pDialog;
    private ListView lv;

    private static String url = "https://bittrex.com/api/v1.1/public/getmarketsummaries";

    ArrayList<HashMap<String, String>> currencyList;
    private Object View;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);






        currencyList = new ArrayList<>();
        lv = (ListView) findViewById(R.id.list);

        new GetCurrencies().execute();

    }

    private  class GetCurrencies extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute(){
            super.onPreExecute();
            pDialog = new ProgressDialog(MainActivity.this);
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            pDialog.show();

        }

        @Override
        protected  Void doInBackground(Void... arg0) {
            HttpHandler sh = new HttpHandler();
            String jsonStr = sh.makeServiceCall(url);

            Log.e(TAG, "Response from url: " + jsonStr);
            if (jsonStr != null){
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);
                    JSONArray result = jsonObj.getJSONArray("result");

                    for (int i=0; i < result.length(); i++){
                        if(i==16 || i==43 ||i==62 ||i==63 ||i==97 ||i==110 ||i==118 ||i==170 ||i==186 ||i==188 ||i==202 ||i==208 ||i==211 ||i==219 ||
                                i==224 ||i==226 ||i==245 ||i==246 ||i==248 ||i==251 ||i==250 ||i==251 ||i==252 ||i==253 ||i==254 ||i==255 ||i==256 ||i==257) {
                            JSONObject c = result.getJSONObject(i);

                            String marketname = c.getString("MarketName");
                            String high = c.getString("High");
                            String low = c.getString("Low");
                            String volume = c.getString("Volume");
                            String last = c.getString("Last");
                            String prevday = c.getString("PrevDay");
                            String created = c.getString("Created");

                            HashMap<String, String> currency = new HashMap<>();

                            currency.put("marketname", marketname);
                            currency.put("high", high);
                            currency.put("low", low);
                            currency.put("volume", volume);
                            currency.put("last", last);
                            currency.put("prevday", prevday);
                            currency.put("created", created);

                            currencyList.add(currency);
                        }
                        else
                        {
                            continue;
                        }
                    }
                }catch (final JSONException e) {
                    Log.e(TAG, "Json parsing error: " + e.getMessage());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), "Json parsng error: " + e.getMessage(),
                                    Toast.LENGTH_LONG).show();
                        }
                    });
                }
            } else {
                Log.e(TAG, "Couldn't get json from server.");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(), "Couldn't get json from server. Check LogCat for possible errors!",
                                Toast.LENGTH_LONG).show();
                    }
                });
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            if (pDialog.isShowing())
                pDialog.dismiss();

            ListAdapter adapter = new SimpleAdapter(
                    MainActivity.this, currencyList, R.layout.list_item,
                    new String[]{"marketname","last","volume","high","low","created"}, new int[]{R.id.marketname, R.id.last, R.id.volume,
                    R.id.high, R.id.low, R.id.created}
            );

            lv.setAdapter(adapter);

        }

    }
}