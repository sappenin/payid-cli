package org.payid.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value.Default;
import org.immutables.value.Value.Immutable;
import org.payid.model.ImmutablePayIdPayload.Builder;

import java.util.Collection;
import java.util.Collections;

@Immutable
@JsonSerialize(as = ImmutablePayIdPayload.class)
@JsonDeserialize(as = ImmutablePayIdPayload.class)
public interface PayIdPayload {

  static Builder builder() {
    return ImmutablePayIdPayload.builder();
  }

  String payId();

  @Default
  default Collection<PayIdAddress> addresses() {
    return Collections.emptyList();
  }

  @Default
  default Collection<VerifiedAddress> verifiedAddresses() {
    return Collections.emptyList();
  }
}
