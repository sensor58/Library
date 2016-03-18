package fiit.mtaa.library;

import android.util.EventLogTags;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Book implements Serializable {
    public int getYear() {
        return year;
    }

    public void setYear(int year) { this.year = year; }

    public int getAuthor() {
        return author;
    }

    public void setAuthor(int author) { this.author = author; }

    public String getAuthorName(int num) {
        switch(num) {
            case 1:
                return "Dan Brown";
            case 2:
                return "Wiliam Shakespeare";
            case 3:
                return "Jo Nesbo";
            case 4:
                return "Dominik Dan";
            case 5:
                return "Martin Kukucin";
            case 6:
                return "Margita Figuli";
            case 7:
                return "Johann Wolfgang von Goethe";
            case 8:
                return "Nicholas Sparks";
            case 9:
                return "Christian Morgenstern";
            case 10:
                return "Dusan Dusek";
        }
        return null;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public int getLanguage() {
        return language;
    }

    public void setLanguage(int language) {
        this.language = language;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public int getLiteraryForm() {
        return literaryForm;
    }

    public void setLiteraryForm(int literaryForm) {
        this.literaryForm = literaryForm;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getPaperback() {
        return paperback;
    }

    public void setPaperback(int paperback) {
        this.paperback = paperback;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public String getObjectId() {
        return objectId;
    }

    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }

    public int author = 0;
    public int year = 0;
    public String isbn = null;
    public int language = 0;
    public String title = null;
    public String picture = null;
    public int literaryForm = 0;
    public double price = 0;
    public int paperback = 0;
    public String publisher = null;
    public String objectId = null;

    /*public enum Author {
        DBrown(1),
        WShakespeare(2),
        JNesbo(3),
        DDan(4),
        MKukucin(5),
        MFiguli(6),
        JGoethe(7),
        NSparks(8),
        CMorgenstern(9),
        DDusek(10);

        private final int value;

        Author(int value) {
            this.value = value;
        }

        public int getValue() {
            return this.value;
        }

        DBrown("Dan Brown"),
        WShakespeare("Wiliam Shakespeare"),
        JNesbo("Jo Nesbo"),
        DDan("Dominik Dan"),
        MKukucin("Martin Kukucin"),
        MFiguli("Margita Figuli"),
        JGoethe("Johann Goethe"),
        NSparks("Nicholas Sparks"),
        CMorgenstern("Christian Morgenstern"),
        DDusek("Dusan Dusek");


        @Override public String toString() { return displayAuthor; }
    }*/
}

