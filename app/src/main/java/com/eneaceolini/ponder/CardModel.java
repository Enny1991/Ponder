

package com.eneaceolini.ponder;

import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.maps.model.LatLng;

public class CardModel implements Parcelable {


    private final String EMPTY = "";
    private String url;
    private String from;
    private String date;
    private String time;
    private String abst;
	private String   title;
    private Typeface typeFace;
    private Typeface typeFace2;
    private String   spk;
    private String   series;
	private String   description;
	private Bitmap cardImageDrawable;
	private Drawable cardLikeImageDrawable;
	private Drawable cardDislikeImageDrawable;
    private LatLng latLng;

    private OnCardDimissedListener mOnCardDimissedListener = null;

    private OnClickListener mOnClickListener = null;

    public interface OnCardDimissedListener {
        void onLike();
        void onDislike();
    }

    public interface OnClickListener {
        void OnClickListener();
    }

    public CardModel(Parcel parcel) {
        this.url = parcel.readString();
        this.title = parcel.readString();
        this.series = parcel.readString();
        this.from = parcel.readString();
        this.date = parcel.readString();
        this.time = parcel.readString();
        this.abst = parcel.readString();
        this.title = parcel.readString();
        this.spk = parcel.readString();
        this.latLng = new LatLng(parcel.readDouble(),parcel.readDouble());
    }

    public void writeToParcel(Parcel out, int flags) {
        out.writeString(url);
        out.writeString(title);
        out.writeString(series);
        out.writeString(from);
        out.writeString(date);
        out.writeString(time);
        out.writeString(abst);
        out.writeString(title);
        out.writeString(spk);
        out.writeDouble(latLng.latitude);
        out.writeDouble(latLng.longitude);

    }

    public int describeContents() {
        return 0;  // TODO: Customise this generated block
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public CardModel createFromParcel(Parcel in) {
            return new CardModel(in);
        }
        public CardModel[] newArray(int size) {
            return new CardModel[size];
        }
    };

	public CardModel() {
		this(null,null,null,null,null,null,null, null, (Bitmap)null,(Typeface)null,(Typeface)null,(Double)null,(Double)null);
	}

	public CardModel(String url, String from, String date, String time, String abst,  String title, String spk,String series, Bitmap cardImage,Typeface typeFace,Typeface typeFace2,Double lat,Double lon) {

        this.url = url;
        this.title = title;
        this.series = series;
        this. from = from;
        this.date = date;
        this.time = time;
        this.abst = abst;
        this.title = title;
		this.spk = spk;
        this.series = series;
		this.cardImageDrawable = cardImage;
        this.typeFace = typeFace;
        this.typeFace2 =typeFace2;
        this.latLng = new LatLng(lat,lon);
	}

	/*public CardModel(String title, String description, Bitmap cardImage) {
		this.title = title;
		this.description = description;
		this.cardImageDrawable = new BitmapDrawable(null, cardImage);
	}*/
    public String geturl() {
        return url;
    }
    public String getFrom() {
        return from;
    }
    public String getDate() {
        return date;
    }
    public String getTime() {
        return time;
    }
    public String getAbst() {
        return abst;
    }
    public String getTitle() {
		return title;
	}
    public String getSpk() {
        return spk;
    }
    public String getSeries() {
        if(!series.isEmpty())return series;
        else return EMPTY;
    }
    public Typeface getTF(){return typeFace;}
    public Typeface getTF2(){return typeFace2;}
    public LatLng getLatLng(){return latLng;}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Bitmap getCardImageDrawable() {
		return cardImageDrawable;
	}

	public void setCardImageDrawable(Bitmap cardImageDrawable) {
		this.cardImageDrawable = cardImageDrawable;
	}

	public Drawable getCardLikeImageDrawable() {
		return cardLikeImageDrawable;
	}

	public void setCardLikeImageDrawable(Drawable cardLikeImageDrawable) {
		this.cardLikeImageDrawable = cardLikeImageDrawable;
	}

	public Drawable getCardDislikeImageDrawable() {
		return cardDislikeImageDrawable;
	}

	public void setCardDislikeImageDrawable(Drawable cardDislikeImageDrawable) {
		this.cardDislikeImageDrawable = cardDislikeImageDrawable;
	}

    public void setOnCardDimissedListener( OnCardDimissedListener listener ) {
        this.mOnCardDimissedListener = listener;
    }

    public OnCardDimissedListener getOnCardDimissedListener() {
       return this.mOnCardDimissedListener;
    }


    public void setOnClickListener( OnClickListener listener ) {
        this.mOnClickListener = listener;
    }

    public OnClickListener getOnClickListener() {
        return this.mOnClickListener;
    }
}