/*
SPDX-License-Identifier: Apache-2.0
*/

package org.example;

import org.junit.Test;

public class ClientTest {
	public static String peerHostPort = "192.168.96.7:7054";
	public static String MSPId = "Org1MSP";
	public static String orgNumber = "org1.example.com";

	public static String adminName = "admin";
	public static String localUserName = "gibbon_1";

	public static String newUserName = "gibbon_3_07181548";


	@Test
	public void testFabCar() throws Exception {


		/* demo */
//		EnrollAdmin.main(null);
//		RegisterUser.main(null);
//		ClientApp.main(null);

//		FabricDemo.main(null);


		/* TCP */
//		if (false) {
//			if (blankCar.main(null) != 0) {
////				caCar.main(null);
//			}
//		} else {
//			EnrollAdmin.main(null);
//			RegisterUser.main(null);
//			caCar.main(null);
//		}


		/* UDP */
//		EnrollAdmin.main(null);
//		RegisterUser.main(null);
//		serverUDP.main(null);
		clientUDP.main(null);
	}
}
