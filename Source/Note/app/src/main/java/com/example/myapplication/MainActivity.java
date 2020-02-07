package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import static java.lang.Integer.parseInt;

public class MainActivity extends AppCompatActivity {

    ListView lvnote;
    ArrayList<ComponentNote> note;
    AdapterNote adapterNote;
    Button btnadd;
    SearchView searchView;
    String currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());
    DataBaseNote DBNote;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Tao cơ sở dữ liệu
        DBNote =new DataBaseNote(this,"NOTE.sqlite",null,2);
        // Tạo bảng
        DBNote.QueryData("CREATE TABLE IF NOT EXISTS note(Id INTEGER PRIMARY KEY AUTOINCREMENT, nameNote VARCHAR(200), timeEdit VARCHAR(200), tag VARCHAR(200), data VARCHAR(8000))");
        anhxa();

        adapterNote= new AdapterNote(this,R.layout.row_listview,note);
        lvnote.setAdapter(adapterNote);
        lvnote.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent= new Intent(MainActivity.this,DetailNote.class);
                intent.putExtra("dulieu",note.get(i).getiD().toString().trim());
                //finish();
                startActivity(intent);
            }
        });
    }
    private void anhxa()
    {
        lvnote=(ListView) findViewById(R.id.note);
        btnadd=(Button) findViewById(R.id.add);
        note = new ArrayList<>();
        note.clear();
        Cursor dataNote =DBNote.getData("SELECT * FROM note");
        while (dataNote.moveToNext())
        {
            Integer id=parseInt(dataNote.getString(0));
            String name=dataNote.getString(1);
            String time =dataNote.getString(2);
            String tag =dataNote.getString(3);
            String data=dataNote.getString(4);
            note.add(new ComponentNote(name,time,tag,id,data));
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_app, menu);
        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView = (SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));
        searchView.setQueryHint("Search");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }
            @Override
            public boolean onQueryTextChange(String s) {
                adapterNote.filter(s.trim());
                return false;
            }
        });
        return super .onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId()==R.id.addNote)
        {
            Intent intent1= new Intent(MainActivity.this,DetailNote.class);
            intent1.putExtra("dulieu","add");
            //finish();
            startActivity(intent1);
        }
        return super.onOptionsItemSelected(item);
    }
    public void getData()
    {
        note.clear();
        Cursor dataNote =DBNote.getData("SELECT * FROM note");
        while (dataNote.moveToNext())
        {
            Integer id=parseInt(dataNote.getString(0));
            String name=dataNote.getString(1);
            String time =dataNote.getString(2);
            String tag =dataNote.getString(3);
            String data=dataNote.getString(4);
            note.add(new ComponentNote(name,time,tag,id,data));
        }
        adapterNote.notifyDataSetChanged();
    }

    public void dialogEditNote(String name, String tag, final Integer id)
    {
        final Dialog dialog=new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialogedit);

        final EditText editName =(EditText) dialog.findViewById(R.id.editName);
        final EditText editTag =(EditText) dialog.findViewById(R.id.editTag);
        Button btnSave=(Button) dialog.findViewById(R.id.editSave);
        Button btnCanel=(Button) dialog.findViewById(R.id.editCanel);

        editName.setText(name);
        editTag.setText(tag);

        btnCanel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar c = Calendar.getInstance();
                SimpleDateFormat dateformat = new SimpleDateFormat("dd-MMM-yyyy hh:mm:ss aa");
                String datetime = dateformat.format(c.getTime());
                String newName=editName.getText().toString().trim();
                String newTag=editTag.getText().toString().trim();

                DBNote.QueryData("UPDATE note SET nameNote = '"+ newName +"' WHERE Id = '"+ id +"'");
                DBNote.QueryData("UPDATE note SET tag = '"+ newTag +"' WHERE Id = '"+ id +"'");
                DBNote.QueryData("UPDATE note SET timeEdit = '"+ datetime +"' WHERE Id = '"+ id +"'");
                Toast.makeText(MainActivity.this, "Note has been update", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
                getData();
            }
        });
        dialog.show();
    }

    public  void dialogDeleteNote(final Integer id)
    {
        final AlertDialog.Builder dialogDelete = new AlertDialog.Builder(this);
        dialogDelete.setMessage("Do you want to delete this note ?");
        dialogDelete.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                DBNote.QueryData("DELETE FROM note WHERE Id = '"+ id +"'");
                Toast.makeText(MainActivity.this, "Deleted!!!", Toast.LENGTH_SHORT).show();
                getData();
            }
        });
        dialogDelete.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        });
        dialogDelete.show();
    }
    @Override
    protected void onRestart() {
        super.onRestart();
        getData();
    }
}