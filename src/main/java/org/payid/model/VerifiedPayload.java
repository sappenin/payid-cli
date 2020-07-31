package org.payid.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value.Immutable;
import org.payid.model.ImmutableVerifiedPayload.Builder;

@Immutable
@JsonSerialize(as = ImmutableVerifiedPayload.class)
@JsonDeserialize(as = ImmutableVerifiedPayload.class)
public interface VerifiedPayload {

  static Builder builder() {
    return ImmutableVerifiedPayload.builder();
  }

  String payId();

  PayIdAddress payIdAddress();
}
