package com.sheepybot.api.event;

public interface UpdateEvent<T>  {

    T getOldValue();

    T getNewValue();

}
