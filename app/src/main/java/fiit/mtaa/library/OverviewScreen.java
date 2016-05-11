package fiit.mtaa.library;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import io.socket.client.Ack;
import io.socket.client.IO;
import io.socket.client.Socket;
import okhttp3.*;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class OverviewScreen extends AppCompatActivity {

    private ArrayList<Book> books = new ArrayList<Book>();
    private ProgressDialog pDialog;
    private ListView listView;

    private ListBooksAdapter listBooksAdapter;
    private TextView btn_logout;
    private ImageButton btn_refresh;
    private ImageButton btn_add;
    private int RequestCode = 100;
    private Book book;
    private Socket socket;
    private JSONObject returnedJson;
    private String jsonString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_overview_screen);

        btn_logout = (TextView) findViewById(R.id.btn_logout);
        btn_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(OverviewScreen.this)
                        .setTitle("Alert")
                        .setMessage("Do you really want to logout?")

                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                                finish();
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        })
                        .show();
            }
        });

        btn_refresh = (ImageButton) findViewById(R.id.btn_refresh);
        btn_refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkConnection() == 0) {
                    Intent intent = getIntent();
                    finish();
                    startActivity(intent);
                }
                else {
                    showDialog("Check your internet connection and try again after while!");
                }
            }
        });

        btn_add = (ImageButton) findViewById(R.id.btn_add);
        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(OverviewScreen.this, NewBook.class);
                startActivity(intent);
            }
        });

        listView = (ListView) findViewById(R.id.listBooks);

        pDialog = new ProgressDialog(OverviewScreen.this);
        pDialog.setMessage("Loading Content ...");
        pDialog.show();

       // new getAllBooks().execute("");
        new getAllBooksSocket().execute("");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == RequestCode && resultCode == RESULT_OK){
            deleteBook((Book) data.getExtras().get("Book"));
        }
    }

    public class getAllBooksSocket extends AsyncTask<String, Integer, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            int ret = checkConnection();
            if(ret == -1)
                this.cancel(true);
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            pDialog.dismiss();
            showDialog("Check your internet connection and try to refresh after while.");
        }

        @Override
        protected String doInBackground(String... params) {
            if(!this.isCancelled()) {
                IO.Options opts = new IO.Options();
                opts.secure = false;
                opts.port = 1341;
                opts.reconnection = true;
                opts.forceNew = true;
                opts.timeout = 5000;

                try {
                    socket = IO.socket("http://sandbox.touch4it.com:1341/?__sails_io_sdk_version=0.12.1", opts);
                    socket.connect();
                } catch (URISyntaxException e) {
                    e.printStackTrace();
                }

                JSONObject js = new JSONObject();
                try {
                    js.put("url", "/data/Library1");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                socket.emit("get", js, new Ack() {
                    @Override
                    public void call(Object... args) {
                        returnedJson = (JSONObject) args[0];

                        try {
                            if (returnedJson.getInt("statusCode") == 200) {
                                JSONObject jo = (JSONObject) returnedJson.get("body");
                                JSONArray ja = (JSONArray) jo.get("data");

                                parseJson(ja);

                                OverviewScreen.this.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        updateView();
                                    }
                                });

                            } else if (returnedJson.getInt("statusCode") == 400) {
                                OverviewScreen.this.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        showDialog("Bad request syntax!");
                                    }
                                });
                            } else if (returnedJson.getInt("statusCode") == 500) {
                                OverviewScreen.this.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        showDialog("Server error!");
                                    }
                                });
                            }
                        } catch (JSONException e1) {
                            e1.printStackTrace();
                        }
                    }
                });
            }

            return "";
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            if(result != null) {
                pDialog.dismiss();
            }
        }
    }

    private void updateView() {
        listView.setAdapter(new ListBooksAdapter(this, books));
        listView.setOnItemClickListener(new ListClickHandler());
    }

    private void parseJson(JSONArray jsonArray) {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(Book.Author.class, new AuthorDeserializer());
        gsonBuilder.registerTypeAdapter(Book.LiteraryForm.class, new LiteraryFormDeserializer());
        gsonBuilder.registerTypeAdapter(Book.Language.class, new LanguageDeserializer());
        Gson gson = gsonBuilder.create();

        for(int i=0; i < jsonArray.length(); i++) {
            Book newBook = new Book();
            try {
                newBook = gson.fromJson(jsonArray.getJSONObject(i).get("data").toString(), Book.class);
                newBook.setObjectId(jsonArray.getJSONObject(i).get("id").toString());
                books.add(newBook);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
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

            startActivityForResult(intent, RequestCode);
        }
    }

    public void deleteBook(Book bookToDelete) {
        book = bookToDelete;

        if(checkConnection() == 0) {
            new HttpDeleteBook().execute("");

            Intent intent = getIntent();
            finish();
            startActivity(intent);
        }
        else {
            showDialog("Check your internet connection and try again after while!");
        }
    }

    public class HttpDeleteBook extends AsyncTask<String, Integer, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            int ret = checkConnection();
            if(ret == -1)
                this.cancel(true);
        }

        @Override
        protected String doInBackground(String... params) {
            OkHttpClient client = new OkHttpClient();
            StringBuilder sb = new StringBuilder();
            sb.append("https://api.backendless.com/v1/data/Books");   //base url
            sb.append("/");
            if(book != null) {
                sb.append(book.getObjectId());
            }

            String url = sb.toString();

            if(!this.isCancelled()) {
                Request request = new Request.Builder()
                        .url(url)
                        .header("application-id", "36E0E8DE-E56C-9A69-FFE7-9CE128693F00")
                        .addHeader("secret-key", "B1E5E7AC-907F-5A89-FFBB-AC7482E0E600")
                        .delete()
                        .build();

                Response response = null;

                try {
                    response = client.newCall(request).execute();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if(response != null) {
                    try {
                        switch (response.code()) {
                            case 200: {
                                break;
                            }

                            case 400: {
                                showDialog("Bad request syntax!");
                                return "";
                            }

                            case 404: {
                                showDialog("Requested books not found!");
                                return "";
                            }
                        };

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                else {
                    this.cancel(true);
                }
            }
            return null;

        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

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
        new AlertDialog.Builder(OverviewScreen.this)
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

    public void showAlertDialog(final Book bookToDelete) {
        new AlertDialog.Builder(OverviewScreen.this)
                .setTitle("Alert")
                .setMessage("Do you really want to delete this book?")

                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        deleteBook(bookToDelete);
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                })
                .show();
    }

    public int checkConnection() {
        ConnectivityManager connectivityManager = (ConnectivityManager)
                this.getSystemService(this.CONNECTIVITY_SERVICE);

        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo == null) {
            return -1;
        }
        else {
            return 0;
        }
    }
}
