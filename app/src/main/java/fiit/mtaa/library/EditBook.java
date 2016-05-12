package fiit.mtaa.library;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import io.socket.client.Ack;
import io.socket.client.IO;
import io.socket.client.Socket;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okio.BufferedSink;

public class EditBook extends AppCompatActivity implements View.OnClickListener {

    private Book book;
    private ImageButton btn_goback4, btn_check;
    private EditText title, year, publisher, paperback, price, isbn, imageUrl;
    private Spinner author, literaryForm, language;

    private Bitmap bitmap;

    private Socket socket;

    private ProgressDialog pDialog;
    private String objectId;

    private Book.Author selectedAuthor;
    private Book.Language selectedLanguage;
    private Book.LiteraryForm selectedLiteraryForm;

    private String json;

    private String madeJson;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_book);

        book = (Book) getIntent().getExtras().get("Book");
        objectId = (String) getIntent().getExtras().get("ID");

        selectedAuthor = book.getAuthor();
        selectedLanguage = book.getLanguage();
        selectedLiteraryForm = book.getLiteraryForm();

        title = (EditText) findViewById(R.id.title);
        year = (EditText) findViewById(R.id.year);
        publisher = (EditText) findViewById(R.id.publisher);
        paperback = (EditText) findViewById(R.id.paperback);
        price = (EditText) findViewById(R.id.price);
        isbn = (EditText) findViewById(R.id.isbn);
        imageUrl = (EditText) findViewById(R.id.imageURL);

        author = (Spinner) findViewById(R.id.author);
        literaryForm = (Spinner) findViewById(R.id.literaryForm);
        language = (Spinner) findViewById(R.id.language);

        btn_goback4 = (ImageButton) findViewById(R.id.btn_goback4);
        btn_goback4.setOnClickListener(this);

        btn_check = (ImageButton) findViewById(R.id.btn_check);
        btn_check.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(checkConnection() == 0) {
                    if (checkInputFields() && tryMakeJson())
                        new HttpEditBook().execute("");
                }
                else {
                    showDialog("Check your internet connection and try again after while!");
                }
            }
        });

        showDeatails();
    }

    private boolean checkInputFields() {
        String input;

        input = year.getText().toString();
        try {
            int n = Integer.parseInt(input);
        }
        catch (NumberFormatException e) {
            EditBook.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    showDialog("Wrong input format in field: Year");
                }
            });
            return false;
        }

        input = paperback.getText().toString();
        try {
            int n = Integer.parseInt(input);
        }
        catch (NumberFormatException e) {
            EditBook.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    showDialog("Wrong input format in field: Paperback");
                }
            });
            return false;
        }

        input = price.getText().toString();
        try {
            double n = Double.parseDouble(input);
        }
        catch (NumberFormatException e) {
            EditBook.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    showDialog("Wrong input format in field: Price");
                }
            });
            return false;
        }

        input = isbn.getText().toString();
        try {
            long n = Long.parseLong(input);
        }
        catch (NumberFormatException e) {
            EditBook.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    showDialog("Wrong input format in field: ISBN");
                }
            });
            return false;
        }

        return true;

    }

    private boolean tryMakeJson() {
        StringBuilder sb = new StringBuilder();
        sb.append("{\n");
        sb.append("      \"year\": " + year.getText().toString() + ",");

        sb.append("      \"isbn\": \"" + isbn.getText().toString() + "\",");

        sb.append("      \"price\": " + price.getText().toString() + ",");

        sb.append("      \"paperback\": " + paperback.getText().toString() + ",");

        sb.append("      \"publisher\": \"" + publisher.getText().toString() + "\",");

        sb.append("      \"title\": \"" + title.getText().toString() + "\",");

        sb.append("      \"author\": " + selectedAuthor.getValue() + ",");

        sb.append("      \"language\": " + selectedLanguage.getValue() + ",");

        sb.append("      \"literaryForm\": " + selectedLiteraryForm.getValue() + ",");

        sb.append("      \"picture\": \"" + imageUrl.getText().toString() + "\"");

        sb.append("}");

        madeJson = sb.toString();

        return true;
    }

    private void showDeatails() {
        title.append(book.getTitle());                      //nastavenie jednotlivych poli
        year.append(Integer.toString(book.getYear()));
        publisher.append(book.getPublisher());
        paperback.append(Integer.toString(book.getPaperback()));
        isbn.append(book.getIsbn());
        price.append(Double.toString(book.getPrice()));
        imageUrl.append(book.getPicture());

        //AUTHOR

        author.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedAuthor = Book.Author.values()[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        ArrayList<String> authors = new ArrayList<>();

        for (int i = 0; i < Book.Author.values().length; i++) {
             authors.add(Book.Author.values()[i].toString());
        }

      //  ArrayAdapter<String> authorsAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, authors);
        ArrayAdapter<String> authorsAdapter = new ArrayAdapter<String>(this, R.layout.spinner_layout, authors);
        authorsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        author.setAdapter(authorsAdapter);
        author.setSelection(selectedAuthor.getValue()-1);

        //LANGUAGE

        language.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedLanguage = Book.Language.values()[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        ArrayList<String> languages = new ArrayList<>();

        for (int i = 0; i < Book.Language.values().length; i++) {
            languages.add(Book.Language.values()[i].toString());
        }

        ArrayAdapter<String> languagesAdapter = new ArrayAdapter<String>(this, R.layout.spinner_layout, languages);
        languagesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        language.setAdapter(languagesAdapter);
        language.setSelection(selectedLanguage.getValue()-1);

        //LITERARY FORM

        literaryForm.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedLiteraryForm = Book.LiteraryForm.values()[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        ArrayList<String> literaryForms = new ArrayList<>();

        for (int i = 0; i < Book.LiteraryForm.values().length; i++) {
            literaryForms.add(Book.LiteraryForm.values()[i].toString());
        }

        ArrayAdapter<String> literaryFormAdapter = new ArrayAdapter<String>(this, R.layout.spinner_layout, literaryForms);
        literaryFormAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        literaryForm.setAdapter(literaryFormAdapter);
        literaryForm.setSelection(selectedLiteraryForm.getValue()-1);
    }

    public class HttpEditBook extends AsyncTask<String, Integer, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(EditBook.this);
            pDialog.setMessage("Processing ...");
            pDialog.show();

            int ret = checkConnection();
            if(ret == -1)
                this.cancel(true);
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            pDialog.dismiss();
            new AlertDialog.Builder(EditBook.this)
                    .setCancelable(false)
                    .setTitle("Error")
                    .setMessage("Check your internet connection and try again after while!")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                            finish();
                        }
                    }).show();
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

                String json = madeJson;

                JSONObject js = new JSONObject();
                try {
                    js.put("url", "/data/Library1/" + objectId);
                    js.put("data", new JSONObject().put("data", new JSONObject(json)));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                socket.emit("put", js, new Ack() {
                    @Override
                    public void call(Object... args) {}
                });
            }
            return "";
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            pDialog.dismiss();

            Intent intent = new Intent();
            intent.putExtra("Json", madeJson);
            setResult(RESULT_OK, intent);
            finish();
        }
    }

    private void showDialog(String message) {
        new AlertDialog.Builder(EditBook.this)
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_goback4:
                Intent intent = new Intent();
                setResult(RESULT_OK, intent);
                finish();
                break;


            default:
                break;
        }
    }
}
