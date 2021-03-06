package com.github.mrvilkaman.presentationlayer.fragments.testfrags;

import com.github.mrvilkaman.di.ActivityCoreComponent;
import com.github.mrvilkaman.presentationlayer.fragments.core.BaseFragment;

public class Frag5ScreenFragment extends FragBaseScreenFragment {


	public static Frag5ScreenFragment open() {
		return new Frag5ScreenFragment();
	}

	@Override
	protected int getNumber() {
		return 5;
	}

	@Override
	protected BaseFragment nextFragment() {
		return null;
	}

	@Override
	protected int getIcon() {
		return android.R.drawable.ic_media_pause;
	}

	@Override
	public void daggerInject() {
		ActivityCoreComponent component = getComponent(ActivityCoreComponent.class);
		DaggerFragScreenComponent.builder().activityCoreComponent(component).build().inject(this);
	}
}
