/*
SPDX-License-Identifier: Apache-2.0
*/

package org.example;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.hyperledger.fabric.gateway.Contract;
import org.hyperledger.fabric.gateway.Gateway;
import org.hyperledger.fabric.gateway.Network;
import org.hyperledger.fabric.gateway.Wallet;
import org.hyperledger.fabric.gateway.Wallets;

public class ClientApp {

	static {
		System.setProperty("org.hyperledger.fabric.sdk.service_discovery.as_localhost", "true");
	}

	private static Path walletPath = Paths.get("wallet");
	private static Wallet wallet = null;
	private static Path networkConfigPath = Paths.get("src", "main", "resources", "crypto-config", "peerOrganizations", "org1.example.com", "connection-org1.yaml");
	private static Gateway.Builder builder = Gateway.createBuilder();
	private static Gateway gateway = null;

	public static void main(String[] args) throws Exception {
//		// Load a file system based wallet for managing identities.
//		Path walletPath = Paths.get("wallet");
		wallet = Wallets.newFileSystemWallet(walletPath);
//		// load a CCP
//		Path networkConfigPath = Paths.get("src", "main", "resources", "crypto-config", "peerOrganizations", "org1.example.com", "connection-org1.yaml");
//
//		Gateway.Builder builder = Gateway.createBuilder();

		builder.identity(wallet, "user").networkConfig(networkConfigPath).discovery(true);

		// create a gateway connection
		config.getNowDate(new String(""));
		try {
			gateway = builder.connect();
			// get the network and contract
			Network network = gateway.getNetwork("mychannel");
			Contract contract = network.getContract("fabcar");

			byte[] result;
//			config.getNowDate(new String(result));

			if (false) {
				result = contract.createTransaction("Vote").submit("carID1");
				config.getNowDate(new String(result));
			} else {
				result = contract.evaluateTransaction("queryAllCars");
				config.getNowDate(new String(result));

				contract.submitTransaction("createCar", "CAR10", "VW", "Polo", "Grey", "Mary");

				result = contract.evaluateTransaction("queryCar", "CAR10");
				config.getNowDate(new String(result));

				contract.submitTransaction("changeCarOwner", "CAR10", "Archie");

				result = contract.evaluateTransaction("queryCar", "CAR10");
				config.getNowDate(new String(result));
			}
			gateway.close();  // 耗时9s
		} catch (Exception e) {
            config.getNowDate(new String());
            e.printStackTrace();
        }
		config.getNowDate(new String(""));
	}
}