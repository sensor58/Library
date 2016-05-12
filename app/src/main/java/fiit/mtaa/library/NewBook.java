package fiit.mtaa.library;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;


import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.ArrayList;

import io.socket.client.Ack;
import io.socket.client.IO;
import io.socket.client.Socket;
import okhttp3.OkHttpClient;

public class NewBook extends AppCompatActivity {
    private EditText title, year, publisher, paperback, price, isbn, imageUrl;
    private Spinner author, literaryForm, language;
    private ImageButton btn_goback3, btn_check;
    private ProgressDialog pDialog;
    private Socket socket;

    private Book.Author selectedAuthor = null;
    private Book.Language selectedLanguage = null;
    private Book.LiteraryForm selectedLiteraryForm = null;

    private String madeJson;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_book);

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

        btn_goback3 = (ImageButton) findViewById(R.id.btn_goback3);
        btn_goback3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btn_check = (ImageButton) findViewById(R.id.btn_check);
        btn_check.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(checkConnection() == 0) {
                    if (checkInputFields() && tryMakeJson()) {
                        new PostBook().execute("");
                        Intent intent = new Intent();
                        setResult(666, intent);
                        finish();
                    }
                }
                else {
                    showDialog("Check your internet connection and try again after while!");
                }
            }
        });

        prepareEnumFields();
    }

    private boolean checkInputFields() {
        String input;

        if (selectedAuthor == null) {
            NewBook.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    showDialog("Author was not chosen");
                }
            });

            return false;
        }

        if (selectedLanguage == null) {
            NewBook.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    showDialog("Language was not chosen");
                }
            });

            return false;
        }

        if (selectedLiteraryForm == null) {
            NewBook.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    showDialog("Literary Form was not chosen");
                }
            });

            return false;
        }

        input = year.getText().toString();
        try {
            int n = Integer.parseInt(input);
        }
        catch (NumberFormatException e) {
            NewBook.this.runOnUiThread(new Runnable() {
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
            NewBook.this.runOnUiThread(new Runnable() {
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
            NewBook.this.runOnUiThread(new Runnable() {
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
            NewBook.this.runOnUiThread(new Runnable() {
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

    public void prepareEnumFields() {
        //AUTHOR

        author.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (parent.getSelectedItem().toString().equals("Choose")) {
                    selectedAuthor = null;
                }
                else {
                    selectedAuthor = Book.Author.values()[position - 1];
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedAuthor = null;
            }
        });

        ArrayList<String> authors = new ArrayList<>();

        authors.add("Choose");

        for (int i = 1; i < Book.Author.values().length + 1; i++) {
            authors.add(Book.Author.values()[i-1].toString());
        }

        //  ArrayAdapter<String> authorsAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, authors);
        ArrayAdapter<String> authorsAdapter = new ArrayAdapter<String>(this, R.layout.spinner_layout, authors);
        authorsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        author.setAdapter(authorsAdapter);

        //LANGUAGE

        language.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (parent.getSelectedItem().toString().equals("Choose")) {
                    selectedLanguage = null;
                }
                else {
                    selectedLanguage = Book.Language.values()[position - 1];
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedLanguage = null;
            }
        });

        ArrayList<String> languages = new ArrayList<>();

        languages.add("Choose");

        for (int i = 1; i < Book.Language.values().length + 1; i++) {
            languages.add(Book.Language.values()[i-1].toString());
        }

        ArrayAdapter<String> languagesAdapter = new ArrayAdapter<String>(this, R.layout.spinner_layout, languages);
        languagesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        language.setAdapter(languagesAdapter);

        //LITERARY FORM

        literaryForm.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (parent.getSelectedItem().toString().equals("Choose")) {
                    selectedLiteraryForm = null;
                }
                else {
                    selectedLiteraryForm = Book.LiteraryForm.values()[position - 1];
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedLiteraryForm = null;
            }
        });

        ArrayList<String> literaryForms = new ArrayList<>();

        literaryForms.add("Choose");

        for (int i = 1; i < Book.LiteraryForm.values().length + 1; i++) {
            literaryForms.add(Book.LiteraryForm.values()[i-1].toString());
        }

        ArrayAdapter<String> literaryFormAdapter = new ArrayAdapter<String>(this, R.layout.spinner_layout, literaryForms);
        literaryFormAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        literaryForm.setAdapter(literaryFormAdapter);
    }

    public class PostBook extends AsyncTask<String, Integer, String> {

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
            new AlertDialog.Builder(NewBook.this)
                    .setCancelable(false)
                    .setTitle("Error")
                    .setMessage("Check your internet connection and try again after while!")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
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

                String json = madeJson;     //Json pridany


                JSONObject js = new JSONObject();
                try {
                    js.put("url", "/data/Library1");
                    js.put("data", new JSONObject().put("data", new JSONObject(json)));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                socket.emit("post", js, new Ack() {
                    @Override
                    public void call(Object... args) {}
                });
            }
            return null;
        }
    }


    private void showDialog(String message) {
        new AlertDialog.Builder(NewBook.this)
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
