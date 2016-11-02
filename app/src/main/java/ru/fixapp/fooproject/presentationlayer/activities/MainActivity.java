package ru.fixapp.fooproject.presentationlayer.activities;

import android.support.v7.widget.Toolbar;

import butterknife.ButterKnife;
import ru.fixapp.fooproject.R;
import ru.fixapp.fooproject.di.AppComponent;
import ru.fixapp.fooproject.di.IHasComponent;
import ru.fixapp.fooproject.presentationlayer.app.App;


public class MainActivity extends BaseActivity implements IHasComponent<ActivityComponent> {

	private ActivityComponent screenComponent;

	@Override
	protected void injectDagger() {
		Toolbar toolbar = ButterKnife.findById(this, R.id.toolbar_actionbar);

		AppComponent appComponent = App.get(this).getAppComponent();
		screenComponent = DaggerActivityComponent.builder().appComponent(appComponent)
				.activityModule(new ActivityComponent.ActivityModule(getRootView(), this,this,
						getSupportFragmentManager(), getContainerID(), this::invalidateOptionsMenu,
						toolbar, this)).build();
		screenComponent.inject(this);
	}

	protected int getActivityLayoutResourceID() {
		return R.layout.activity_main_app;
	}

	@Override
	public ActivityComponent getComponent() {
		return screenComponent;
	}
}
