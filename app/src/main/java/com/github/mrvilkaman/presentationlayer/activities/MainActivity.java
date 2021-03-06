package com.github.mrvilkaman.presentationlayer.activities;

import com.github.mrvilkaman.R;
import com.github.mrvilkaman.di.ActivityComponent;
import com.github.mrvilkaman.di.AppComponent;
import com.github.mrvilkaman.di.DaggerActivityComponent;
import com.github.mrvilkaman.di.modules.activity.CommonActivityModule;
import com.github.mrvilkaman.di.modules.activity.DrawerModule;
import com.github.mrvilkaman.di.modules.activity.FragmentModule;
import com.github.mrvilkaman.di.modules.activity.ToolbarModule;
import com.github.mrvilkaman.presentationlayer.app.App;
import com.github.mrvilkaman.presentationlayer.fragments.cachework.CacheworkScreenFragment;
import com.github.mrvilkaman.presentationlayer.fragments.testfrags.DrawerScreenFragment;
import com.github.mrvilkaman.presentationlayer.utils.DevUtils;

import javax.inject.Inject;


public class MainActivity extends BaseActivity<ActivityComponent,SecondActivityPresenter> {

	@Override
	protected ActivityComponent createComponent() {
		AppComponent appComponent = DevUtils.getComponent(App.get(this),AppComponent.class);
		CommonActivityModule commonActivityModule =
				new CommonActivityModule(this, this, getRootView(), CacheworkScreenFragment::open);
		return DaggerActivityComponent.builder()
				.appComponent(appComponent)
				.commonActivityModule(commonActivityModule)
				.fragmentModule(new FragmentModule(getSupportFragmentManager(),getContainerID()))
				.toolbarModule(new ToolbarModule(this))
				.drawerModule(new DrawerModule(DrawerScreenFragment::open,false))
				.build();
	}

	@Override
	public void injectMe(ActivityComponent activityComponent) {
		activityComponent.inject(this);
	}

	@Override
	protected void afterOnCreate() {
	}

	@Override
	@Inject
	public void setPresenter(SecondActivityPresenter presenter) {
		super.setPresenter(presenter);
	}

	protected int getActivityLayoutResourceID() {
		return R.layout.cleanbase_activity_content_with_toolbar_and_drawer;
	}
}
