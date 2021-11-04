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

		builder.identity(wallet, config.newUserName).networkConfig(networkConfigPath).discovery(true);

		// create a gateway connection
		try {
			gateway = builder.connect();
			// get the network and contract
			Network network = gateway.getNetwork("mychannel");
			Contract contract = network.getContract("fabcar");

			byte[] result;

			result = contract.evaluateTransaction("queryAllCars");
			System.out.println("身份合法,查询结果如下: " + new String(result));

			result = contract.submitTransaction("createCar", "CAR10", "VW", "Polo", "Grey", "Mary");
			System.out.println("身份合法,创建新车成功");

			result = contract.evaluateTransaction("queryCar", "CAR10");
			System.out.println("身份合法,查询车主信息结果如下: " + new String(result));

			contract.submitTransaction("changeCarOwner", "CAR10", "Archie");
			System.out.println("身份合法,修改车主信息成功");

			result = contract.evaluateTransaction("queryCar", "CAR10");
			System.out.println("身份合法,修改成功" + new String(result));

//			gateway.close();  // 耗时9s
		} catch (Exception e) {
			System.out.println("身份不合法, 调用链码失败");
//            e.printStackTrace();
        }
	}
}