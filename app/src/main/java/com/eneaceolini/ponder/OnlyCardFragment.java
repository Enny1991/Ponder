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

    PositionFirstCard myPosition;
    int width,height;
    InternalFragmentHolder a;

    public OnlyCardFragment(InternalFragmentHolder a){
        this.a = a;
    }

    public OnlyCardFragment(PositionFirstCard a,int width,int height){
        myPosition = a;
        this.width = width;
        this.height = height;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.simple_card,container,false);
        ((ImageView) rootView.findViewById(R.id.img_roll)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                a.switchFragments2();
            }
        });
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

    /*
    @Override
    public void onPause() {
        b = loadBitmapFromView(getView());
        super.onPause();
    }

    public static Bitmap loadBitmapFromView(View v) {
        Bitmap b = Bitmap.createBitmap(v.getWidth(),
                v.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(b);
        v.layout(0, 0, v.getWidth(),
                v.getHeight());
        v.draw(c);
        return b;
    }

    @Override
    public void onDestroyView() {
        BitmapDrawable bd = new BitmapDrawable(b);
        getView().findViewById(R.id.pager).setBackground(bd);
        b = null;
        super.onDestroyView();
    }

    Bitmap b;
*/
}
