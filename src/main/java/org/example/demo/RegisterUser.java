/*
SPDX-License-Identifier: Apache-2.0
*/

package org.example;




import java.nio.file.Paths;
import java.security.PrivateKey;
import java.util.Properties;
import java.util.Set;

import org.hyperledger.fabric.gateway.Wallet;
import org.hyperledger.fabric.gateway.Wallets;
import org.hyperledger.fabric.gateway.Identities;
import org.hyperledger.fabric.gateway.Identity;
import org.hyperledger.fabric.gateway.X509Identity;
import org.hyperledger.fabric.sdk.Enrollment;
import org.hyperledger.fabric.sdk.User;
import org.hyperledger.fabric.sdk.security.CryptoSuite;
import org.hyperledger.fabric.sdk.security.CryptoSuiteFactory;
import org.hyperledger.fabric_ca.sdk.HFCAClient;
import org.hyperledger.fabric_ca.sdk.RegistrationRequest;
import org.hyperledger.fabric_ca.sdk.Attribute;
import org.hyperledger.fabric_ca.sdk.EnrollmentRequest;

public class RegisterUser {

//	static {
//		System.setProperty("org.hyperledger.fabric.sdk.service_discovery.as_localhost", "true");
//	}

	public static void main(String[] args) throws Exception  {
		// Create a CA client for interacting with the CA.
		Properties props = new Properties();
		props.put("pemFile", "src/main/resources/crypto-config/peerOrganizations/" + config.pemDir);
		props.put("allowAllHostNames", "true");
		HFCAClient caClient = HFCAClient.createNewInstance("https://" + config.peerHostIp + ":" + config.peerHostPort, props);
		CryptoSuite cryptoSuite = CryptoSuiteFactory.getDefault().getCryptoSuite();
		caClient.setCryptoSuite(cryptoSuite);

		// Create a wallet for managing identities
		Wallet wallet = Wallets.newFileSystemWallet(Paths.get("wallet"));

		// Check to see if we've already enrolled the user.
		if (wallet.get(config.localUserName) != null) {
			System.out.println("An identity for the user " + config.localUserName + " already exists in the wallet");
			return;
		}

		X509Identity adminIdentity = (X509Identity) wallet.get(config.adminName);
		if (adminIdentity == null) {
			System.out.println(config.adminName + " needs to be enrolled and added to the wallet first");
			return;
		}
		User admin = new User() {

			@Override
			public String getName() {
				return config.adminName;
			}

			@Override
			public Set<String> getRoles() {
				return null;
			}

			@Override
			public String getAccount() {
				return null;
			}

			@Override
			public String getAffiliation() {
				return config.affiliation;
			}

			@Override
			public Enrollment getEnrollment() {
				return new Enrollment() {

					@Override
					public PrivateKey getKey() {
						return adminIdentity.getPrivateKey();
					}

					@Override
					public String getCert() {
						return Identities.toPemString(adminIdentity.getCertificate());
					}
				};
			}

			@Override
			public String getMspId() {
				return config.MSPId;
			}

		};

		// Register the user, enroll the user, and import the new identity into the wallet.
		RegistrationRequest registrationRequest = new RegistrationRequest(config.localUserName);
		registrationRequest.setAffiliation(config.affiliation);
		registrationRequest.setEnrollmentID(config.localUserName);

		// register的时候在registrationRequest中增加自定义属性
		registrationRequest.addAttribute(new Attribute("attr1", "value1"));	//user-defined attributes
		String enrollmentSecret = caClient.register(registrationRequest, admin);

		EnrollmentRequest enrollmentRequest = new EnrollmentRequest();
		enrollmentRequest.addAttrReq("hf.Affiliation");		//default attribute
		enrollmentRequest.addAttrReq("hf.EnrollmentID");	//default attribute
		enrollmentRequest.addAttrReq("hf.Type");			//default attribute
		enrollmentRequest.addAttrReq("attr1");				//user-defined attribute

		Enrollment enrollment = caClient.enroll(config.localUserName, enrollmentSecret, enrollmentRequest);
		Identity user = Identities.newX509Identity(config.MSPId, enrollment);
		wallet.put(config.localUserName, user);
		System.out.println("Successfully enrolled user " + config.localUserName + " and imported it into the wallet");
	}
}
