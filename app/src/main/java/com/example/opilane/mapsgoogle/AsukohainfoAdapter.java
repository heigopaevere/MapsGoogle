package com.example.opilane.mapsgoogle;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

public class AsukohainfoAdapter implements GoogleMap.InfoWindowAdapter{

    private final View mWindow;
    private Context mContext;

    public AsukohainfoAdapter (Context context){
        mContext = context;
        mWindow = LayoutInflater.from(context).inflate(R.layout.asukoha_info_aken, null);
    }
    private void aknaTekst (Marker marker, View view){
        String title = marker.getTitle();
        TextView txtTiitel = view.findViewById(R.id.title);
        if (!title.equals("")){
            txtTiitel.setText(title);
        }
        String snippet = marker.getSnippet();
        TextView txtSnippet = view.findViewById(R.id.snippet);
        if (!snippet.equals("")){
            txtSnippet.setText(snippet);
        }
    }
    @Override
    public View getInfoWindow(Marker marker) {
        return null;
    }

    @Override
    public View getInfoContents(Marker marker) {
        return null;
    }
}
