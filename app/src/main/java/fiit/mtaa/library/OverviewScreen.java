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

import okhttp3.*;

import java.io.IOException;
import java.util.ArrayList;

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
            } catch (IOException e) {
                e.printStackTrace();
            }

            return "";
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            httptext.append(result);
        }
    }

    private void showAllBooks() {
        OkHttpClient client = new OkHttpClient();

        Request httpGet = new Request.Builder()
                .url("https://api.backendless.com/v1/data/Books")
                .header("application-id", "36E0E8DE-E56C-9A69-FFE7-9CE128693F00")
                .addHeader("secret-key", "B1E5E7AC-907F-5A89-FFBB-AC7482E0E600")
                .build();


        //ResponseHandler<String> responseHandler = new BasicResponseHandler();

        try {
            Response response = client.newCall(httpGet).execute();
            httptext.append(response.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
