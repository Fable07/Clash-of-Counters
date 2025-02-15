package com.example.clashofcounters;

import java.util.Objects;

public class UserHighscore {
    private String userEmail;
    private int highscore;

    public UserHighscore() {
    }

    public UserHighscore(String userEmail, int highscore) {
        this.userEmail = userEmail;
        this.highscore = highscore;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public int getHighscore() {
        return highscore;
    }

    public void setHighscore(int highscore) {
        this.highscore = highscore;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserHighscore that = (UserHighscore) o;
        return Objects.equals(userEmail, that.userEmail);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userEmail);
    }
}
