package org.payid.cli.shell.commands;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Maps;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.jwk.Curve;
import com.nimbusds.jose.jwk.ECKey;
import com.nimbusds.jose.jwk.KeyUse;
import com.nimbusds.jose.jwk.gen.ECKeyGenerator;
import org.payid.model.PayIdAddress;
import org.payid.model.PayIdPayload;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.UUID;

/**
 * Commands for encryption.
 */
public abstract class AbstractCommands {

  protected static final String KEY_CONFIGURATION = "Signature Key Settings";

  protected static final String GCP_CONFIGURATION = "GCP Configuration";
  protected static final String JKS_CONFIGURATION = "JKS Configuration";

  protected static final String UNSET = "[unset]";

  static String payId;
  static Map<String, PayIdAddress> addresses = Maps.newHashMap();

  // The private-key to use as the Identity Key.
  private static ECKey identityKey;
  protected final Logger blankLineLogger = LoggerFactory.getLogger("blank.line.logger");

  // Only used for test validation (not used in normal CLI).
  @VisibleForTesting
  protected PayIdPayload payIdPayload;

  protected PayIdAddress addAddressStatic(String key, PayIdAddress address) {
    return addresses.put(key, address);
  }

  @VisibleForTesting
  protected void setPayIdStatic(final String payId) {
    AbstractCommands.payId = payId;
  }

  protected ECKey getIdentityKey() throws JOSEException {
    if (identityKey == null) {
      identityKey = new ECKeyGenerator(Curve.SECP256K1)
        .keyUse(KeyUse.SIGNATURE)
        .keyID(UUID.randomUUID().toString())
        .algorithm(JWSAlgorithm.ES256K)
        .generate();
    }

    return identityKey;
  }

  /**
   * Allows the test harness access to the Signed PayIdPayload in order to obtain things like the JWSObject for each
   * signed address. Ordinarily, commands just return Strings that are used for output, so this value is not generally
   * accessible from the test harness.
   */
  @VisibleForTesting
  protected PayIdPayload getSignedPayIdPayload() {
    return this.payIdPayload;
  }

  @VisibleForTesting
  protected void setIdentityKeyStatic(final ECKey identityKey) {
    AbstractCommands.identityKey = identityKey;
  }
}
