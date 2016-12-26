package com.github.mrvilkaman.presentationlayer.resolution.navigation;

import android.app.Activity;
import android.content.Intent;

import com.github.mrvilkaman.presentationlayer.fragments.core.BaseFragment;

public interface NavigationResolver {

	void init();

	BaseFragment createStartFragment();

	void showFragment(BaseFragment fragment);

	void showRootFragment(BaseFragment fragment);

	void showFragmentWithoutBackStack(BaseFragment fragment);

	void setTargetFragment(int cropPhotoRequestCode);

	void onBackPressed();

	void back();

	void openActivity(Class<? extends Activity> aClass);

	void startActivityForResultFormFragment(Intent intent, int requestCode);

	void startActivityForResult(Intent intent, int requestCode);

}