package cn.sk.skhstablet.fragment;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

//import android.support.design.widget.Snackbar;


public class BaseFragment extends Fragment {



	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return container;
	}
	public void showSnackar(View view ,String string){
		Snackbar.make(view, string, Snackbar.LENGTH_LONG)
				.show();
	}



}
