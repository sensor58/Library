package fiit.mtaa.library;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
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
    private ProgressDialog pDialog;
    private ListView listView;

    private ListBooksAdapter listBooksAdapter;
    private Button btn_logout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_overview_screen);

        btn_logout = (Button) findViewById(R.id.btn_logout);
        btn_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        listView = (ListView) findViewById(R.id.listBooks);

        pDialog = new ProgressDialog(OverviewScreen.this);
        pDialog.setMessage("Loading Content ...");
        pDialog.show();

        new TestMain().execute("");
    }

    public class TestMain extends AsyncTask<String, Integer, String> {

        @Override
        protected String doInBackground(String... params) {
            OkHttpClient client = new OkHttpClient();

                Request request = new Request.Builder()
                        .url("https://api.backendless.com/v1/data/Books?props=author%2Ctitle%2CobjectId")
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

                switch (response.code()) {
                    case 200: {
                        return response.body().string();
                    }

                    case 204: {
                        showDialog("No content to show!");
                        break;
                    }

                    case 400: {
                        showDialog("Bad request syntax!");
                        break;
                    }

                    case 404: {
                        showDialog("404 Not found!");
                        break;
                    }
                };

            } catch (Exception e) {
                e.printStackTrace();
            }

            return "";
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            pDialog.dismiss();

            parseJson(result);
        }
    }

    private void parseJson(String jsonString) {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(Book.Author.class, new AuthorDeserializer());
        gsonBuilder.registerTypeAdapter(Book.LiteraryForm.class, new LiteraryFormDeserializer());
        gsonBuilder.registerTypeAdapter(Book.Language.class, new LanguageDeserializer());
        Gson gson = gsonBuilder.create();
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
            Bundle bundle = new Bundle();

            Book selectedBook = (Book) parent.getItemAtPosition(position);          //ziskanie objektu vybranej knihy
            String objectId = selectedBook.getObjectId();                           //zistenie ID knihy

            bundle.putString("objectId", objectId);               //pridanie ID ako argumentu
            intent.putExtras(bundle);

            startActivity(intent);
        }
    }

    public class AuthorDeserializer implements JsonDeserializer<Book.Author> {
        @Override
        public Book.Author deserialize(JsonElement element, Type arg1, JsonDeserializationContext arg2) throws JsonParseException {
            int value = element.getAsInt();
            return Book.Author.fromValue(value);
        }
    }

    public class LiteraryFormDeserializer implements JsonDeserializer<Book.LiteraryForm> {
        @Override
        public Book.LiteraryForm deserialize(JsonElement element, Type arg1, JsonDeserializationContext arg2) throws JsonParseException {
            int value = element.getAsInt();
            return Book.LiteraryForm.fromValue(value);
        }
    }

    public class LanguageDeserializer implements JsonDeserializer<Book.Language> {
        @Override
        public Book.Language deserialize(JsonElement element, Type arg1, JsonDeserializationContext arg2) throws JsonParseException {
            int value = element.getAsInt();
            return Book.Language.fromValue(value);
        }
    }

    public class Wrapper {
        int offset = 0;
        Book[] data = null;
    }

    private void showDialog(String message) {
        new AlertDialog.Builder(this)
                .setCancelable(false)
                .setTitle("Error")
                .setMessage(message)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                }).show();
    }
}
