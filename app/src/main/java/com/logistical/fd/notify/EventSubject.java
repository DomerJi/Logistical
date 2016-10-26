package com.logistical.fd.notify;

import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * 具体的主题角色的实现
 */
public class EventSubject implements EventSubjectInterface{

private static final String TAG="EventSubject";

    private Map<String,ArrayList<EventObserver>> mEventObservers=new HashMap<String,ArrayList<EventObserver>>();

    private static volatile EventSubject mEventSubject;

    private EventSubject(){

 

    }

 

    public synchronized static EventSubject getInstance(){

        if(mEventSubject ==null){

            mEventSubject =new EventSubject();

        }

        return mEventSubject;

    }

 

    @Override

    public void registerObserver(String eventType,EventObserver observer) {

        synchronized (mEventObservers){

            ArrayList<EventObserver> eventObservers = mEventObservers.get(eventType);

            if (eventObservers == null) {

                eventObservers = new ArrayList<EventObserver>();

                mEventObservers.put(eventType, eventObservers);

            }

            if(eventObservers.contains(observer)) {

                return;

            }

            eventObservers.add(observer);

        }

 

    }

 

    @Override

    public void removeObserver(String eventType,EventObserver observer) {

        synchronized (mEventObservers){

            int index = mEventObservers.get(eventType).indexOf(observer);

            if (index >= 0) {

                mEventObservers.remove(observer);

            }

        }

    }

 

    @Override

    public void notifyObserver(String eventType) {

        if(mEventObservers!=null && mEventObservers.size()>0 && eventType!=null){

            ArrayList<EventObserver> eventObservers=mEventObservers.get(eventType);

            if(eventObservers!=null){

                for(EventObserver observer:eventObservers){

                    observer.dispatchChange(eventType);

                }

            }else{

                Log.e(TAG, "eventObservers is null," + eventType + " may be not register");

            }

        }

 

    }

}
