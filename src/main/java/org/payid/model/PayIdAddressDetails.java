package org.payid.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;
import org.payid.model.ImmutablePayIdAddressDetails.Builder;

import java.util.Optional;

@Value.Immutable
@JsonSerialize(as = ImmutablePayIdAddressDetails.class)
@JsonDeserialize(as = ImmutablePayIdAddressDetails.class)
public interface PayIdAddressDetails {

  static Builder builder() {
    return ImmutablePayIdAddressDetails.builder();
  }

  String address();

  @JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL) // or JsonSerialize.Inclusion.NON_EMPTY
  Optional<String> tag();

}
