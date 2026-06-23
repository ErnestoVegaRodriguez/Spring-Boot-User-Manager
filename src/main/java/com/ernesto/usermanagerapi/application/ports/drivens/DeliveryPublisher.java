package com.ernesto.usermanagerapi.application.ports.drivens;

import com.ernesto.usermanagerapi.domain.patterns.Delivery;

public interface DeliveryPublisher {

    public <TEvent> void publish(Delivery<TEvent> event);

}
