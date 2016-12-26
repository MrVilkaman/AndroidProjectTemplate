package com.github.mrvilkaman.presentationlayer.resolution.navigation;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;

import com.github.mrvilkaman.core.R;
import com.github.mrvilkaman.presentationlayer.activities.BaseActivityView;
import com.github.mrvilkaman.presentationlayer.fragments.core.BaseFragment;
import com.github.mrvilkaman.presentationlayer.resolution.ProvideFragmentCallback;
import com.github.mrvilkaman.presentationlayer.resolution.UIResolver;
import com.github.mrvilkaman.presentationlayer.resolution.drawer.LeftDrawerHelper;
import com.github.mrvilkaman.presentationlayer.resolution.fragments.FragmentResolver;
import com.github.mrvilkaman.presentationlayer.resolution.fragments.MyFragmentResolverCallback;
import com.github.mrvilkaman.presentationlayer.resolution.toolbar.MyToolbarResolverCallback;
import com.github.mrvilkaman.presentationlayer.resolution.toolbar.ToolbarResolver;

import java.util.concurrent.TimeUnit;

import static rx.Observable.just;


public class NavigationResolverImpl implements NavigationResolver {

	protected boolean doubleBackToExitPressedOnce;
	private Activity currentActivity;
	private FragmentResolver fragmentManager;
	private LeftDrawerHelper drawerHelper;
	private ToolbarResolver toolbarResolver;
	private UIResolver uiResolver;
	private BaseActivityView activityView;

	private ProvideFragmentCallback callback;

	public NavigationResolverImpl(Activity currentActivity, FragmentResolver fragmentManager,
								  LeftDrawerHelper drawerHelper, ToolbarResolver toolbarResolver,
								  UIResolver uiResolver, BaseActivityView activityView,
								  ProvideFragmentCallback callback) {
		this.currentActivity = currentActivity;
		this.fragmentManager = fragmentManager;
		this.drawerHelper = drawerHelper;
		this.toolbarResolver = toolbarResolver;
		this.uiResolver = uiResolver;
		this.activityView = activityView;

		this.callback = callback;
	}

	@Override
	public void init() {

		fragmentManager.setCallback(new MyFragmentResolverCallback(toolbarResolver));

		MyToolbarResolverCallback callback =
				new MyToolbarResolverCallback(fragmentManager, drawerHelper, activityView,
						toolbarResolver, this);
		toolbarResolver.setCallback(callback);


		if (!fragmentManager.hasFragment()) {
			fragmentManager.showRootFragment(createStartFragment());

			if (drawerHelper.hasDrawer()) {
				fragmentManager.addDrawer(drawerHelper.getDrawerContentFrame(),
						drawerHelper.getDrawerFragment());
			}
		}
	}

	@Override
	public BaseFragment createStartFragment() {
		return callback.createFragment();
	}

	@Override
	public void onBackPressed() {
		if (fragmentManager.processBackFragment()) {
			activityView.hideProgress();
			if (fragmentManager.onBackPressed()) {
				toolbarResolver.updateIcon();
			} else {
				exit();
			}
		}
	}

	@Override
	public void showFragment(BaseFragment fragment) {
		LeftDrawerHelper.LeftDrawerHelperCallback callback = () -> {
			toolbarResolver.clear();
			fragmentManager.showFragment(fragment);
		};
		close(callback, drawerHelper);
	}

	@Override
	public void showRootFragment(BaseFragment fragment) {
		LeftDrawerHelper.LeftDrawerHelperCallback callback = () -> {
			toolbarResolver.clear();
			fragmentManager.showRootFragment(fragment);
		};
		close(callback, drawerHelper);
	}

	@Override
	public void showFragmentWithoutBackStack(BaseFragment fragment) {
		LeftDrawerHelper.LeftDrawerHelperCallback callback = () -> {
			toolbarResolver.clear();
			fragmentManager.showFragmentWithoutBackStack(fragment);
		};
		LeftDrawerHelper drawerHelper = this.drawerHelper;
		close(callback, drawerHelper);
	}

	private void close(LeftDrawerHelper.LeftDrawerHelperCallback callback,
					   LeftDrawerHelper drawerHelper) {
		if (drawerHelper.isOpen()) {
			drawerHelper.close(callback);
		} else {
			callback.onClose();
		}
	}

	@Override
	public void setTargetFragment(int code) {
		fragmentManager.setTargetFragmentCode(code);
	}

	@Override
	public void openActivity(Class<? extends Activity> aClass) {
		currentActivity.startActivity(new Intent(currentActivity, aClass));
		currentActivity.finish();
	}

	@Override
	public void startActivityForResult(Intent intent, int requestCode) {
		currentActivity.startActivityForResult(intent, requestCode);
	}

	@Override
	public void startActivityForResultFormFragment(Intent intent, int requestCode) {
		fragmentManager.startActivityForResult(intent, requestCode);
	}

	@Override
	public void openLinkInBrowser(String link) {
		final String url;
		if (!link.startsWith("http://") && !link.startsWith("https://"))
			url = "http://" + link;
		else
			url = link;

		Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
		currentActivity.startActivity(browserIntent);
	}


	@Override
	public void back() {
		if (drawerHelper.isOpen()) {
			drawerHelper.close();
		} else {
			onBackPressed();
			toolbarResolver.updateIcon();
		}
	}

	void exit() {
		if (doubleBackToExitPressedOnce) {
			currentActivity.finish();
		} else {
			uiResolver.showToast(R.string.exit_toast);
			doubleBackToExitPressedOnce = true;
			just(null).delay(1000, TimeUnit.MILLISECONDS)
					.subscribe(o -> doubleBackToExitPressedOnce = false);
		}
	}

}
