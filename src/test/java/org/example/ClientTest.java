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


		/* plan A -- TCP */
//		if (false) {
//			if (blankCar.main(null) != 0) {
////				caCar.main(null);
//			}
//		} else {
//			EnrollAdmin.main(null);
//			RegisterUser.main(null);
//			caCar.main(null);
//		}


		/* plan B -- UDP */
//		RSAUtils.genKeyPair();  // 生成公钥和私钥
//		if (false) {
//			config.getNowDate("公钥" + RSAUtils.keyMap.get(0));
//			config.getNowDate("私钥" + RSAUtils.keyMap.get(1));
//		}


		config.peerHostIp = "192.168.2.177";
		config.localUserName = "appUser1";
		config.newUserName = "appUser1";
//		System.out.println(config.localUserName);

		try {
			int ssss = Integer.parseInt(System.getProperty("t"));
			/*
			服务器端：mvn test -D t=1
			客户端： mvn test -D t=2 */
			if (ssss == 1) {
				EnrollAdmin.main(null);
				RegisterUser.main(null);
				ClientApp.main(null);
//				serverPlanB.main(null);
			} else if (ssss == 2) {
				clientPlanB.main(null);
				ClientApp.main(null);
			} else {
				EnrollAdmin.main(null);
				RegisterUser.main(null);
				ClientApp.main(null);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}
