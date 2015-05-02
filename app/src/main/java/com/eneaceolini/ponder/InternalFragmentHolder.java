package com.eneaceolini.ponder;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnticipateOvershootInterpolator;
import android.view.animation.OvershootInterpolator;
import android.view.animation.TranslateAnimation;


public class InternalFragmentHolder extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    public FragmentHolderTest mainActivity;
    public OnlyCardFragment ocf ;
    public NextPageFragment npf = new NextPageFragment();
    public  ToolBox toolBox;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment InternalFragmentHolder.
     */

    // TODO: Rename and change types and number of parameters
    public static InternalFragmentHolder newInstance(String param1, String param2) {
        InternalFragmentHolder fragment = new InternalFragmentHolder();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public InternalFragmentHolder() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_internal_fragment_holder, container, false);
        toolBox = ToolBox.getInstance();
        toolBox.callToSwitch = this;
        ocf = new OnlyCardFragment(this);
        if(savedInstanceState == null){
            npf.mainActivity = this.mainActivity;
            getChildFragmentManager().beginTransaction()
                    .add(R.id.internal_container, npf)
                    .commit();
        }


        return rootView;
    }


    public void switchFragments(){

        toolBox.toGetIn.setVisibility(View.VISIBLE);
        TranslateAnimation anim = new TranslateAnimation(1000,0,0,0);
        anim.setDuration(400);
        anim.setInterpolator(new OvershootInterpolator());
        toolBox.toGetIn.startAnimation(anim);

    }

    public void switchFragments2(){

        getChildFragmentManager().beginTransaction()
                .setCustomAnimations(
                        R.anim.transition3,
                        R.anim.transition4)
                .replace(R.id.internal_container,npf)
                .commit();

        //TODO
    }



}
