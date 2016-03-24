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

    public Author getAuthor() {
        return author;
    }

    public void setAuthor(Author author) { this.author = author; }

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

    public Language getLanguage() {
        return language;
    }

    public void setLanguage(Language language) {
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

    public LiteraryForm getLiteraryForm() {
        return literaryForm;
    }

    public void setLiteraryForm(LiteraryForm literaryForm) {
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

    public Author author = null;
    public int year = 0;
    public String isbn = null;
    public Language language = null;
    public String title = null;
    public String picture = null;
    public LiteraryForm literaryForm = null;
    public double price = 0;
    public int paperback = 0;
    public String publisher = null;
    public String objectId = null;

    public enum Author {
        Dan_Brown(1),
        Wiliam_Shakespeare(2),
        Jo_Nesbo(3),
        Dominik_Dan(4),
        Martin_Kukucin(5),
        Margita_Figuli(6),
        Johann_Wolfgang_von_Goethe(7),
        Nicholas_Sparks(8),
        Christian_Morgenstern(9),
        Dusan_Dusek(10);

        private final int value;

        Author(int value) {
            this.value = value;
        }

        public int getValue() {
            return this.value;
        }

        public static Author fromValue(int value) {
            for (Author author : Author.values()) {
                if (author.getValue() == value) {
                    return author;
                }
            }
            return null;
        }

        @Override public String toString() { return this.name().replace("_", " "); }
    }

    public enum LiteraryForm {
        Poetry(1),
        Prose(2),
        Drama(3);

        private final int value;

        LiteraryForm(int value) {
            this.value = value;
        }

        public int getValue() {
            return this.value;
        }

        public static LiteraryForm fromValue(int value) {
            for (LiteraryForm literaryForm : LiteraryForm.values()) {
                if (literaryForm.getValue() == value) {
                    return literaryForm;
                }
            }
            return null;
        }
    }

    public enum Language {
        English(1),
        Slovak(2),
        German(3);

        private final int value;

        Language(int value) {
            this.value = value;
        }

        public int getValue() {
            return this.value;
        }

        public static Language fromValue(int value) {
            for (Language language : Language.values()) {
                if (language.getValue() == value) {
                    return language;
                }
            }
            return null;
        }
    }
}

