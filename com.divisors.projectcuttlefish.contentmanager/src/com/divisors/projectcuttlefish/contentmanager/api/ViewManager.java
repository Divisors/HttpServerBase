package com.divisors.projectcuttlefish.contentmanager.api;

import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import com.divisors.projectcuttlefish.uac.InvalidSessionException;
import com.divisors.projectcuttlefish.uac.NotPermittedException;
import com.divisors.projectcuttlefish.uac.Session;
import com.divisors.projectcuttlefish.uac.User;

public class ViewManager {
	protected static ViewManager instance = new ViewManager();
	
	public static ViewManager getInstance() {
		return instance;
	}
	
	protected ConcurrentHashMap<String, View> views = new ConcurrentHashMap<>();
	
	public ViewManager registerView(View view) {
		return registerView(view.getName(), view);
	}
	
	public ViewManager registerView(String name, View view) {
		views.put(name, view);
		return this;
	}
	
	public ViewManager deregisterView(View view) {
		views.remove(view.getName());
		return this;
	}
	public ViewManager deregisterView(String name) {
		views.remove(name);
		return this;
	}
	
	public String generate(String view, User user, Optional<Session> session) throws InvalidSessionException, NotPermittedException, IOException {
//		return views.get(view).generate(user, session);
		return null;//TODO finish
	}
	//TODO finish
}
