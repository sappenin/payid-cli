package org.payid.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value.Immutable;
import org.payid.model.ImmutableVerifiedSignature.Builder;

@Immutable
@JsonSerialize(as = ImmutableVerifiedSignature.class)
@JsonDeserialize(as = ImmutableVerifiedSignature.class)
public interface VerifiedSignature {

  static Builder builder() {
    return ImmutableVerifiedSignature.builder();
  }

  /**
   * Base64-encoded protected headers.
   */
  @JsonProperty("protected")
  String protectedHeaders();

  /**
   * Signature as Base64.
   */
  String signature();
}
