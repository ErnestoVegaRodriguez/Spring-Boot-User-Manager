package com.ernesto.usermanagerapi.adapter.persistence.core.schemas;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class TelephoneEmbeddable {

    @Column(name = "phone_country_code")
    private String countryCode;

    @Column(name = "phone_number")
    private String number;

}
