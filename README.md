# solanaj

Solana blockchain client, written in pure Java.
Solanaj is an API for integrating with Solana blockchain using the [Solana RPC API](https://docs.solana.com/apps/jsonrpc-api)

## Requirements
- Java 7+

## Dependencies
- bitcoinj
- OkHttp
- Moshi

### Example

##### Transfer lamports

```java
RpcClient client = new RpcClient(Cluster.TESTNET);

PublicKey fromPublicKey = new PublicKey("QqCCvshxtqMAL2CVALqiJB7uEeE5mjSPsseQdDzsRUo");
PublicKey toPublickKey = new PublicKey("GrDMoeqMLFjeXQ24H56S1RLgT4R76jsuWCd6SvXyGPQ5");
int lamports = 3000;

Account signer = new Account(secret_key);

Transaction transaction = new Transaction();
transaction.addInstruction(SystemProgram.transfer(fromPublicKey, toPublickKey, lamports));

String signature = client.getApi().sendTransaction(transaction, signer);
```

##### Get balance

```java
RpcClient client = new RpcClient(Cluster.TESTNET);

long balance = client.getApi().getBalance(new PublicKey("QqCCvshxtqMAL2CVALqiJB7uEeE5mjSPsseQdDzsRUo"));
```

## Contribution

Welcome to contribute, feel free to change and open a PR.


## License

Solanaj is available under the MIT license. See the LICENSE file for more info.
