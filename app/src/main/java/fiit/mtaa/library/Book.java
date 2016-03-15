package fiit.mtaa.library;

import com.google.gson.annotations.Expose;

import java.io.Serializable;

public class Book implements Serializable {
    public int year = 0;

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getAuthor() {
        return author;
    }

    public void setAuthor(int author) {
        this.author = author;
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
    public String isbn = null;
    public int language = 0;
    public String title = null;
    public String picture = null;
    public int literaryForm = 0;
    public double price = 0;
    public int paperback = 0;
    public String publisher = null;
    public String objectId = null;


}
