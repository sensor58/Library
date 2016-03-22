package fiit.mtaa.library;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;

import okhttp3.*;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class OverviewScreen extends AppCompatActivity {

    private ArrayList<Book> books = new ArrayList<Book>();
    //EditText httptext;
    private ListView listView;

    private ListBooksAdapter listBooksAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_overview_screen);

        listView = (ListView) findViewById(R.id.listBooks);

        new TestMain().execute("nic");
    }

    public class TestMain extends AsyncTask<String, Integer, String> {

        @Override
        protected String doInBackground(String... params) {
            OkHttpClient client = new OkHttpClient();

                Request request = new Request.Builder()
                        .url("https://api.backendless.com/v1/data/Books")
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



        for(int i = 0; i < wrapper.data.length; i++) {
            books.add(wrapper.data[i]);
        }

        listView.setAdapter(new ListBooksAdapter(this, books));
        listView.setOnItemClickListener(new ListClickHandler());

    }

    public class ListClickHandler implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Intent intent = new Intent(OverviewScreen.this, DetailScreen.class);
            startActivity(intent);
        }
    }

    public class Wrapper {
        int offset = 0;
        Book[] data = null;
    }

}
