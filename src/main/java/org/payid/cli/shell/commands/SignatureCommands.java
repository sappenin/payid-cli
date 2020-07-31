package org.payid.cli.shell.commands;

import static org.payid.cli.ObjectMapperFactory.objectMapper;
import static org.payid.cli.ObjectMapperFactory.prettyJson;
import static org.payid.model.VerifiedProtected.IDENTITY_KEY;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JOSEObjectType;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader.Builder;
import com.nimbusds.jose.JWSObject;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.Payload;
import com.nimbusds.jose.crypto.ECDSASigner;
import com.nimbusds.jose.crypto.ECDSAVerifier;
import com.nimbusds.jose.jwk.Curve;
import com.nimbusds.jose.jwk.ECKey;
import com.nimbusds.jose.jwk.KeyUse;
import com.nimbusds.jose.jwk.gen.ECKeyGenerator;
import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;
import net.minidev.json.parser.ParseException;
import org.payid.model.PayIdPayload;
import org.payid.model.VerifiedAddress;
import org.payid.model.VerifiedPayload;
import org.payid.model.VerifiedProtected;
import org.payid.model.VerifiedSignature;
import org.springframework.shell.Availability;
import org.springframework.shell.standard.ShellCommandGroup;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellMethodAvailability;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Commands for encryption.
 */
@ShellComponent
@ShellCommandGroup("Verifiable PayID")
public class SignatureCommands extends AbstractCommands {

  private Optional<String> identityKeyAlias;
  private Optional<String> serverKeyAlias;
  private Optional<String> proofOfControlAlias;

  public SignatureCommands() {
    this.identityKeyAlias = Optional.of(IDENTITY_KEY);
    this.serverKeyAlias = Optional.empty();
    this.proofOfControlAlias = Optional.empty();
  }

  /**
   * Encrypt a plaintext value using the selected key information.
   */
  @ShellMethod(value = "Sign a PayID that conforms to Verifiable PayID", key = {"s", "sign"})
  String signJws() throws JOSEException, JsonProcessingException {
    ECKey ecJWK = new ECKeyGenerator(Curve.SECP256K1)
      .keyUse(KeyUse.SIGNATURE)
      .keyID(UUID.randomUUID().toString())
      .algorithm(JWSAlgorithm.ES256K)
      .generate();

    // For each address in Addresses, we need to construct a VerifiedPayload

    List<VerifiedAddress> verifiedAddresses = this.addresses.values().stream()
      ///////////
      // Turn each address into a VerifiedPayload
      ///////////
      .map(address -> VerifiedPayload.builder()
        .payId(this.payId)
        .payIdAddress(address)
        .build()
      )
      ///////////
      // For each verifiedPayload, we need to construct a VerifiedAddress
      ///////////
      .map(verifiedPayload -> {

        try {

          ///////////////////
          // IdentityKey Sign
          ///////////////////

          // TODO: Validate with https://tools.ietf.org/html/rfc7797#section-8
          // The actual bytes should not be normalized. However, when serializing these two should be normalized.
          String payloadAsJsonString = objectMapper().writeValueAsString(verifiedPayload);
          //.replace(".", "\\.")
          //.replace("\"", "\\");

          JSONParser jsonParser = new JSONParser();
          JSONObject payloadJsonObject = (JSONObject) jsonParser.parse(payloadAsJsonString);

          // Create the EC signer
          JWSSigner signer = new ECDSASigner(ecJWK);

          JWSObject jwsObject = new JWSObject(
            new Builder(JWSAlgorithm.ES256K)
              .keyID(ecJWK.getKeyID())
              .customParam("name", IDENTITY_KEY)
              .type(JOSEObjectType.JOSE_JSON)
              .customParam("b64", false)
              .criticalParams(Sets.newHashSet("b64"))
              .jwk(ecJWK.toPublicJWK())
              .build(),
            new Payload(payloadJsonObject)
          );

          // Compute the EC signature
          jwsObject.sign(signer);

          // The recipient creates a verifier with the public EC key
          JWSVerifier verifier = new ECDSAVerifier(ecJWK.toECPublicKey());

          // Verify the EC signature
          assert jwsObject.verify(verifier);
          assert jwsObject.getPayload().toString().equals(payloadJsonObject.toJSONString());

          VerifiedSignature identityKeyVerifiedSignature = VerifiedSignature.builder()
            .signature(jwsObject.getSignature().toString())
            .verifiedProtected(VerifiedProtected.builder()
              .name(IDENTITY_KEY)
              .algorithm(jwsObject.getHeader().getAlgorithm().getName())
              .type(jwsObject.getHeader().getType().getType())
              .criticalClaims(jwsObject.getHeader().getCriticalParams().toArray(new String[0]))
              .base64(Boolean.getBoolean(jwsObject.getHeader().getCustomParam("b64").toString()))
              .jwk(jwsObject.getHeader().getJWK())
              .build()
            )
            .build();
          ///////////////////
          // ServerKey Sign
          ///////////////////

          // Coming Soon

          ///////////////////
          // POC Sign
          ///////////////////

          // Coming Soon

          return VerifiedAddress.builder()
            .payload(verifiedPayload)
            .signatures(Lists.newArrayList(
              identityKeyVerifiedSignature
              // TODO: Server and POC sigs here.
            ))
            .build();
        } catch (JOSEException | JsonProcessingException | ParseException e) {
          throw new RuntimeException(e.getMessage(), e);
        }

      })
      .collect(Collectors.toList());

    PayIdPayload payIdPayload = PayIdPayload.builder()
      .payId(this.payId)
      //.addresses()
      .verifiedAddresses(verifiedAddresses)
      .build();

    // Output the V.PayID JWS. Do 'prettyPrint' so that the JWK is indented properly.
    return prettyJson(objectMapper().writeValueAsString(payIdPayload));
  }

  /**
   * Centralizes all logic for enabling/disabling shell commands.
   */
  @ShellMethodAvailability({"s", "sign"})
  Availability availabilityCheck() {
    if (!identityKeyAlias.isPresent() && !serverKeyAlias.isPresent() && !proofOfControlAlias.isPresent()) {
      return Availability.unavailable(
        "Verifiable PayID signing requires at least one of an Identity, Server, or Proof-of-Control key alias"
      );
    }

    if (addresses.size() <= 0) {
      return Availability.unavailable("one or more addresses must be added before signing a PayID payload -- try "
        + "the `payid-addresses-add` command first.");
    }

    return Availability.available();
  }

  /**
   * Set the Secret0 Alias
   *
   * @param identityKeyAlias {@link String} representing the secret0 Key Alias
   *
   * @return A message displayable to the CLI user.
   */
  @ShellMethod(
    value = "Set the Identity Key Alias",
    key = {"set-identity-key-alias"}
  )
  public String setIdentityKeyAlias(final String identityKeyAlias) {
    // TODO: make this conform to the proper name (currently anything goes).
    this.identityKeyAlias = Optional.of(identityKeyAlias);
    return String.format("PayID payloads will be signed using the Identity Key: `%s`", identityKeyAlias);
  }

}
