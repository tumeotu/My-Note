package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.SearchManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static java.lang.Integer.parseInt;
import static java.lang.Integer.toBinaryString;

public class DetailNote extends AppCompatActivity {

    DataBaseNote DBNote;
    TextView timeNote;
    EditText tagNote, dataNote;
    ComponentNote note;
    String value,name="";
    AdapterImage adapterImage;
    ArrayList<Image> imageArrayList;
    GridView gridView;
    int REQUEST_TOAST_CAMERA=123;
    int FIRST_SIZE=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_note);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        anhxa();
        DBNote =new DataBaseNote(this,"NOTE.sqlite",null,2);
        // Tạo bảng
        DBNote.QueryData("CREATE TABLE IF NOT EXISTS note(Id INTEGER PRIMARY KEY AUTOINCREMENT, nameNote VARCHAR(200), timeEdit VARCHAR(200), tag VARCHAR(200), data VARCHAR(8000))");
        DBNote.QueryData("CREATE TABLE IF NOT EXISTS image(Id INTEGER PRIMARY KEY AUTOINCREMENT, idImage INTEGER, bitMap VARCHAR(2000))");
        getData();
        adapterImage =new AdapterImage(this,R.layout.row_image,imageArrayList);
        gridView.setAdapter(adapterImage);
       gridView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
           @Override
           public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
               dialogDeleteImage(i);
               return false;
           }
       });
    }
    private void getData()
    {
        Intent intent=getIntent();
        value=intent.getStringExtra("dulieu");
        if(!value.trim().equals("add")) {
            Cursor temp=DBNote.getData("SELECT * FROM note WHERE Id = '"+ Integer.parseInt(value.trim()) +"'");
            while (temp.moveToNext())
            {
                Integer id=parseInt(temp.getString(0));
                String name=temp.getString(1);
                String time =temp.getString(2);
                String tag =temp.getString(3);
                String data=temp.getString(4);
                note=new ComponentNote(name,time,tag,id,data);
            }
            Cursor temp1 =DBNote.getData("SELECT * FROM image WHERE idImage= '"+ note.getiD() +"'");
            while (temp1.moveToNext())
            {
                Integer id=parseInt(temp1.getString(0));
                Integer idImage=parseInt(temp1.getString(1));
                String bimap =temp1.getString(2);
                Image image=new Image(StringToBitMap(bimap),idImage);
                image.setId(id);
                imageArrayList.add(image);
            }
            FIRST_SIZE=imageArrayList.size();
            timeNote.setText(note.getTimewrite().toString().trim());
            tagNote.setText(note.getTag().toString().trim());
            dataNote.setText(note.getData().toString().trim());
        }
        else
        {
            FIRST_SIZE=0;
            note=new ComponentNote("","","",null,"");
            timeNote.setText(note.getTimewrite().toString().trim());
            tagNote.setText(note.getTag().toString().trim());
            dataNote.setText(note.getData().toString().trim());

        }
    }
    private void anhxa()
    {
        timeNote=(TextView) findViewById(R.id.time_note);
        tagNote=(EditText) findViewById(R.id.tag_note);
        dataNote=(EditText) findViewById(R.id.data_note);
        gridView=(GridView) findViewById(R.id.gridView);
        imageArrayList=new ArrayList<>();
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                if(!compareData())
                {
                    dialogSave();
                }
                else
                {
                    Intent intent= new Intent(DetailNote.this,MainActivity.class);
                    finish();
                    startActivity(intent);
                }
                return true;
            case R.id.saveNote:
                Calendar c = Calendar.getInstance();
                SimpleDateFormat dateformat = new SimpleDateFormat("dd-MMM-yyyy hh:mm:ss aa");
                String datetime = dateformat.format(c.getTime());
                note.setTag(tagNote.getText().toString().trim());
                note.setData(dataNote.getText().toString().trim());
                note.setTimewrite(datetime.trim());
                timeNote.setText(datetime.trim());
                save();
                return true;
            case R.id.camera:
                takeAPhoto();
                return true;

        }
        return super.onOptionsItemSelected(item);
    }
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.navitationnote, menu);
        return super .onCreateOptionsMenu(menu);
    }
    public boolean compareData()
    {
        if(!tagNote.getText().toString().trim().equals(note.getTag()))
        {
            return false;
        }
        if (!dataNote.getText().toString().trim().equals(note.getData()))
        {
            return false;
        }
        if(FIRST_SIZE!=imageArrayList.size())
        {
            return false;
        }
        return true;
    }
    public boolean checkImageChange()
    {
        if(FIRST_SIZE!=imageArrayList.size())
        {
            return true;
        }
        return false;
    }
    public void dialogSave()
    {
        final AlertDialog.Builder dialogSave = new AlertDialog.Builder(this);
        dialogSave.setMessage("Do you want to save this note ?");
        dialogSave.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                save();
                Intent intent2= new Intent(DetailNote.this,MainActivity.class);
                finish();
                startActivity(intent2);
            }
        });
        dialogSave.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent intent1= new Intent(DetailNote.this,MainActivity.class);
                finish();
                startActivity(intent1);
            }
        });
        dialogSave.show();
    }
    public void dialogDeleteImage(final int k)
    {
        final AlertDialog.Builder dialogDelete = new AlertDialog.Builder(this);
        dialogDelete.setMessage("Do you want to delete this image ?");

        dialogDelete.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                DBNote.QueryData("DELETE FROM image WHERE Id= '"+ imageArrayList.get(k).getId() +"'");
                imageArrayList.remove(k);
                FIRST_SIZE=imageArrayList.size();
                adapterImage.notifyDataSetChanged();
            }
        });
        dialogDelete.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        });
        dialogDelete.show();

    }
    public void save()
    {
        getNameNote();
        if(!value.equals("add")) {
            DBNote.QueryData("UPDATE note SET nameNote = '" + name + "' WHERE Id = '" + note.getiD() + "'");
            DBNote.QueryData("UPDATE note SET tag = '" + tagNote.getText().toString().trim() + "' WHERE Id = '" + note.getiD() + "'");
            DBNote.QueryData("UPDATE note SET timeEdit = '" + timeNote.getText().toString().trim() + "' WHERE Id = '" + note.getiD() + "'");
            DBNote.QueryData("UPDATE note SET data = '" + dataNote.getText().toString().trim() + "' WHERE Id = '" + note.getiD() + "'");
            if(checkImageChange())
            {
                saveImage(FIRST_SIZE);
            }
        }
        else
        {
            DBNote.QueryData("INSERT INTO note VALUES(null, '"+ name +"', '"+ timeNote.getText().toString().trim() +"','"+ tagNote.getText().toString().trim() +"', '"+ dataNote.getText().toString().trim() +"')");
            ArrayList<ComponentNote> noteTemp1=new ArrayList<>();
            Cursor dataNote =DBNote.getData("SELECT * FROM note");
            noteTemp1.clear();
            while (dataNote.moveToNext())
            {
                Integer id=parseInt(dataNote.getString(0));
                String name=dataNote.getString(1);
                String time =dataNote.getString(2);
                String tag =dataNote.getString(3);
                String data=dataNote.getString(4);
                noteTemp1.add(new ComponentNote(name,time,tag,id,data));
            }
            value=noteTemp1.get(noteTemp1.size()-1).getiD().toString();
            note= noteTemp1.get(noteTemp1.size()-1);
            if(checkImageChange())
            {
                saveImage(FIRST_SIZE);
            }
        }
        closeKeyBroad();
    }
    public void closeKeyBroad()
    {
        View view =this.getCurrentFocus();
        if(view!=null)
        {
            InputMethodManager imm=(InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(),0);
        }
    }
    public void getNameNote()
    {
        name="";
        String temp=dataNote.getText().toString();
        if(temp.indexOf('\n')==-1)
        {
            name=dataNote.getText().toString();
        }
        else
        {
            for(int i=0;i<temp.indexOf('\n');i++)
            {
                name+=temp.charAt(i);
            }
        }
        note.setName(name);
    }
    public void takeAPhoto()
    {
        ActivityCompat.requestPermissions(DetailNote.this,
                new String[]{Manifest.permission.CAMERA},REQUEST_TOAST_CAMERA);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if(requestCode==REQUEST_TOAST_CAMERA&&grantResults.length>0&&grantResults[0]== PackageManager.PERMISSION_GRANTED)
        {
            Intent intentCamera=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(intentCamera,REQUEST_TOAST_CAMERA);
        }
        else
        {
            Toast.makeText(this, "You don't allow open your camera!!!", Toast.LENGTH_SHORT).show();
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode==REQUEST_TOAST_CAMERA&&resultCode==RESULT_OK&&data!=null)
        {
            Bitmap bitmapIamge= (Bitmap) data.getExtras().get("data");

            imageArrayList.add(new Image(bitmapIamge,note.getiD()));
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
    public void saveImage(int index)
    {
        if(!value.equals("add")) {
            for (int i = index; i < imageArrayList.size(); i++) {
                DBNote.QueryData("INSERT INTO image VALUES(null, '" + note.getiD() + "', '" + BitMapToString(imageArrayList.get(i).getBitmap()) + "')");
            }
            FIRST_SIZE = imageArrayList.size();
        }
        else
        {
            ArrayList<ComponentNote> noteTemp=new ArrayList<>();
            Cursor dataNote =DBNote.getData("SELECT * FROM note");
            noteTemp.clear();
            while (dataNote.moveToNext())
            {
                Integer id=parseInt(dataNote.getString(0));
                String name=dataNote.getString(1);
                String time =dataNote.getString(2);
                String tag =dataNote.getString(3);
                String data=dataNote.getString(4);
                noteTemp.add(new ComponentNote(name,time,tag,id,data));
            }
            value=noteTemp.get(noteTemp.size()-1).getiD().toString();
            for (int i = index; i < imageArrayList.size(); i++) {
                DBNote.QueryData("INSERT INTO image VALUES(null, '" + noteTemp.get(noteTemp.size()-1).getiD() + "', '" + BitMapToString(imageArrayList.get(i).getBitmap()) + "')");
            }
            FIRST_SIZE = imageArrayList.size();
        }
    }
    public String BitMapToString(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] b = baos.toByteArray();
        String temp = Base64.encodeToString(b, Base64.DEFAULT);
        return temp;
    }
    public Bitmap StringToBitMap(String encodedString) {
        try {
            byte[] encodeByte = Base64.decode(encodedString, Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(encodeByte, 0,
                    encodeByte.length);
            return bitmap;
        } catch (Exception e) {
            e.getMessage();
            return null;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(!compareData())
        {
            save();
        }
    }
}
