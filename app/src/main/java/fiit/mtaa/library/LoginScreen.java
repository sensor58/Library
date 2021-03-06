package fiit.mtaa.library;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;

public class LoginScreen extends AppCompatActivity implements View.OnClickListener {

    Button btn_login;
    EditText username, password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_screen);

        username = (EditText) findViewById(R.id.username);
        password = (EditText) findViewById(R.id.password);
        btn_login = (Button) findViewById(R.id.btn_login);

        btn_login.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if((username.getText().toString().equals("Margita")) && (password.getText().toString().equals("Ziguli")))  {
            startActivity(new Intent(this, OverviewScreen.class));
        }
        else {
            String message = null;

            if((!username.getText().toString().isEmpty()) && (!password.getText().toString().isEmpty())) {
                showDialog("Incorrect username or password! Please try again.");
            }
            else {
                showDialog("Both username and password must be filled!");
            }
        }
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
