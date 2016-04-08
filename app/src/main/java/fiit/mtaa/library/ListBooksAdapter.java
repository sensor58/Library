package fiit.mtaa.library;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ListBooksAdapter extends ArrayAdapter<Book> {

    LayoutInflater inflater;
    Book bookToDelete;
    private ArrayList<Book> books;

    public ListBooksAdapter(Activity context, ArrayList<Book> books) {
        super(context, 0, books);

        this.books = books;
        this.inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return books.size();
    }

    @Override
    public Book getItem(int position) { return books.get(position); }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        Book book = books.get(position);
        View vi = convertView;

        if (convertView == null) vi = inflater.inflate(R.layout.layout_book, null);

        ImageButton imageButton = (ImageButton) vi.findViewById(R.id.listBooksButton);
        imageButton.setTag(position);
        imageButton.setOnClickListener(new XClickHandler());

        TextView textView = (TextView) vi.findViewById(R.id.txtTitle);
        TextView subtextView = (TextView) vi.findViewById(R.id.txtSubTitle);

        textView.setText(book.getAuthor().toString());
        subtextView.setText(book.getTitle());

        return vi;
    }

    public class XClickHandler implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            int position = (Integer) v.getTag();
            bookToDelete = getItem(position);
        }
    }
}