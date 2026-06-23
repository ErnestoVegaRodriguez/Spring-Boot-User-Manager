package com.ernesto.usermanagerapi.application.ports.drivens;

import com.ernesto.usermanagerapi.domain.entities.DeliveryAttempt;

public interface DeliveryReceptor {

    public void procesar(DeliveryAttempt attempt);

}
