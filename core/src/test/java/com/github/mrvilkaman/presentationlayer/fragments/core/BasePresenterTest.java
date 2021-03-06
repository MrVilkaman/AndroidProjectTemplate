package com.github.mrvilkaman.presentationlayer.fragments.core;

import com.github.mrvilkaman.domainlayer.providers.SchedulersProvider;
import com.github.mrvilkaman.presentationlayer.subscriber.LoadSubscriber;
import com.github.mrvilkaman.presentationlayer.subscriber.ViewSubscriber;
import com.github.mrvilkaman.testsutils.BaseTestCase;
import com.github.mrvilkaman.testsutils.TestSchedulers;

import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.List;

import rx.observers.TestSubscriber;
import rx.subjects.PublishSubject;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;


public class BasePresenterTest extends BaseTestCase {

	@Mock BaseView mockView;
	private SchedulersProvider prov = Mockito.spy(new TestSchedulers());
	private BasePresenter<BaseView> presenter;

	@Override
	public void init() {
		presenter = new BasePresenter<>();
		presenter.setSchedulersProvider(prov);
	}

	@Test
	public void testSetView() {
		BaseView view = presenter.view();
		assertThat(view).isNull();

		// Act
		presenter.setView(mockView);

		// Assert
		BaseView view2 = presenter.view();
		assertThat(view2).isNotNull()
				.isEqualTo(mockView);
	}

	@Test
	public void testSubscribe_setView() {
		// Arrange
		ViewSubscriber subscriber = Mockito.spy(new ViewSubscriber());
		PublishSubject<Object> obs = PublishSubject.create();

		presenter.setView(mockView);
		// Act
		presenter.subscribeUI(obs, subscriber);

		// Assert
		verify(subscriber).setView(mockView);
	}

	@Test
	public void testSubscribe_setView_load() {
		// Arrange
		LoadSubscriber subscriber = Mockito.spy(new LoadSubscriber());
		PublishSubject<Object> obs = PublishSubject.create();
		presenter.setView(mockView);

		// Act
		presenter.subscribeUI(obs, subscriber);

		// Assert
		verify(subscriber).setView(mockView);
	}


	@Test
	public void testSubscribe_afterDetachAndAttachAgain() {
		// Arrange
		PublishSubject<Object> obs = PublishSubject.create();
		TestSubscriber<Object> subscriber = new TestSubscriber<>();

		// Act
		presenter.onViewAttached();
		presenter.onViewDetached();
		presenter.onViewAttached();

		presenter.subscribeUI(obs, subscriber);
		obs.onNext("1");
		obs.onCompleted();
		obs.onNext("2");
		obs.onNext("3");

		// Assert
		subscriber.assertValue("1");
		subscriber.assertValueCount(1);
		subscriber.assertUnsubscribed();
	}

	@Test
	public void testSubscribe_Subscription() {
		// Arrange
		PublishSubject<Object> obs = PublishSubject.create();
		TestSubscriber<Object> subscriber = new TestSubscriber<>();

		// Act
		presenter.subscribeUI(obs, subscriber);
		obs.onNext("1");
		obs.onCompleted();

		// Assert
		subscriber.assertValue("1");
		subscriber.assertCompleted();
		subscriber.assertValueCount(1);
	}

	@Test
	public void testSubscribe_callUnsubscribeFromDetach() {
		// Arrange
		PublishSubject<Object> obs = PublishSubject.create();
		TestSubscriber<Object> subscriber = new TestSubscriber<>();

		// Act
		presenter.onViewAttached();
		presenter.subscribeUI(obs, subscriber);
		obs.onNext("1");
		presenter.onViewDetached();
		obs.onNext("2");
		obs.onNext("3");

		// Assert
		subscriber.assertValue("1");
		subscriber.assertValueCount(1);
		subscriber.assertUnsubscribed();
	}

	@Test
	public void testSubscribeUI() {
		// Arrange
		PublishSubject<Object> obs = PublishSubject.create();
		TestSubscriber<Object> subscriber = new TestSubscriber<>();

		// Act
		presenter.onViewAttached();
		presenter.subscribeUI(obs, subscriber);
		obs.onNext("1");
		presenter.onViewDetached();
		obs.onNext("2");
		obs.onNext("3");

		// Assert
		subscriber.assertValue("1");
		subscriber.assertValueCount(1);
		subscriber.assertUnsubscribed();

		Mockito.verify(prov)
				.mainThread();
	}

	@Test
	public void testSubscribe_more_then_one_observer_in_wort() {
		// Arrange
		PublishSubject<Object> obs = PublishSubject.create();
		TestSubscriber<Object> subscriber = new TestSubscriber<>();
		PublishSubject<Object> obs2 = PublishSubject.create();
		TestSubscriber<Object> subscriber2 = new TestSubscriber<>();
		PublishSubject<Object> obs3 = PublishSubject.create();
		TestSubscriber<Object> subscriber3 = new TestSubscriber<>();

		Holders holders = new Holders(new Holder(obs, subscriber), new Holder(obs2, subscriber3),
				new Holder(obs3, subscriber3));
		// Act
		presenter.onViewAttached();
		presenter.subscribeUI(obs, subscriber);
		presenter.subscribeUI(obs2, subscriber2);
		presenter.subscribeUI(obs3, subscriber3);
		holders.onNext("1");
		presenter.onViewDetached();
		holders.onNext("2");
		holders.onNext("3");

		// Assert
		holders.assertIt();
		Mockito.verify(prov,atLeastOnce())
				.mainThread();
	}

	private static class Holders {

		private final List<Holder> holders;

		private Holders(Holder... holders) {
			this.holders = Arrays.asList(holders);
		}

		void onNext(Object o) {
			for (Holder holder : holders) {
				holder.onNext(o);
			}
		}

		void assertIt() {
			holders.forEach(Holder::assertIt);
		}
	}

	private static class Holder {

		private final PublishSubject<Object> obs;
		private final TestSubscriber<Object> subscriber;

		private Holder(PublishSubject<Object> obs, TestSubscriber<Object> subscriber) {
			this.obs = obs;
			this.subscriber = subscriber;
		}

		void onNext(Object o) {
			obs.onNext(o);
		}

		void assertIt() {
			subscriber.assertValue("1");
			subscriber.assertValueCount(1);
			subscriber.assertUnsubscribed();
		}
	}


}