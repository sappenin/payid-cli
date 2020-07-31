package org.payid.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value.Immutable;
import org.payid.model.ImmutableVerifiedAddress.Builder;

import java.util.Collection;

@Immutable
@JsonSerialize(as = ImmutableVerifiedAddress.class)
@JsonDeserialize(as = ImmutableVerifiedAddress.class)
public interface VerifiedAddress {

  static Builder builder() {
    return ImmutableVerifiedAddress.builder();
  }

  Collection<VerifiedSignature> signatures();

  VerifiedPayload payload();
}
