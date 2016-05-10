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
import android.widget.Spinner;


import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;

import io.socket.*;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_book);

        author = (Spinner) findViewById(R.id.author);
        title = (EditText) findViewById(R.id.title);
        literaryForm = (Spinner) findViewById(R.id.literaryForm);
        year = (EditText) findViewById(R.id.year);
        publisher = (EditText) findViewById(R.id.publisher);
        paperback = (EditText) findViewById(R.id.paperback);
        language = (Spinner) findViewById(R.id.language);
        price = (EditText) findViewById(R.id.price);
        isbn = (EditText) findViewById(R.id.isbn);

        btn_goback3 = (ImageButton) findViewById(R.id.btn_goback3);
        btn_goback3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btn_check = (ImageButton) findViewById(R.id.btn_check);
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

                String json = null;     //tu treba dat objekt knihy.


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
