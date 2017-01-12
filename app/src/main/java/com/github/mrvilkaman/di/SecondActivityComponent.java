package com.github.mrvilkaman.di;

import com.github.mrvilkaman.di.modules.activity.CommonActivityModule;
import com.github.mrvilkaman.di.modules.activity.DrawerEmptyModule;
import com.github.mrvilkaman.di.modules.activity.ToolbarModule;
import com.github.mrvilkaman.presentationlayer.activities.SecondActivity;

import dagger.Component;

@PerActivity
@Component(dependencies = AppComponent.class,
		modules = {CommonActivityModule.class, DrawerEmptyModule.class, ToolbarModule.class})
public interface SecondActivityComponent extends ActivityCoreComponent {

	void inject(SecondActivity activity);
}