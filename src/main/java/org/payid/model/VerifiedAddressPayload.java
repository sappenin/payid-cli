package org.payid.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value.Immutable;
import org.payid.model.ImmutableVerifiedAddressPayload.Builder;

@Immutable
@JsonSerialize(as = ImmutableVerifiedAddressPayload.class)
@JsonDeserialize(as = ImmutableVerifiedAddressPayload.class)
public interface VerifiedAddressPayload {

  static Builder builder() {
    return ImmutableVerifiedAddressPayload.builder();
  }

  String payId();

  PayIdAddress payIdAddress();
}
