package ru.fixapp.fooproject.presentationlayer.activities;


import android.content.Context;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import dagger.Component;
import dagger.Module;
import dagger.Provides;
import ru.fixapp.fooproject.di.AppComponent;
import ru.fixapp.fooproject.di.PerActivity;
import ru.fixapp.fooproject.domainlayer.providers.SchedulersProvider;
import ru.fixapp.fooproject.presentationlayer.resolution.FragmentResolver;
import ru.fixapp.fooproject.presentationlayer.resolution.FragmentResolverImpl;
import ru.fixapp.fooproject.presentationlayer.resolution.LeftDrawerHelper;
import ru.fixapp.fooproject.presentationlayer.resolution.NavigationResolver;
import ru.fixapp.fooproject.presentationlayer.resolution.NavigationResolverImpl;
import ru.fixapp.fooproject.presentationlayer.resolution.ThrowableResolver;
import ru.fixapp.fooproject.presentationlayer.resolution.ThrowableResolverImpl;
import ru.fixapp.fooproject.presentationlayer.resolution.UIResolver;
import ru.fixapp.fooproject.presentationlayer.resolution.UIResolverImpl;
import ru.fixapp.fooproject.presentationlayer.toolbar.IToolbar;
import ru.fixapp.fooproject.presentationlayer.toolbar.MyToolbarImpl;
import ru.fixapp.fooproject.presentationlayer.toolbar.ToolbarMenuHelper;

@PerActivity
@Component(dependencies = AppComponent.class, modules = ActivityComponent.ActivityModule.class)
public interface ActivityComponent {
	void inject(MainActivity activity);

	SchedulersProvider getSchedulersProvider();

	UIResolver getUIResolver();

	ThrowableResolver getThrowableResolver();

	NavigationResolver getNavigationResolver();

	IToolbar getIToolbar();

	BaseActivityView provideBaseActivityView();

	@Module
			// todo разбить на несколько модулей, для включения\выключения по ненадобности
	class ActivityModule {

		private View view;
		private AppCompatActivity activity;
		private FragmentManager fm;
		private int contentId;
		private Runnable updateToolbarButtonsCallback;
		private Toolbar toolbar;
		private MyToolbarImpl.ToolbarCallbacks toolbarCallback;
		private BaseActivityView baseActivityView;

		public ActivityModule(View view, AppCompatActivity activity,BaseActivityView baseActivityView, FragmentManager fm,
							  int contentId, Runnable updateToolbarButtonsCallback, Toolbar toolbar,
							  MyToolbarImpl.ToolbarCallbacks toolbarCallback) {
			this.view = view;
			this.activity = activity;
			this.fm = fm;
			this.contentId = contentId;
			this.updateToolbarButtonsCallback = updateToolbarButtonsCallback;
			this.toolbar = toolbar;
			this.toolbarCallback = toolbarCallback;
			this.baseActivityView = baseActivityView;
		}


		@Provides
		@PerActivity
		public UIResolver createUiResolver(Context context) {
			return new UIResolverImpl(context, view);
		}

		@Provides
		@PerActivity
		public ThrowableResolver createThrowableResolver(UIResolver ui) {
			return new ThrowableResolverImpl(ui);
		}

		@Provides
		@PerActivity
		public NavigationResolver createNavigationResolver(FragmentResolver fragmentResolver,
														   LeftDrawerHelper drawer,
														   ToolbarResolver toolbarResolver,
														   UIResolver ui) {
			return new NavigationResolverImpl(activity, fragmentResolver, drawer, toolbarResolver,ui,baseActivityView);
		}

		@Provides
		@PerActivity
		public LeftDrawerHelper createLeftDrawerHelper() {
			return new LeftDrawerHelperImpl(view);
		}


		@Provides
		@PerActivity
		public FragmentResolver createFragmentResolver() {
			return new FragmentResolverImpl(fm, contentId);
		}

		@Provides
		@PerActivity
		public ToolbarMenuHelper createToolbarMenuHelper() {
			return new ToolbarMenuHelper(updateToolbarButtonsCallback);
		}

		@Provides
		@PerActivity
		public IToolbar getToolbar(ToolbarMenuHelper toolbarMenuHelper) {
			return new MyToolbarImpl(toolbarMenuHelper, toolbar, toolbarCallback);
		}

		@Provides
		@PerActivity
		public LeftDrawerHelperImpl getLeftDrawerHelper() {
			return new LeftDrawerHelperImpl(view);
		}

		@Provides
		@PerActivity
		public ToolbarResolver getToolbarResolver(ToolbarMenuHelper menuHelper) {
			return new ToolbarResolverImpl(view, activity, menuHelper);
		}

		@Provides
		@PerActivity
		public BaseActivityView getBaseActivityView() {
			return baseActivityView;
		}

	}
}