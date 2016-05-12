package fiit.mtaa.library;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Type;

import io.socket.client.Ack;
import io.socket.client.IO;
import io.socket.client.Socket;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class DetailScreen extends AppCompatActivity implements View.OnClickListener {
    private EditText author, title ,literaryForm, year, publisher, paperback, language, price, isbn;
    private ImageButton btn_goback, btn_trash, btn_edit;
    private ImageView image;
    private String objectId;
    private Book book;

    private Bitmap bitmap;
    private ProgressDialog pDialog;
    private int RequestCode = 100;

    private Socket socket;
    private JSONObject returnedJson;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_screen);

        Bundle bundle = getIntent().getExtras();            //nacitanie ID knihy z argumentov
        objectId = bundle.getString("objectId");

        author = (EditText) findViewById(R.id.author);
        title = (EditText) findViewById(R.id.title);
        literaryForm = (EditText) findViewById(R.id.literaryForm);
        year = (EditText) findViewById(R.id.year);
        publisher = (EditText) findViewById(R.id.publisher);
        paperback = (EditText) findViewById(R.id.paperback);
        language = (EditText) findViewById(R.id.language);
        price = (EditText) findViewById(R.id.price);
        isbn = (EditText) findViewById(R.id.isbn);

        btn_trash = (ImageButton) findViewById(R.id.btn_trash);
        btn_trash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(DetailScreen.this)
                        .setTitle("Alert")
                        .setMessage("Do you really want to delete this book?")

                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();

                                if(checkConnection() == 0) {
                                    Intent intent = new Intent();
                                    intent.putExtra("id", objectId);
                                    setResult(RESULT_OK, intent);
                                    finish();
                                }
                                else {
                                    showDialog("Check your internet connection and try again after while!");
                                }
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

        btn_goback = (ImageButton) findViewById(R.id.btn_goback);
        btn_goback.setOnClickListener(this);
        image = (ImageView) findViewById(R.id.image);
        image.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                byte[] bytes = stream.toByteArray();

                Intent intent = new Intent(DetailScreen.this, PictureDetail.class);
                intent.putExtra("image", bytes);
                startActivity(intent);
            }
        });

        btn_edit = (ImageButton) findViewById(R.id.btn_eidt);
        btn_edit.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(DetailScreen.this, EditBook.class);
                intent.putExtra("Book", book);
                intent.putExtra("ID", objectId);
                startActivityForResult(intent, RequestCode);
            }
        });

        new getBookSocket().execute("");
    }

    public class getBookSocket extends AsyncTask<String, Integer, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            int ret = checkConnection();
            if(ret == -1)
                this.cancel(true);

            pDialog = new ProgressDialog(DetailScreen.this);
            pDialog.setMessage("Loading Content ...");
            pDialog.show();
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            pDialog.dismiss();
            showDialog("Check your internet connection and try again after while!");
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
                    js.put("url", "/data/Library1/" + objectId);
                } catch (JSONException e) {
                    e.printStackTrace();
                }



                socket.emit("get", js, new Ack() {
                    @Override
                    public void call(Object... args) {
                        returnedJson = (JSONObject) args[0];

                        try {
                            if (returnedJson.getInt("statusCode") == 200) {
                                final String data = returnedJson.getJSONObject("body").get("data").toString();

                                DetailScreen.this.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        processReply(data);
                                    }
                                });


                            } else if (returnedJson.getInt("statusCode") == 400) {
                                DetailScreen.this.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        showDialog("Bad request syntax!");
                                    }
                                });
                            } else if (returnedJson.getInt("statusCode") == 404) {
                                DetailScreen.this.runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            showDialog("Not Found!");
                                        }
                                });
                            } else if (returnedJson.getInt("statusCode") == 500) {
                                DetailScreen.this.runOnUiThread(new Runnable() {
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == RequestCode && resultCode == RESULT_OK){
            if (data.hasExtra("Json")) {
                processReply((String) data.getExtras().get("Json"));
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_goback:
                Intent intent = new Intent();
                setResult(666, intent);
                finish();

                break;

            default:
                break;
        }
    }

    private void processReply(String jsonString) {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(Book.Author.class, new AuthorDeserializer());
        gsonBuilder.registerTypeAdapter(Book.LiteraryForm.class, new LiteraryFormDeserializer());
        gsonBuilder.registerTypeAdapter(Book.Language.class, new LanguageDeserializer());
        Gson gson = gsonBuilder.create();

        book = gson.fromJson(jsonString, Book.class);      //parsovanie vrateneho JSonu

        author.setText("Author: " + book.getAuthor().toString());             //nastavenie jednotlivych poli
        title.setText("Title: " + book.getTitle());
        literaryForm.setText("Literary form: " + book.getLiteraryForm().toString());
        year.setText("Year: " + Integer.toString(book.getYear()));
        publisher.setText("Publisher: " + book.getPublisher());
        paperback.setText("Paperback: " + Integer.toString(book.getPaperback()));
        language.setText("Language: " + book.getLanguage().toString());
        isbn.setText("ISBN: " + book.getIsbn());
        price.setText("Price (in €): " + Double.toString(book.getPrice()));

        new LoadImage().execute(book.getPicture());         //zavolanie funkcie, kt nacita obrazok v novom vlakne
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

    private class LoadImage extends AsyncTask<String, String, Bitmap> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(DetailScreen.this);
            pDialog.setMessage("Loading Image ...");
            pDialog.show();

        }
        protected Bitmap doInBackground(String... args) {
            try {
                bitmap = BitmapFactory.decodeStream((InputStream)new URL(args[0]).getContent());

            } catch (Exception e) {
                e.printStackTrace();
            }
            return bitmap;
        }

        protected void onPostExecute(Bitmap img) {

            if(img != null){
                image.setClickable(true);
                image.setImageBitmap(img);
                pDialog.dismiss();

            }else{
                image.setClickable(false);
                pDialog.dismiss();
                Toast.makeText(DetailScreen.this, "Image Does Not exist!", Toast.LENGTH_SHORT).show();

            }
        }
    }

    private void showDialog(String message) {
        new AlertDialog.Builder(DetailScreen.this)
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
