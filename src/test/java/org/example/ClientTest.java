/*
SPDX-License-Identifier: Apache-2.0
*/

package org.example;

import org.junit.Test;

public class ClientTest {
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
		if (false) {
			EnrollAdmin.main(null);
			RegisterUser.main(null);
			serverUDP.main(null);
		} else {
			clientUDP.main(null);
		}
	}
}
