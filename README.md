# Overview
This folder contains a Command-line-interface that can be used to perform
 operations useful to user of [PayID](https://payid.org).
 
For example, this utility can generate static PayID payloads as well as 
 Verifiable PayID payloads.

## Running the CLI
To execute this CLI, perform the following steps:

1. Checkout the code and navigate to this directory.
1. Build the project: `$> mvn clean install`
1. Run the CLI: `$> java -jar /target/payid-cli-0.1.0-SNAPSHOT.jar`

## Create a Keystore
To create a Keystore that the CLI can use, issue the following command to create a new keystore with an AES-256 SecretKey inside:

```bash
> keytool -keystore ./crypto.p12 -storetype PKCS12 -genseckey -alias secret0 -keyalg aes -keysize 256
```

Validate the key by inspecting the contents of the keystore:

```
> keytool -keystore ./crypto.p12 -storetype PKCS12 -list
``` 

## Examples
### Base PayID
Here is an example of a PayID payload that can be emitted from this utility:

```
{
    "payId": "alice$402.money",
    "addresses": [{
        "paymentNetwork": "XRPL",
        "environment": "MAINNET",
        "addressDetailsType": "CryptoAddressDetails",
        "addressDetails": {
            "x-address": "X7zmKiqEhMznSXgj9cirEnD5sWo3iZRura9qWXQbNvR2g69",
            "address": "rw2ciyaNshpHe7bCHo4bRWq6pqqynnWKQg",
            "tag": "722714325",
            "test": false
        }
    }]
}
```

### Verifiable PayID
Here is an example of a PayID payload that can be emitted from this utility:

```
{
  "payId" : "alice$xpring.money",
  "addresses" : [ ],
  "verifiedAddresses" : [ {
    "signatures" : [ {
      "protected" : {
        "jwk" : {
          "kty" : "EC",
          "use" : "sig",
          "crv" : "secp256k1",
          "kid" : "8c36aeec-7689-43c9-8b95-92c39d04a8d0",
          "x" : "Dq4ZMi4DfituAlFH_SYKsWh9Z-Aq5GricISkQxAPUtw",
          "y" : "VjYxAwD6KOOwDTz1BVmiBPHGEmm6clO4sGqf1YW5yac",
          "alg" : "ES256K"
        },
        "name" : "identityKey",
        "alg" : "ES256K",
        "typ" : "JOSE+JSON",
        "crit" : [ "b64" ],
        "b64" : false
      },
      "signature" : "00SnJc4O8Mx9gaU4sUmRhVl3q2XqhW1NyPjo0KMKFz1DCYie2cD0b_CL8xbi9pqSFHK54d-uoOSOPpBesjxK-g"
    } ],
    "payload" : {
      "payId" : "alice$xpring.money",
      "payIdAddress" : {
        "paymentNetwork" : "XRPL",
        "environment" : "MAINNET",
        "addressDetailsType" : "CryptoAddressDetails",
        "addressDetails" : {
          "address" : "rw2ciyaNshpHe7bCHo4bRWq6pqqynnWKQg",
          "tag" : "722714325"
        }
      }
    }
  }, {
    "signatures" : [ {
      "protected" : {
        "jwk" : {
          "kty" : "EC",
          "use" : "sig",
          "crv" : "secp256k1",
          "kid" : "8c36aeec-7689-43c9-8b95-92c39d04a8d0",
          "x" : "Dq4ZMi4DfituAlFH_SYKsWh9Z-Aq5GricISkQxAPUtw",
          "y" : "VjYxAwD6KOOwDTz1BVmiBPHGEmm6clO4sGqf1YW5yac",
          "alg" : "ES256K"
        },
        "name" : "identityKey",
        "alg" : "ES256K",
        "typ" : "JOSE+JSON",
        "crit" : [ "b64" ],
        "b64" : false
      },
      "signature" : "u7rZb9UjqD_go_GG_bvdyx07wBrjgVMWBPdqCsXvBuLmDiqC1N2MycxBO3RUfoUh0FW8bn8buIrXXXXRSy-nQg"
    } ],
    "payload" : {
      "payId" : "alice$xpring.money",
      "payIdAddress" : {
        "paymentNetwork" : "XRPL",
        "environment" : "TESTNET",
        "addressDetailsType" : "CryptoAddressDetails",
        "addressDetails" : {
          "address" : "rw2ciyaNshpHe7bCHo4bRWq6pqqynnWKQg",
          "tag" : "722714325"
        }
      }
    }
  } ]
}
```


