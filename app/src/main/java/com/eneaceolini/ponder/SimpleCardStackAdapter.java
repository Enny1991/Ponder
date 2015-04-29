package com.eneaceolini.ponder;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


public final class SimpleCardStackAdapter extends CardStackAdapter {
    private final int MAX_SPEAKER_LENGTH=25;

	public SimpleCardStackAdapter(Context mContext) {
		super(mContext);
	}

	@Override
	public View getCardView(int position, CardModel model, View convertView, ViewGroup parent) {
		if(convertView == null) {
			LayoutInflater inflater = LayoutInflater.from(getContext());
			convertView = inflater.inflate(R.layout.std_card_inner, parent, false);
			assert convertView != null;
		}
        Bitmap bitmap =model.getCardImageDrawable();
        Bitmap output ;
        try {
            output = Bitmap.createBitmap(bitmap.getWidth(), bitmap
                    .getHeight(), Bitmap.Config.ARGB_8888);
        }catch(Exception e){

            output = Bitmap.createScaledBitmap(bitmap,bitmap.getWidth()/2, bitmap.getHeight()/2, true);
            output = Bitmap.createBitmap(output.getWidth(), output
                    .getHeight(), Bitmap.Config.ARGB_8888);
        }
        Canvas canvas = new Canvas(output);


        BitmapShader shader;
        shader = new BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);

        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setShader(shader);
        int radius = 20;
        RectF rect = new RectF(0.0f, 0.0f, bitmap.getWidth(), bitmap.getHeight());

        // rect contains the bounds of the shape
        // radius is the radius in pixels of the rounded corners
        // paint contains the shader that will texture the shape
        canvas.drawRoundRect(rect, radius, radius, paint);

		((ImageView) convertView.findViewById(R.id.image)).setImageBitmap(output);
        if(model.getSpk().length()>MAX_SPEAKER_LENGTH)
		((TextView) convertView.findViewById(R.id.spk)).setText(model.getSpk().substring(0,MAX_SPEAKER_LENGTH-1));
        else ((TextView) convertView.findViewById(R.id.spk)).setText(model.getSpk());
        if(model.getSpk().equals(" ")) ((TextView) convertView.findViewById(R.id.spk)).setVisibility(View.GONE);
        ((TextView) convertView.findViewById(R.id.spk)).setTypeface(model.getTF());
        if(!model.getSeries().isEmpty()) ((TextView) convertView.findViewById(R.id.series)).setText("");
		else ((TextView) convertView.findViewById(R.id.series)).setText(model.getSeries());
        //Log.d("TEXT", model.getSeries());
        ((TextView) convertView.findViewById(R.id.series)).setTypeface(model.getTF2());
        ((TextView) convertView.findViewById(R.id.title)).setText(model.getTitle());
        ((TextView) convertView.findViewById(R.id.title)).setTypeface(model.getTF2());

        return convertView;
	}
}
