package org.payid.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;
import org.immutables.value.Value.Default;
import org.payid.model.ImmutablePayIdAddress.Builder;

@Value.Immutable
@JsonSerialize(as = ImmutablePayIdAddress.class)
@JsonDeserialize(as = ImmutablePayIdAddress.class)
public interface PayIdAddress {

  static Builder builder() {
    return ImmutablePayIdAddress.builder();
  }

  @Default
  default String paymentNetwork() {
    return "XRPL";
  }

  @Default
  default String environment() {
    return "MAINNET";
  }

  @Default
  default String addressDetailsType() {
    return "CryptoAddressDetails";
  }

  PayIdAddressDetails addressDetails();
}
