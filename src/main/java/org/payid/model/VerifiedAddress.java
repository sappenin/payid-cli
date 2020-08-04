package org.payid.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.google.common.annotations.VisibleForTesting;
import com.nimbusds.jose.JWSObject;
import org.immutables.value.Value.Immutable;
import org.payid.model.ImmutableVerifiedAddress.Builder;

import java.util.Collection;
import java.util.Optional;

@Immutable
@JsonSerialize(as = ImmutableVerifiedAddress.class)
@JsonDeserialize(as = ImmutableVerifiedAddress.class)
public interface VerifiedAddress {

  static Builder builder() {
    return ImmutableVerifiedAddress.builder();
  }

  Collection<VerifiedSignature> signatures();

  String payload();

  // For testing purposes.
  @JsonIgnore
  @VisibleForTesting
  Optional<JWSObject> signedJwsObject();
}
