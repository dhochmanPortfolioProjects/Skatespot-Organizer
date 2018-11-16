package com.dhochmanrquick.skatespotorganizer.data;

import android.os.Parcel;

import com.arlib.floatingsearchview.suggestions.model.SearchSuggestion;

public class SpotSuggestion implements SearchSuggestion {
    private String mColorName;
    private boolean mIsHistory = false;

    public SpotSuggestion(String suggestion) {
        this.mColorName = suggestion.toLowerCase();
    }

    public SpotSuggestion(Parcel source) {
        this.mColorName = source.readString();
        this.mIsHistory = source.readInt() != 0;
    }

    public void setIsHistory(boolean isHistory) {
        this.mIsHistory = isHistory;
    }

    public boolean getIsHistory() {
        return this.mIsHistory;
    }

    @Override
    public String getBody() {
        return mColorName;
    }

    public static final Creator<SpotSuggestion> CREATOR = new Creator<SpotSuggestion>() {
        @Override
        public SpotSuggestion createFromParcel(Parcel in) {
            return new SpotSuggestion(in);
        }

        @Override
        public SpotSuggestion[] newArray(int size) {
            return new SpotSuggestion[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mColorName);
        dest.writeInt(mIsHistory ? 1 : 0);
    }
}
