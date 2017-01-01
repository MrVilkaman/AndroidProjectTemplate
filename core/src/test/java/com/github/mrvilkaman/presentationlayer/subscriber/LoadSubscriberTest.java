package com.github.mrvilkaman.presentationlayer.subscriber;

import com.github.mrvilkaman.presentationlayer.fragments.core.BaseView;
import com.github.mrvilkaman.presentationlayer.fragments.core.BindType;
import com.github.mrvilkaman.testsutils.BaseTestCase;

import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;

import rx.subjects.PublishSubject;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;


public class LoadSubscriberTest extends BaseTestCase {

	@Mock BaseView view;
	private PublishSubject<Object> subject = PublishSubject.create();

	@Test
	public void testProgress_onSuccess() {
		subject.subscribe(new LoadSubscriber<>(view));
		subject.onCompleted();

		InOrder inOrder = Mockito.inOrder(view);
		inOrder.verify(view)
				.showProgress();
		inOrder.verify(view)
				.hideProgress();
	}

	@Test
	public void testProgress_OnError() {
		Exception exception = mock(Exception.class);

		subject.subscribe(new LoadSubscriber<>(view));
		subject.onError(exception);

		InOrder inOrder = Mockito.inOrder(view);
		inOrder.verify(view)
				.showProgress();
		inOrder.verify(view)
				.hideProgress();
		inOrder.verify(view)
				.handleError(exception);
	}

	@Test
	public void testNoNeedProgress_onSuccess() {
		subject.subscribe(new WithoutProgressSubscriber());
		subject.onCompleted();

		InOrder inOrder = Mockito.inOrder(view);
		inOrder.verify(view, never())
				.showProgress();
		inOrder.verify(view, never())
				.hideProgress();
	}

	@Test
	public void testNoNeedProgress_OnError() {
		Exception exception = mock(Exception.class);

		subject.subscribe(new WithoutProgressSubscriber());
		subject.onError(exception);

		InOrder inOrder = Mockito.inOrder(view);
		inOrder.verify(view, never())
				.showProgress();
		inOrder.verify(view, never())
				.hideProgress();
		inOrder.verify(view)
				.handleError(exception);
	}

	@Test
	public void testSkipError() {
		Exception exception = mock(Exception.class);

		LoadSubscriber<BaseView, Object> subscriber = new LoadSubscriber<>(view);
		subscriber.skipNextError();

		subject.subscribe(subscriber);
		subject.onError(exception);

		verify(view, never()).handleError(exception);
	}


	@Test
	public void testOnNext_bindString() {
		TestView v = mock(TestView.class);

		LoadSubscriber<BaseView, Object> subscriber = new LoadSubscriber<>(v);

		subject.subscribe(subscriber);
		subject.onNext("qwer");
		subject.onNext("qweeee");
		subject.onNext(new Object());

		InOrder inOrder = Mockito.inOrder(v);
		inOrder.verify(v).bind(eq("qwer"));
		inOrder.verify(v).bind(eq("qweeee"));
	}

	@Test
	public void testOnNext() {

		ViewSubscriber<BaseView, Object> subscriber = new ViewSubscriber<>(view);

		subject.subscribe(subscriber);
		subject.onNext(new Object());

	}


	private class WithoutProgressSubscriber extends LoadSubscriber<BaseView, Object> {
		public WithoutProgressSubscriber() {
			super(LoadSubscriberTest.this.view);
		}

		@Override
		protected boolean needProgress() {
			return false;
		}
	}

	public interface TestView extends BaseView, BindType<String> {

	}
}