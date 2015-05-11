package com.eneaceolini.ponder;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

/**
 * Created by Enea on 28/04/15.
 */
public class OnlyCardFragment extends Fragment {

    private static final String ARG_PARAM1 = "card";
    private static final String ARG_PARAM2 = "param2";

    PositionFirstCard myPosition;
    int width,height;
    InternalFragmentHolder a;
    ToolBox toolBox;
    CardModel obj;

    public static OnlyCardFragment newInstance(CardModel card) {
        OnlyCardFragment fragment = new OnlyCardFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_PARAM1, card);
        //args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (getArguments() != null) {
            obj = getArguments().getParcelable(ARG_PARAM1);
        }
        toolBox = ToolBox.getInstance();
        View rootView = inflater.inflate(R.layout.simple_card,container,false);
        ((ImageView) rootView.findViewById(R.id.img_roll)).setImageBitmap(toolBox.myPhoto);
        //((ImageView) rootView.findViewById(R.id.img_roll)).setX(myPosition.x);
        //((ImageView) rootView.findViewById(R.id.img_roll)).setY(myPosition.y);
        //Bitmap bmp = myPosition.card.getCardImageDrawable();
        //bmp = Bitmap.createScaledBitmap(bmp,myPosition.width/2,myPosition.high/2,true);
        //((ImageView) rootView.findViewById(R.id.img_roll)).setImageBitmap(bmp);

        return rootView;
    }

    public Bitmap getResizedBitmap(Bitmap bm, int newHeight, int newWidth) {
        int width = bm.getWidth();
        int height = bm.getHeight();
        //Log.d("original height:", "" + height + " instead of" + newHeight);
        //Log.d("original width:", "" + width + " instead of" + newWidth);
        int scale = 1;
        Matrix matrix = new Matrix();
        Bitmap resizedBitmap;
        if (newHeight < height && newWidth < width) {
            resizedBitmap = bm;
        } else {
            if ((newHeight - height) > (newWidth - width)) {
                scale = (int) Math.ceil(newHeight / height) + 1;
                resizedBitmap = Bitmap.createScaledBitmap(bm, width * scale, newHeight, true);
            } else {
                scale = (int) Math.ceil(newWidth / width) + 1;
                resizedBitmap = Bitmap.createScaledBitmap(bm, newWidth, height * scale, true);
            }
            // "RECREATE" THE NEW BITMAP

        }

        //Log.d("new height:", "" + resizedBitmap.getHeight());
        //Log.d("new width:", "" + resizedBitmap.getWidth());
        resizedBitmap = Bitmap.createBitmap(resizedBitmap, 0, 0, newWidth, newHeight);

        return resizedBitmap;
    }


    @Override
    public void onPause() {
        super.onPause();
    }



}
