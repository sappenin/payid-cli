package org.payid.cli.shell.commands;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.payid.cli.ObjectMapperFactory;
import org.payid.model.PayIdAddress;
import org.payid.model.PayIdAddressDetails;
import org.payid.model.PayIdPayload;
import org.springframework.shell.Availability;
import org.springframework.shell.standard.ShellCommandGroup;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellMethodAvailability;
import org.springframework.shell.standard.ShellOption;
import org.springframework.util.StringUtils;

import java.util.Optional;

/**
 * Commands for encryption.
 */
@ShellComponent
@ShellCommandGroup("PayID Utils")
public class PayIdCommands extends AbstractCommands {

  private static final String TESTNET = "TESTNET";
  private static final String MAINNET = "MAINNET";
  private static final String CRYPTO_ADDRESS_DETAILS = "CryptoAddressDetails";
  private static final String FIAT_ADDRESS_DETAILS = "FiatAddressDetails";

  private String networkEnvironment = TESTNET;
  private String addressDetailsType = CRYPTO_ADDRESS_DETAILS;

  /**
   * Encrypt a plaintext value using the selected key information.
   */
  @ShellMethod(value = "Show the current in-memory settings.", key = {"current-settings"})
  void showCurrentState() {
    blankLineLogger.info("Current PayID: {}", this.payId);
    blankLineLogger.info("Current AddressDetails Type: {}", this.addressDetailsType);
    blankLineLogger.info("Current Network Environment: {}", this.networkEnvironment);
  }

  /**
   * Clear all addresses from memory.
   */
  @ShellMethod(value = "Set the PayID to be operated upon.", key = {"set-payid"})
  String setPayId(String payId) {
    if (!payId.contains("$")) {
      throw new RuntimeException("PayID must contain a '$'");
    }
    this.payId = payId;
    return String.format("PayID is now: %s", payId);
  }

  /**
   * Encrypt a plaintext value using the selected key information.
   */
  @ShellMethod(value = "Set the network environent (`TESTNET` or `MAINNET`)", key = {"set-environment"})
  String setNetworkEnvironment(
    @ShellOption(
      help = "Specify the Layer1 Payment Network (e.g., `XRPL`)."
    ) String networkEnvironment
  ) {
    if (MAINNET.equals(networkEnvironment) || TESTNET.equals(networkEnvironment)) {
      this.networkEnvironment = networkEnvironment;
    } else {
      throw new RuntimeException("Network Environment must be either `TESTNET` or `MAINNET`.");
    }
    return String.format("Network Environment is now: %s", networkEnvironment);
  }

  /**
   * Encrypt a plaintext value using the selected key information.
   */
  @ShellMethod(
    value = "Set the address details type (`CryptoAddressDetails` or `FiatAddressDetails`)",
    key = {"set-address-details-type"})
  String setAddressDetailsType(
    @ShellOption(
      help = "Specify the PayID AddressDetailsType to use for new addresess."
    ) String addressDetailsType
  ) {
    if (CRYPTO_ADDRESS_DETAILS.equals(addressDetailsType) || FIAT_ADDRESS_DETAILS.equals(addressDetailsType)) {
      this.addressDetailsType = addressDetailsType;
    } else {
      throw new RuntimeException(
        String.format("AddressDetails Type must be either `%s` or `%s`.", CRYPTO_ADDRESS_DETAILS, FIAT_ADDRESS_DETAILS)
      );
    }
    return String.format("AddressDetails Type is now: %s", addressDetailsType);
  }


  /**
   * Add an address to the currently stored collection.
   */
  @ShellMethod(value = "Add an address into memory.", key = {"payid-address-add"})
  void addAddress(

    @ShellOption(
      help = "Specify the Layer Payment Network (e.g., `XRPL`).",
      value = "layer1-paymentNetwork"
    ) String paymentNetwork,

    @ShellOption(
      help = "Specify a Layer1 address (e.g., an XRPL address).",
      value = "layer1-address"
    ) String addressString,

    @ShellOption(
      help = "Specify an optional tag (optional; e.g., an XRPL destination tag).",
      defaultValue = "",
      value = "layer1-address-tag"
    ) String tagString

  ) throws JsonProcessingException {
    final PayIdAddress address = PayIdAddress.builder()
      .paymentNetwork(paymentNetwork.toUpperCase())
      .environment(networkEnvironment)
      .addressDetailsType(addressDetailsType)
      .addressDetails(
        PayIdAddressDetails.builder()
          .address(addressString)
          .tag(Optional.ofNullable(tagString))
          .build()
      )
      .build();

    final String addressKey = paymentNetwork + "-" + addressDetailsType + "-" + networkEnvironment;
    this.addresses.put(addressKey, address);
    blankLineLogger
      .info("Added PayID Address Mapping:\n\n{}", ObjectMapperFactory.objectMapperForDisplay().writeValueAsString(address));
  }

  /**
   * Remove an address from the currently stored collection.
   *
   * @return
   */
  @ShellMethod(value = "Remove an address from memory.", key = {"payid-address-remove"})
  String removeAddress(
    @ShellOption(
      help = "Specify the Layer Payment Network (e.g., `XRPL`)."
    ) String paymentNetwork,

    @ShellOption(
      help = "Specify the type of address (e.g., `CryptoAddressDetails`).",
      defaultValue = "CryptoAddressDetails"
    ) String addressDetailsType

  ) throws JsonProcessingException {
    final String addressKey = paymentNetwork + "-" + addressDetailsType + "-" + networkEnvironment;
    this.addresses.remove(addressKey);
    return String.format("PayID Address mapping for network('%s') and type('%s') removed.");
  }

  /**
   * Clear all addresses from memory.
   */
  @ShellMethod(value = "Clear all address mappings from memory.", key = {"payid-addresses-clear"})
  String removeAllAddress() {
    this.addresses.clear();
    return "All PayID Address mappings cleared.";
  }

  /**
   * Encrypt a plaintext value using the selected key information.
   */
  @ShellMethod(value = "Generate a full PayID Payload", key = {"payid-gen"})
  String generatePayIdPayload() throws JsonProcessingException {

    PayIdPayload payIdPayload = PayIdPayload.builder()
      .payId(payId)
      .addresses(this.addresses.values())
      .build();

    return ObjectMapperFactory.objectMapperForDisplay().writeValueAsString(payIdPayload);
  }

  /**
   * Centralizes all logic for enabling/disabling shell commands.
   */
  @ShellMethodAvailability({"payid-address-remove", "payid-address-add", "payid-addresses-clear"})
  Availability availabilityCheck() {
    if (!validPayIDSet()) {
      return Availability.unavailable("PayID must be set before using the CLI -- try the `set-payid` command first.");
    }
    return Availability.available();
  }

  /**
   * Centralizes all logic for enabling/disabling shell commands.
   */
  @ShellMethodAvailability({"payid-gen"})
  Availability availabilityCheckForPayIDGen() {
    if (!validPayIDSet()) {
      return Availability.unavailable("the PayID must be set before using the CLI -- try the `set-payid` command first"
        + ".");
    }
    if (addresses.size() <= 0) {
      return Availability.unavailable("one or more addresses must be added before generating a PayID payload -- try "
        + "the `payid-addresses-add` command first.");
    }
    return Availability.available();
  }

  boolean validPayIDSet() {
    return !StringUtils.isEmpty(this.payId) && this.payId.contains("$");
  }
}
