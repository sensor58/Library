package fiit.mtaa.library;

import android.content.Context;
import android.widget.ArrayAdapter;

import java.util.ArrayList;

/**
 * Created by Tomas on 14.3.2016.
 */
public class ListAdapter extends ArrayAdapter<Book>{
    private Context context;
    private ArrayList<Book> books;
    public ListAdapter(Context context, int textViewId, ArrayList<Book> books) {
        super(context, textViewId, books);
        this.books = books;
    }
}
