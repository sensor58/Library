package fiit.mtaa.library;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;

import okhttp3.*;

import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

public class OverviewScreen extends AppCompatActivity {

    private ListView listView;
    private ArrayList<String> dataItems = new ArrayList<String>();
    EditText httptext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_overview_screen);
        httptext = (EditText) findViewById(R.id.Texthttp);

        new TestMain().execute("nic");
    }

    public class TestMain extends AsyncTask<String, Integer, String> {

        @Override
        protected String doInBackground(String... params) {
            OkHttpClient client = new OkHttpClient();

                Request request = new Request.Builder()
                        .url("https://api.backendless.com/v1/data/Books")
                    //    .url("https://api.backendless.com/v1/data/Books/2D36510A-45A0-9EB0-FF86-F71D935ADC00")
                        .header("application-id", "36E0E8DE-E56C-9A69-FFE7-9CE128693F00")
                        .addHeader("secret-key", "B1E5E7AC-907F-5A89-FFBB-AC7482E0E600")
                        .build();

            Response response = null;
            try {
                response = client.newCall(request).execute();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {

                return response.body().string();
            } catch (Exception e) {
                e.printStackTrace();
            }

            return "";
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            parseJson(result);
        }
    }

    private void parseJson(String jsonString) {
        Gson gson = new Gson();

        Wrapper wrapper = gson.fromJson(jsonString, Wrapper.class);

        httptext.append(wrapper.data[0].getTitle());
       // httptext.append(book.getTitle());
    }

    public class Wrapper {
        int offset = 0;
        Book[] data = null;
    }

}
