package org.payid.model;

import static org.payid.cli.ObjectMapperFactory.prettyJson;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRawValue;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.nimbusds.jose.jwk.JWK;
import net.minidev.json.JSONStyle;
import org.immutables.value.Value.Default;
import org.immutables.value.Value.Immutable;
import org.payid.model.ImmutableVerifiedProtected.Builder;

@Immutable
@JsonSerialize(as = ImmutableVerifiedProtected.class)
@JsonDeserialize(as = ImmutableVerifiedProtected.class)
public interface VerifiedProtected {

  String IDENTITY_KEY = "identityKey";
  String SERVER_KEY = "serverKey";
  String PROOF_OF_CONTROL_KEY = "proofOfControlKey";

  static Builder builder() {
    return ImmutableVerifiedProtected.builder();
  }

  @JsonProperty("name")
  @Default
  default String name() {
    return IDENTITY_KEY;
  }

  @Default
  @JsonProperty("alg")
  default String algorithm() {
    return "ES256K";
  }

  @Default
  @JsonProperty("typ")
  default String type() {
    return "JOSE+JSON";
  }

  @Default
  @JsonProperty("crit")
  default String[] criticalClaims() {
    return new String[]{"b64"};
  }

  @Default
  @JsonProperty("b64")
  default boolean base64() {
    return false;
  }

  @JsonIgnore
  JWK jwk();

  @JsonProperty("jwk")
  @JsonRawValue // Let net.minidev.json do the work, and just pump that right into Jackson.
  default String jwkAsJson() {
    return prettyJson(jwk().toJSONObject().toJSONString(JSONStyle.NO_COMPRESS));
  }

}
