package com.semanticsquare.thrillio.entities;

import com.semanticsquare.thrillio.constants.MovieGenre;

import java.util.Arrays;

public class Movie extends Bookmark {

    private int releaseYear;
    private String[] cast;
    private String[] direcors;
    private MovieGenre genre;
    private double imdbRating;

    public int getReleaseYear() {
        return releaseYear;
    }

    public void setReleaseYear(int releaseYear) {
        this.releaseYear = releaseYear;
    }

    public String[] getCast() {
        return cast;
    }

    public void setCast(String[] cast) {
        this.cast = cast;
    }

    public String[] getDirecors() {
        return direcors;
    }

    public void setDirecors(String[] direcors) {
        this.direcors = direcors;
    }

    public MovieGenre getGenre() {
        return genre;
    }

    public void setGenre(MovieGenre genre) {
        this.genre = genre;
    }

    public double getImdbRating() {
        return imdbRating;
    }

    public void setImdbRating(double imdbRating) {
        this.imdbRating = imdbRating;
    }

    @Override
    public String toString() {
        return "Movie{" +
                "releaseYear=" + releaseYear +
                ", cast=" + Arrays.toString(cast) +
                ", direcors=" + Arrays.toString(direcors) +
                ", genre='" + genre + '\'' +
                ", imdbRating=" + imdbRating +
                '}';
    }
    @Override
    public boolean isKidFriendlyEligible() {
        if(genre.equals(MovieGenre.HORROR)|| genre.equals(MovieGenre.THRILLERS)){
            return false;
        }
        return true;
    }
}

