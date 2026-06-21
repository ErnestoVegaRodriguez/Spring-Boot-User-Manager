package com.ernesto.usermanagerapi.application.ports.drivens;

public interface HasherService {

    public String hash(String value);

    public boolean compare(String value, String hash);

}
