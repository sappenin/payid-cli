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
  "payId": "pay$payid.fluid.money",
  "addresses": [],
  "verifiedAddresses": [
    {
      "payId": "pay$payid.fluid.money",
      "addresses": [
      ],
      "verifiedAddresses": [
        {
          "signatures": [
            {
              "protected": "eyJiNjQiOmZhbHNlLCJjcml0IjpbImI2NCJdLCJraWQiOiJlMTViNmM5Zi0yZWEwLTQ1ZTctYWYxNi01ODk3M2E1MDM4ZmEiLCJuYW1lIjoiaWRlbnRpdHlLZXkiLCJ0eXAiOiJKT1NFK0pTT04iLCJhbGciOiJFUzI1NksiLCJqd2siOnsia3R5IjoiRUMiLCJ1c2UiOiJzaWciLCJjcnYiOiJzZWNwMjU2azEiLCJraWQiOiJlMTViNmM5Zi0yZWEwLTQ1ZTctYWYxNi01ODk3M2E1MDM4ZmEiLCJ4IjoiXzE5dFJ2dDQ4UWltbFZaS25DUzE5TFJBeUNPWmNndVAxXzhaQkJKQWMwQSIsInkiOiJJNW9wemlQQ3cxblB3SnQxRXhDbEhfa1o2YUlrQlk0bUtfTnBEeEV5MHEwIiwiYWxnIjoiRVMyNTZLIn19",
              "signature": "p-Xg_8KJIgiqAUA99Jd_VM8PVwpbNhj3vCC5DX8Xr2gatl91pCndxZMKBdsOhL95OuHHUomcj69zl2B9_vdf6A"
            }
          ],
          "payload": "{\"payId\":\"pay$payid.fluid.money\",\"payIdAddress\":{\"paymentNetwork\":\"XRPL\",\"environment\":\"TESTNET\",\"addressDetailsType\":\"CryptoAddressDetails\",\"addressDetails\":{\"address\":\"rw2ciyaNshpHe7bCHo4bRWq6pqqynnWKQg\",\"tag\":\"722714325\"}}}"
        }
      ]
    },
    {
      "payId": "pay$payid.fluid.money",
      "addresses": [
      ],
      "verifiedAddresses": [
        {
          "signatures": [
            {
              "protected": "eyJiNjQiOmZhbHNlLCJjcml0IjpbImI2NCJdLCJraWQiOiJlMTViNmM5Zi0yZWEwLTQ1ZTctYWYxNi01ODk3M2E1MDM4ZmEiLCJuYW1lIjoiaWRlbnRpdHlLZXkiLCJ0eXAiOiJKT1NFK0pTT04iLCJhbGciOiJFUzI1NksiLCJqd2siOnsia3R5IjoiRUMiLCJ1c2UiOiJzaWciLCJjcnYiOiJzZWNwMjU2azEiLCJraWQiOiJlMTViNmM5Zi0yZWEwLTQ1ZTctYWYxNi01ODk3M2E1MDM4ZmEiLCJ4IjoiXzE5dFJ2dDQ4UWltbFZaS25DUzE5TFJBeUNPWmNndVAxXzhaQkJKQWMwQSIsInkiOiJJNW9wemlQQ3cxblB3SnQxRXhDbEhfa1o2YUlrQlk0bUtfTnBEeEV5MHEwIiwiYWxnIjoiRVMyNTZLIn19",
              "signature": "tcK8zdtIcDHVyz1GKj2SRWTD862KcDsjsNM1d9SFDPp4wuDoe0dBja80KOzpMijXcxdKjoA7aqiYQqbm1hMCIw"
            }
          ],
          "payload": "{\"payId\":\"pay$payid.fluid.money\",\"payIdAddress\":{\"paymentNetwork\":\"XRPL\",\"environment\":\"TESTNET\",\"addressDetailsType\":\"CryptoAddressDetails\",\"addressDetails\":{\"address\":\"rw2ciyaNshpHe7bCHo4bRWq6pqqynnWKQg\",\"tag\":\"722714325\"}}}"
        }
      ]
    }
  ]
}
```

# Docker

## Build a Docker Image
To create a docker image, execute the following commands:

`> docker build -t payid/payid-cli .`

## Run the Image

`> docker run -it -p 5432:5432 payid/payid-cli`

