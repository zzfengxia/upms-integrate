package com.zz.jmeter.utils;

import com.rfcyber.rfcepayment.globalplatform.GPSecurityDomainFactory;
import com.rfcyber.rfcepayment.globalplatform.security.GPSecurityService;
import com.rfcyber.rfcepayment.sam.RFCSAMFactory;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

public class MasterKeyHelper {
	private static GPSecurityService secser;
	//private static String RFC_EPAY_SAM_CONF = "conf/security/RFCEPAYSAM.xml";

	// 用于本地eclipse启动用
	//private static String RFC_EPAY_SAM_CONF = "target/classes/conf/security/RFCEPAYSAM.xml";

	// 软加密
	private static String implClass = "com.rfcyber.rfcepayment.sam.impl.RFCSAMImpl";
	private static String RFC_EPAY_SAM_CONF = "D:/apache-tomcat-6.0.35/webapps/ROOT/WEB-INF/classes/conf/security/RFCEPAYMOCKSAM.xml";

	private static final Logger logger = LoggerFactory.getLogger(MasterKeyHelper.class);
		
	private static void init() {
		System.out.println("Init GPSecurityService ...");
		// TSM Default Configuration
		try {
            InputStream stream = MasterKeyHelper.class.getClassLoader().getResourceAsStream("RFCEPAYMOCKSAM.xml");
            if(stream == null) {
                stream = new FileInputStream("RFCEPAYMOCKSAM.xml");
            }

            File targetFile = new File("hsm.xml");
            FileUtils.copyInputStreamToFile(stream, targetFile);

			secser= GPSecurityDomainFactory.createGPSecurityService(RFCSAMFactory.createRFCSAM(implClass, targetFile.getAbsolutePath()));
		} catch (Exception e) {
			logger.error("Failed to create GP security service!", e);
		}
	}
	
	public static byte[][] getDerivedKeySet(String keyData, String masterKeyVal){
		byte[][] keyset = null;
		
		try{
			if(secser == null){
				init();
			}
			
			System.out.println("KeyData: " + keyData);
			System.out.println("masterKey: " + masterKeyVal);
			
			byte[] cardKeyData = ByteUtil.hexToByteArray(keyData);
			// Such as '404142434445464748494a4b4c4d4e4f'
			byte[] masterKey = ByteUtil.hexToByteArray(masterKeyVal);	
			
			keyset = new byte[3][];
			keyset[0] = secser.generateDerivedKey(masterKey, GPSecurityService.KEY_TYPE_KENC, cardKeyData);
			keyset[1] = secser.generateDerivedKey(masterKey, GPSecurityService.KEY_TYPE_KMAC, cardKeyData);
			keyset[2] = secser.generateDerivedKey(masterKey, GPSecurityService.KEY_TYPE_KDEK, cardKeyData);
			
			System.out.println(" ENC Key: " + ByteUtil.byteArrayToHex(keyset[0]));
			System.out.println(" MAC key: " + ByteUtil.byteArrayToHex(keyset[1]));
			System.out.println(" DEK key: " + ByteUtil.byteArrayToHex(keyset[2]));
			
			return keyset;
		}catch(Exception e){
			logger.error("Failed to generate derived KeySet!", e);
			return null; 
		}
	}
}
