/*
SPDX-License-Identifier: Apache-2.0
*/

package org.example;

import org.junit.Test;

public class ClientTest {
	public class GlobalClass {
		public static String caIPlist = "gibbon";
		public static int caPort = 8888;
		public static String myUserName = "gibbon";
	}

	@Test
	public void testFabCar() throws Exception {
//		EnrollAdmin.main(null);
//		RegisterUser.main(null);
//		ClientApp.main(null);

//		FabricDemo.main(null);

		if (false) {
			if (blankCar.main(null) != 0) {
//				caCar.main(null);
			}
		} else {
			EnrollAdmin.main(null);
			RegisterUser.main(null);
			caCar.main(null);
		}
	}
}
