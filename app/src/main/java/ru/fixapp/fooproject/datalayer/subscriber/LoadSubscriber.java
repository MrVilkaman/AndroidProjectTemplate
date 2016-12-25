package ru.fixapp.fooproject.datalayer.subscriber;


import ru.fixapp.fooproject.presentationlayer.fragments.core.BaseView;
import ru.fixapp.fooproject.presentationlayer.fragments.core.BindType;

/**
 * Created by root on 16.03.16.
 */
public class LoadSubscriber<V extends BaseView, T> extends ViewSubscriber<V,T> {


	public LoadSubscriber(V view) {
		super(view);
		if (needProgress()) {
			view().showProgress();
		}
	}

	@Override
	@SuppressWarnings("unchecked")
	public void onNext(T item) {
		V view = view();
		if (view instanceof BindType) {
			((BindType<T>) view).bind(item);
		}
	}

	@Override
	public void onError(Throwable e) {
		BaseView view = view();
		if (view  == null) return;

		if (needProgress()) {
			view.hideProgress();
		}
		super.onError(e);
	}

	protected boolean needProgress() {
		return true;
	}

	@Override
	public void onCompleted() {
		super.onCompleted();
		if (needProgress()) {
			view().hideProgress();
		}
	}

}
