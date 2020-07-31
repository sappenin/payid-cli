package org.payid.cli.shell.commands;

import static org.payid.cli.ObjectMapperFactory.prettyJson;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.jwk.Curve;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.KeyUse;
import com.nimbusds.jose.jwk.gen.ECKeyGenerator;
import com.nimbusds.jose.jwk.gen.OctetKeyPairGenerator;
import org.springframework.shell.standard.ShellCommandGroup;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;

import java.util.UUID;

/**
 * Commands for encryption.
 */
@ShellComponent
@ShellCommandGroup("Key Utils")
public class JwkCommands extends AbstractCommands {

  ///////////////
  // Keyvalues generated at https://mkjwk.org/
  // RFC: https://tools.ietf.org/html/rfc7518#section-6.2.2.1
  ///////////////
  // The "d" (ECC private key) parameter contains the Elliptic Curve private key value, base64url encoded.
  private static final String D = "W906mG1q6NS4b6s-171On0CJPs9DrDgWkJ_-7X3r0hg";
  // The "x" (x coordinate) parameter contains the x coordinate for the Elliptic Curve point representing the public key
  private static final String X = "A_KVLBSFEJZ36UNE6ZD2XIBXOCAHCNH0490AO6UHQYI";
  // The "y" (y coordinate) parameter contains the x coordinate for the Elliptic Curve point representing the public key
  private static final String Y = "RXEW_IGY7I4K3O2_4QDR8HWARGMQIYRXIVSLW-52CFK";

  /**
   * Encrypt a plaintext value using the selected key information.
   */
  @ShellMethod(value = "Generate a new JWK.", key = {"jwk-gen"})
  void generateNewJwk(
    @ShellOption(
      defaultValue = "secp256k1",
      help = "Specify the type of JWK to generate (e.g., `secp256k1` or `ed25519`)."
    ) String keyType
  ) throws JOSEException, JsonProcessingException {
    final JWK jwk;
    if (keyType.equals("secp256k1")) {
      jwk = new ECKeyGenerator(Curve.SECP256K1)
        .keyUse(KeyUse.SIGNATURE)
        .keyID(UUID.randomUUID().toString())
        .algorithm(JWSAlgorithm.ES256K)
        .generate();
    } else if ("ed25519".equals(keyType)) {
      jwk = new OctetKeyPairGenerator(Curve.Ed25519)
        //.keyUse(KeyUse.SIGNATURE)
        .keyID(UUID.randomUUID().toString())
        //.algorithm(JWSAlgorithm.EdDSA)
        .generate();
    } else {
      throw new RuntimeException("Invalid JWK type.");
    }

    blankLineLogger.info("" +
        "===========\n" +
        "Private JWK:\n" +
        "===========\n" +
        "\n{}\n",
      prettyJson(jwk.toJSONString()));

    blankLineLogger.info("" +
        "===========\n" +
        "Public JWK:\n" +
        "===========\n" +
        "\n{}\n",
      prettyJson(jwk.toJSONString()));
  }
}
