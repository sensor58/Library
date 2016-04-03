package fiit.mtaa.library;

import android.content.DialogInterface;
import android.content.Intent;
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

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.InputStream;
import java.net.URL;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.Toast;

public class DetailScreen extends AppCompatActivity implements View.OnClickListener {
    private EditText author, title ,literaryForm, year, publisher, paperback, language, price, isbn;
    private ImageButton btn_goback;
    private ImageView image;
    private String objectId;

    private Bitmap bitmap;
    private ProgressDialog pDialog;


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

        new HttpGetBook().execute("");
    }

    public class HttpGetBook extends AsyncTask<String, Integer, String> {



        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(DetailScreen.this);
            pDialog.setMessage("Loading Content ...");
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... params) {
            OkHttpClient client = new OkHttpClient();
            StringBuilder sb = new StringBuilder();
            sb.append("https://api.backendless.com/v1/data/Books");   //base url
            sb.append("/");                                          //lomka
            sb.append(objectId);                                     //ID knihy ktoru chcem
            String url = sb.toString();

            Log.i("Error", url);


            Request request = new Request.Builder()
                    .url(url)
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
            pDialog.dismiss();

            processReply(result);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_goback:
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

        Book book = gson.fromJson(jsonString, Book.class);      //parsovanie vrateneho JSonu

        author.append(book.getAuthor().toString());             //nastavenie jednotlivych poli
        title.append(book.getTitle());
        literaryForm.append(book.getLiteraryForm().toString());
        year.append(Integer.toString(book.getYear()));
        publisher.append(book.getPublisher());
        paperback.append(Integer.toString(book.getPaperback()));
        language.append(book.getLanguage().toString());
        isbn.append(book.getIsbn());
        price.append(Double.toString(book.getPrice()));

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
                image.setImageBitmap(img);
                pDialog.dismiss();

            }else{

                pDialog.dismiss();
                Toast.makeText(DetailScreen.this, "Image Does Not exist or Network Error", Toast.LENGTH_SHORT).show();

            }
        }
    }

}
