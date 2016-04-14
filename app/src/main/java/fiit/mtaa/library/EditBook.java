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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okio.BufferedSink;

public class EditBook extends AppCompatActivity implements View.OnClickListener {

    private Book book;
    private ImageButton btn_goback, btn_check;
    private EditText title, year, publisher, paperback, price, isbn, imageUrl;
    private Spinner author, literaryForm, language;

    private Bitmap bitmap;

    private ProgressDialog pDialog;
    private String objectId;

    private Book.Author selectedAuthor;
    private Book.Language selectedLanguage;
    private Book.LiteraryForm selectedLiteraryForm;

    private String json;

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

        btn_goback = (ImageButton) findViewById(R.id.btn_goback4);
        btn_goback.setOnClickListener(this);

        btn_check = (ImageButton) findViewById(R.id.btn_check);
        btn_goback.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (tryMakeJson()) new HttpEditBook().execute("");
            }
        });

        showDeatails();
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

        sb.append("      \"literaryForm\": " + selectedLiteraryForm.getValue());

        sb.append("}");

        json = sb.toString();

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

        ArrayAdapter<String> authorsAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, authors);
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

        ArrayAdapter<String> languagesAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, languages);
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

        ArrayAdapter<String> literaryFormAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, literaryForms);
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
            OkHttpClient client = new OkHttpClient();
            StringBuilder sb = new StringBuilder();
            sb.append("https://api.backendless.com/v1/data/Books");   //base url
            sb.append("/");                                          //lomka
            sb.append(objectId);                                     //ID knihy pre edit
            String url = sb.toString();

            if(!this.isCancelled()) {

                RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), json);

                Request request = new Request.Builder()
                        .url(url)
                        .header("application-id", "36E0E8DE-E56C-9A69-FFE7-9CE128693F00")
                        .addHeader("secret-key", "B1E5E7AC-907F-5A89-FFBB-AC7482E0E600")
                        .put(requestBody)
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
                                return response.body().string();
                            }

                            case 204: {
                                EditBook.this.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        showDialog("No content to show!");
                                    }
                                });
                                return "";
                            }

                            case 400: {
                                EditBook.this.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        showDialog("Bad request syntax!");
                                    }
                                });
                                return "";
                            }

                            case 404: {
                                EditBook.this.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        showDialog("Requested book not found!");
                                    }
                                });
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

            return "";
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            pDialog.dismiss();

            Intent intent = new Intent();
            intent.putExtra("Json", result);
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
            case R.id.btn_goback:
                Intent intent = new Intent();
                setResult(RESULT_OK, intent);
                finish();
                break;


            default:
                break;
        }
    }
}
