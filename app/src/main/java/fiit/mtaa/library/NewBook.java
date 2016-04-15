package fiit.mtaa.library;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import okhttp3.OkHttpClient;

public class NewBook extends AppCompatActivity {
    private EditText author, title ,literaryForm, year, publisher, paperback, language, price, isbn;
    private ImageButton btn_goback3;
    private ProgressDialog pDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_book);

        author = (EditText) findViewById(R.id.author);
        title = (EditText) findViewById(R.id.title);
        literaryForm = (EditText) findViewById(R.id.literaryForm);
        year = (EditText) findViewById(R.id.year);
        publisher = (EditText) findViewById(R.id.publisher);
        paperback = (EditText) findViewById(R.id.paperback);
        language = (EditText) findViewById(R.id.language);
        price = (EditText) findViewById(R.id.price);
        isbn = (EditText) findViewById(R.id.isbn);

        btn_goback3 = (ImageButton) findViewById(R.id.btn_goback3);
        btn_goback3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        showDialog("Coming soon...:-)");
    }

    public class HttpPostBook extends AsyncTask<String, Integer, String> {

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
            OkHttpClient client = new OkHttpClient();
            String url = "https://api.backendless.com/v1/data/Books";

            if(!this.isCancelled()) {

            }
            return null;
        }
    }


    private void showDialog(String message) {
        new AlertDialog.Builder(NewBook.this)
                .setCancelable(false)
                .setTitle("Under construction")
                .setMessage(message)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        finish();
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
