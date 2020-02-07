package com.example.myapplication;

import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class AdapterNote extends BaseAdapter {
    private MainActivity context;
    private int layout;
    private List<ComponentNote> noteList;
    private ArrayList<ComponentNote> arrayList;

    public AdapterNote(MainActivity context, int layout, List<ComponentNote> alarmList) {
        this.context = context;
        this.layout = layout;
        this.noteList = alarmList;
        this.arrayList=new ArrayList<ComponentNote>();
        this.arrayList.addAll(alarmList);
    }

    @Override
    public int getCount() {
        return noteList.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    private class ViewHolder{
       TextView txtName, txtTime, txtTag;
       ImageView imgDelete, imgEdit;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override

    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder holder;
        if(view==null)
        {
            holder=new ViewHolder();
            LayoutInflater inflater= (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view =inflater.inflate(layout,null);
            holder.txtName=(TextView) view.findViewById(R.id.name);
            holder.txtTime=(TextView) view.findViewById(R.id.timewrite);
            holder.txtTag=(TextView) view.findViewById(R.id.tag);
            holder.imgDelete=(ImageView) view.findViewById(R.id.delete);
            holder.imgEdit=(ImageView) view.findViewById(R.id.edit);
            view.setTag(holder);
        }
        else
        {
            holder=(ViewHolder) view.getTag();
        }
         final ComponentNote note= noteList.get(i);
        holder.txtName.setText(note.getName());
        holder.txtTime.setText(note.getTimewrite());
        holder.txtTag.setText(note.getTag());

        //handle click edit and delete

        holder.imgEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                context.dialogEditNote(note.getName(),note.getTag(), note.getiD());
            }
        });
        holder.imgDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                context.dialogDeleteNote(note.getiD());
            }
        });
        return  view;
    }

    public void filter(String charText) {
        charText = charText.toLowerCase(Locale.getDefault());
        noteList.clear();
        if (charText.length() == 0) {
            noteList.addAll(arrayList);
        } else {
            for (ComponentNote wp : arrayList) {
                if (wp.getName().toLowerCase(Locale.getDefault())
                        .contains(charText)||wp.getTag().toLowerCase(Locale.getDefault())
                        .contains(charText)||wp.getData().toLowerCase(Locale.getDefault())
                        .contains(charText)||wp.getTimewrite().toLowerCase(Locale.getDefault())
                        .contains(charText)) {
                    noteList.add(wp);
                }
            }
        }
        notifyDataSetChanged();
    }

}
