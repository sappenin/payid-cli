package org.payid.cli.shell.commands;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.collect.Sets;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.crypto.ECDSAVerifier;
import com.nimbusds.jose.jwk.ECKey;
import com.nimbusds.jose.jwk.JWK;
import org.junit.Before;
import org.junit.Test;
import org.payid.cli.ObjectMapperFactory;
import org.payid.model.PayIdAddress;
import org.payid.model.PayIdAddressDetails;
import org.payid.model.PayIdPayload;
import org.payid.model.VerifiedAddress;
import org.payid.model.VerifiedAddressPayload;
import org.payid.model.VerifiedSignature;

import java.text.ParseException;
import java.util.Base64;

/**
 * Unit tests for {@link SignatureCommands}.
 */
public class SignatureCommandsTest {

  private ECKey identityKeyForTesting;
  private SignatureCommands signatureCommands;

  @Before
  public void setUp() throws ParseException {
    AbstractCommands.addresses.clear();

    this.signatureCommands = new SignatureCommands();
    signatureCommands.setPayIdStatic("alice$xpring.money");

    identityKeyForTesting = ECKey.parse("{"
      + "\"kty\":\"EC\","
      + "\"d\":\"yAX_KpWSgG7vPLTCU1qQMTxa0LOzictmNXtASeBgFM8\","
      + "\"use\":\"sig\","
      + "\"crv\":\"secp256k1\","
      + "\"kid\":\"ab1f3fdb-2f6e-4b90-92eb-07daa1747abe\","
      + "\"x\":\"F-odA2bI25U86op2MIh97ZCaOJgoyCiA7JILqbpxRAc\","
      + "\"y\":\"pG38M98Q3oaLzFWUQAs5Ua6-qNk6wZ3edShebmFaeHA\","
      + "\"alg\":\"ES256K\""
      + "}"
    );

    signatureCommands.setIdentityKeyStatic(identityKeyForTesting);
  }

  @Test
  public void signJwsWithNoAddresses() throws JsonProcessingException, JOSEException {
    PayIdPayload signedJws = ObjectMapperFactory.objectMapper()
      .readValue(signatureCommands.signJws(), PayIdPayload.class);

    assertThat(signedJws.payId()).isEqualTo("alice$xpring.money");
    assertThat(signedJws.addresses()).isEmpty();
    assertThat(signedJws.verifiedAddresses()).isEmpty();
  }

  @Test
  public void signJwsWithSingleAddress() throws JsonProcessingException, JOSEException, ParseException {

    signatureCommands.addAddressStatic("identityKey", PayIdAddress.builder()
      .addressDetails(PayIdAddressDetails.builder()
        .address("rw2ciyaNshpHe7bCHo4bRWq6pqqynnWKQg")
        .tag("722714325")
        .build())
      .build());

    PayIdPayload signedJws = ObjectMapperFactory.objectMapper()
      .readValue(signatureCommands.signJws(), PayIdPayload.class);

    assertThat(signedJws.payId()).isEqualTo("alice$xpring.money");
    assertThat(signedJws.addresses()).isEmpty();

    // First and only address
    VerifiedAddress signedAddress = signedJws.verifiedAddresses().stream().findFirst().get();

    VerifiedAddressPayload verifiedAddressPayload =
      ObjectMapperFactory.objectMapper().readValue(signedAddress.payload(), VerifiedAddressPayload.class);

    assertThat(verifiedAddressPayload.payId()).isEqualTo("alice$xpring.money");

    // Payload
    assertThat(verifiedAddressPayload.payIdAddress().paymentNetwork()).isEqualTo("XRPL");
    assertThat(verifiedAddressPayload.payIdAddress().environment()).isEqualTo("MAINNET");
    assertThat(verifiedAddressPayload.payIdAddress().addressDetailsType()).isEqualTo("CryptoAddressDetails");
    assertThat(verifiedAddressPayload.payIdAddress().addressDetails().address())
      .isEqualTo("rw2ciyaNshpHe7bCHo4bRWq6pqqynnWKQg");
    assertThat(verifiedAddressPayload.payIdAddress().addressDetails().tag().get()).isEqualTo("722714325");

    // Protected Headers
    byte[] jsonBytes = Base64.getUrlDecoder().decode(
      "eyJiNjQiOmZhbHNlLCJjcml0IjpbImI2NCJdLCJraWQiOiI3MzAzMmYwNC01MWY5LTRlODEtODc1NS1kNjZmYjQ2MDQ4NTYiLCJuYW1lIjoi"
        + "aWRlbnRpdHlLZXkiLCJ0eXAiOiJKT1NFK0pTT04iLCJhbGciOiJFUzI1NksiLCJqd2siOnsia3R5IjoiRUMiLCJ1c2UiOiJzaWciLCJjcnYi"
        + "OiJzZWNwMjU2azEiLCJraWQiOiI3MzAzMmYwNC01MWY5LTRlODEtODc1NS1kNjZmYjQ2MDQ4NTYiLCJ4IjoiSEt3by1jMVQwblE4WXNBMkFH"
        + "elVFOTR5VEZBMjFVNEpHMjRDd1d5Tm1YQSIsInkiOiJ6cDd3Sk1UTmVBRkwzTDJUNU9XbjRzZzZ0dUdERFZGamVHc0dTM3NGTHlrIiwiYWxn"
        + "IjoiRVMyNTZLIn19"
    );

    String json = new String(jsonBytes);
    JsonNode jsonNode = ObjectMapperFactory.objectMapper().readValue(json, JsonNode.class);

    assertThat(jsonNode.get("name").asText()).isEqualTo("identityKey");
    assertThat(jsonNode.get("typ").asText()).isEqualTo("JOSE+JSON");
    assertThat(jsonNode.get("alg").asText()).isEqualTo("ES256K");
    assertThat(jsonNode.get("b64").asBoolean()).isFalse();
    assertThat(jsonNode.get("crit").size()).isEqualTo(1);
    assertThat(jsonNode.get("crit").get(0).asText()).isEqualTo("b64");

    JWK jwk = JWK.parse(jsonNode.get("jwk").toString());
    assertThat(jwk.getAlgorithm().getName()).isEqualTo("ES256K");
    assertThat(jwk.getKeyType().getValue()).isEqualTo("EC");
    assertThat(jwk.getKeyUse().getValue()).isEqualTo("sig");
    assertThat(jwk.getKeyID()).isNotBlank();

    // Signatures
    assertThat(signedAddress.signatures().size()).isEqualTo(1);

    final VerifiedSignature signature = signedAddress.signatures().stream().findFirst().get();
    assertThat(signature.signature()).isNotBlank();

    // The recipient creates a verifier with the public EC key
    JWSVerifier verifier = new ECDSAVerifier(identityKeyForTesting.toECPublicKey(), Sets.newHashSet("b64"));

    // Verify the EC signature
    signatureCommands.getSignedPayIdPayload().verifiedAddresses().stream().forEach(verifiedAddress -> {
      try {
        verifiedAddress.signedJwsObject().get().verify(verifier);
      } catch (JOSEException e) {
        throw new RuntimeException(e.getMessage(), e);
      }
    });
  }
}
