package com.github.mrvilkaman.datalayer.provider;


import com.github.mrvilkaman.domainlayer.models.DataErrorWrapper;

import rx.Observable;

public interface CacheworkDp {
	Observable<DataErrorWrapper<String>> observeString();
	void refreshString();
}
