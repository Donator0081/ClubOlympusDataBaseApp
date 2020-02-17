package com.example.clubolympus;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NavUtils;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.clubolympus.data.ClubOlympusContract.MemberEntry;

public class AddMemberActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    private EditText firstNameEditText;
    private EditText lastNameEditText;
    private EditText groupNameEditText;
    private Spinner genderSpinner;
    private int gender = 0;
    private ArrayAdapter spinnerAdapter;
    private static final int EDIT_MEMBER_LOADER = 111;
    Uri currentMemberURI;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_member);

        Intent intent = getIntent();
        currentMemberURI = intent.getData();
        if (currentMemberURI == null) {
            setTitle("Add a member");
            invalidateOptionsMenu();
        } else {
            setTitle("Edit the member");
            getSupportLoaderManager().initLoader(EDIT_MEMBER_LOADER, null, this);
        }

        firstNameEditText = findViewById(R.id.firstNameEditText);
        lastNameEditText = findViewById(R.id.lastNameEditText);
        groupNameEditText = findViewById(R.id.groupEditText);
        genderSpinner = findViewById(R.id.genderSpinner);

        spinnerAdapter = ArrayAdapter.createFromResource(this, R.array.gender_array, android.R.layout.simple_spinner_item);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        genderSpinner.setAdapter(spinnerAdapter);

        genderSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedGender = (String) parent.getItemAtPosition(position);
                if (!TextUtils.isEmpty(selectedGender)) {
                    if (selectedGender.equals("Male")) {
                        gender = MemberEntry.GENDER_MALE;
                    } else if (selectedGender.equals("Female")) {
                        gender = MemberEntry.GENDER_FEMALE;
                    } else {
                        gender = MemberEntry.GENDER_UNKNOWN;
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                gender = 0;
            }
        });
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);

        if (currentMemberURI == null) {
            MenuItem menuItem = menu.findItem(R.id.delete_member);
            menuItem.setVisible(false);
        }

        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.edit_member_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.save_member:
                saveMember();
                return true;
            case R.id.delete_member:
                showDeleteMember();
                return true;
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
        }
        return super.onOptionsItemSelected(item);
    }

    private void saveMember() {

        String firstNameString = firstNameEditText.getText().toString().trim();
        String lastNameString = lastNameEditText.getText().toString().trim();
        String groupNameString = groupNameEditText.getText().toString().trim();

        if (TextUtils.isEmpty(firstNameString)) {
            Toast.makeText(this, "Input the First Name", Toast.LENGTH_SHORT).show();
            return;
        } else if (TextUtils.isEmpty(lastNameString)) {
            Toast.makeText(this, "Input the Last Name", Toast.LENGTH_SHORT).show();
            return;
        } else if (TextUtils.isEmpty(groupNameString)) {
            Toast.makeText(this, "Input the group", Toast.LENGTH_SHORT).show();
            return;
        } else if (gender == MemberEntry.GENDER_UNKNOWN) {
            Toast.makeText(this, "Choose the gender", Toast.LENGTH_SHORT).show();
            return;
        }

        ContentValues contentValues = new ContentValues();
        contentValues.put(MemberEntry.KEY_FIRST_NAME, firstNameString);
        contentValues.put(MemberEntry.KEY_LAST_NAME, lastNameString);
        contentValues.put(MemberEntry.KEY_SPORT, groupNameString);
        contentValues.put(MemberEntry.KEY_GENDER, gender);

        if (currentMemberURI == null) {
            ContentResolver contentResolver = getContentResolver();
            Uri uri = contentResolver.insert(MemberEntry.CONTENT_URI, contentValues);
            if (uri == null) {
                Toast.makeText(this, "Insertion of date failed ", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Data saved", Toast.LENGTH_SHORT).show();
            }
        } else {
            int rowsChanged = getContentResolver().update(currentMemberURI, contentValues, null, null);
            if (rowsChanged == 0) {
                Toast.makeText(this, "Saving of date failed ", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Member updated", Toast.LENGTH_SHORT).show();
            }
        }

    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
        String[] projection = {
                MemberEntry._ID,
                MemberEntry.KEY_FIRST_NAME,
                MemberEntry.KEY_LAST_NAME,
                MemberEntry.KEY_GENDER,
                MemberEntry.KEY_SPORT
        };
        return new CursorLoader(this, currentMemberURI,
                projection,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
        if (data.moveToFirst()) {
            int firstNameColumn = data.getColumnIndex(MemberEntry.KEY_FIRST_NAME);
            int lastNameColumn = data.getColumnIndex(MemberEntry.KEY_LAST_NAME);
            int genderColumn = data.getColumnIndex(MemberEntry.KEY_GENDER);
            int sportColumn = data.getColumnIndex(MemberEntry.KEY_SPORT);
            String firstName = data.getString(firstNameColumn);
            String lastName = data.getString(lastNameColumn);
            int gender = data.getInt(genderColumn);
            String sport = data.getString(sportColumn);

            firstNameEditText.setText(firstName);
            lastNameEditText.setText(lastName);
            groupNameEditText.setText(sport);
            switch (gender) {
                case MemberEntry.GENDER_MALE:
                    genderSpinner.setSelection(1);
                    break;
                case MemberEntry.GENDER_FEMALE:
                    genderSpinner.setSelection(2);
                    break;
                case MemberEntry.GENDER_UNKNOWN:
                    genderSpinner.setSelection(0);
                    break;
            }
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {

    }

    private void showDeleteMember() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Do you want delete the member?");
        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteMember();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();

    }

    private void deleteMember() {
        if (currentMemberURI != null) {
            int rowsDeleted = getContentResolver().delete(currentMemberURI, null, null);
            if (rowsDeleted == 0) {
                Toast.makeText(this, "Deleting of date failed", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Member is deleted", Toast.LENGTH_SHORT).show();
            }

            finish();
        }

    }
}























