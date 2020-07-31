package org.payid.cli.shell.commands;

import com.google.common.collect.Maps;
import org.payid.model.PayIdAddress;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

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
  protected final Logger blankLineLogger = LoggerFactory.getLogger("blank.line.logger");

  public AbstractCommands() {
  }
}
