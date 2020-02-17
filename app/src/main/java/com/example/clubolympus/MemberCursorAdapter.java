package com.example.clubolympus;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.example.clubolympus.data.ClubOlympusContract.MemberEntry;

public class MemberCursorAdapter extends CursorAdapter {

    public MemberCursorAdapter(Context context, Cursor c, boolean autoRequery) {
        super(context, c, autoRequery);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.member_item, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        TextView firstNameTextView = view.findViewById(R.id.firstNameTextViewForItem);
        TextView lastNameTextView = view.findViewById(R.id.lastNameTextViewForItem);
        TextView sportTextView = view.findViewById(R.id.sportTextViewForItem);

        String firstName = cursor.getString(cursor.getColumnIndexOrThrow(MemberEntry.KEY_FIRST_NAME));
        String lastName = cursor.getString(cursor.getColumnIndexOrThrow(MemberEntry.KEY_LAST_NAME));
        String sport = cursor.getString(cursor.getColumnIndexOrThrow(MemberEntry.KEY_SPORT));

        firstNameTextView.setText(firstName);
        lastNameTextView.setText(lastName);
        sportTextView.setText(sport);
    }
}
