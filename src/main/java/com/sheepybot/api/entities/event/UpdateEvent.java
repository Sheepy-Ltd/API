package com.sheepybot.api.entities.event;

public interface UpdateEvent<T>  {

    T getOldValue();

    T getNewValue();

}
