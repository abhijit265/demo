package com.ecs.asaksa.client.api.tests;

import in.gov.uidai.auth.aua.helper.DigitalSigner;
import in.gov.uidai.auth.aua.helper.SignatureVerifier;
import in.gov.uidai.auth.device.model.AuthDataFromDeviceToAUA;
import in.gov.uidai.authentication.otp._1.Tkn;
import in.gov.uidai.authentication.uid_auth_request_data._1.MatchingStrategy;
import in.gov.uidai.authentication.uid_auth_response._1.AuthRes;
import in.gov.uidai.authentication.uid_auth_response._1.AuthResult;

import java.net.Proxy.Type;
import java.util.Date;

import com.ecs.asaksa.gateway.AsaAuthResponse;

public class AuthTest implements AuthProcessor {

	public static void main(String[] args) throws Exception {

		// TODO Auto-generated method stub new AuthTest().testBiometricAuth();
		new AuthTest().testDemographicAuth();
	}

	public void testDemographicAuth() throws Exception {

		AuthProcessor pro=new AuthProcessor() {
			
			@Override
			public void setProxyConfig(Type proxyType, String proxyIp, int proxyPort) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public String sendToAsa(String url, String data) {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public AsaAuthResponse processAsaResponse(String xmlData,
					SignatureVerifier gatewayVerifier) {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public AuthDataFromDeviceToAUA prepareAuaData(
					DeviceCollectedAuthData authData, boolean useProto,
					String uidaiPublicKeyFile, boolean useSSK, String terminalId,
					String fdc, String idc, String lot, String lov, String pip,
					String udc) {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public String prepareAsaData(String clientId, String transactionId,
					String auaCode, String subAuaCode, String licenseKey,
					boolean usesBio, String usesBt, boolean usesOtp, boolean usesPa,
					boolean usesPfa, boolean usesPi, boolean usesPin, Tkn token,
					AuthDataFromDeviceToAUA auaData, DigitalSigner auasigner,
					DigitalSigner gatewaySigner) {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public AuthRes getAuthRes(AsaAuthResponse asaRes, Object object) {
				// TODO Auto-generated method stub
				return null;
			}
		}; 
		/*** Prepare Auth Data Object ***/
		DeviceCollectedAuthData authData = new DeviceCollectedAuthData();

		authData.setUid("999935530463");
		authData.setName("Prabu");

		authData.setNameMatchStrategy(MatchingStrategy.P);
		authData.setNameMatchValue(50);
		/*
		 * Prepare PID Block - The following code is recemonded to be executed
		 * in the system capturing
		 * 
		 * Biometric data
		 */

		AuthDataFromDeviceToAUA deviceData = pro.prepareAuaData(authData,
				false, "D:\\UIDAI\\ECSAsaKsaInterfaceAPITester\\ECSAsaKsaInterfaceAPITester\\uidai_auth_stage.cer", false, "public", "NA", "NC",
				"P", "636103", "127.0.0.1", "USIM000 0001");

		/* Prepare ASA Data */

		/*
		 * Create Instance of DigitalSigner for signing AUA Auth Packet. If the
		 * DigitalSigning of Auth Packet is deligated to ASA then the
		 * DigitalSigner shall be null
		 */

		DigitalSigner auasigner = null;

		/*
		 * Create Instance of DigitalSigner for signing the Overall ASA packet
		 * using the AUA Private Key (Generated using ECS Key Generation
		 * Utility)
		 */

		DigitalSigner gatewaySigner = new DigitalSigner("D:\\UIDAI\\ECSAsaKsaInterfaceAPITester\\ECSAsaKsaInterfaceAPITester\\DEM-AUA.pfx",
				"Demo@aua123".toCharArray(), "ecs_staging_aua_client_key");

		Tkn tkn = null;
		String transactionId = "DEM-AUA-" + new Date().getTime(); // Should be
																	// Unique

		String asaXML = pro.prepareAsaData("DEM-AUA", transactionId, "public",
				"public",
				"MKlog1vDNtblvEI8VYd2b2zK6ltQcGVVquhFD2ssBWtf5mLFrpqXQSo",
				false, null, false, false, false, false, false, tkn,
				deviceData, auasigner, gatewaySigner);
		System.out.println("REQUEST TO ASA: " + asaXML);

		/* Send the Auth Request XML to ECS ASA/KSA */String asaResponseXML =

		pro.sendToAsa(
				"http://122.166.213.4:9090/ECSAsaKsaClientGatewayV2/AsaKsaGatewayClientInterfaceV2",
				asaXML);
		System.out.println("RESPONSE FROM ASA: " + asaResponseXML);

		if (asaResponseXML.startsWith("<Error>"))

		{

/*			System.out.println("Error occured - "
					+ pro.getErrorMessage(asaResponseXML));*/
			return;
		}

		/*
		 * Prepare SignatureVerifier object for validating the DigitalSignature
		 * of XML received from ASA using ASA Gateway Public Key
		 */
		SignatureVerifier gatewayVerifier = new SignatureVerifier(
				"D:\\UIDAI\\ECSAsaKsaInterfaceAPITester\\ECSAsaKsaInterfaceAPITester\\ECS_ASA_GW_STG.cer");

		/* Validate XML and Deserialize Ksa response XML */
		AsaAuthResponse asaRes = pro.processAsaResponse(asaResponseXML,
				gatewayVerifier);

		if (asaRes.isError() == false)
		{

			AuthRes authres = pro.getAuthRes(asaRes, null);
			if (authres.getRet() == AuthResult.Y)
				System.out.println("Authentication Successful!");

			else

			{
				System.out.println("Authentication Failed! - Error Code: "
						+ authres.getErr());

			}
		}

		else {

			System.out.println("Error Code: " + asaRes.getStatusCode());
			System.out.println("Error Description: "
					+ asaRes.getStatusDescription());

		}

	}

	public String prepareDemoAuthDOB(int dd, int mm, int yy) {
		String day = null;
		// Assemble DOB String day = null;
		if (dd != 0)

			day = "" + dd;

		String month = null;
		if (mm != 0)

			month = "" + mm;

		String year = null;
		if (yy != 0)

			year = "" + year;

		String dob = null;

		if ((year != null) && (year.length() > 0) && (month != null)
				&& (month.length() > 0) && (day != null) && (day.length() > 0)) {

			dob = year + "-" + month + "-" + day;

		} else if ((year != null) && (year.length() > 0) && (month != null)
				&& (month.length() > 0)) {
			dob = year + "-" + month + "-" + "";
		} else if ((year != null) && (year.length() > 0) && (day != null)
				&& (day.length() > 0)) {

			dob = year + "-" + "" + "-" + day;

		} else if ((month != null) && (month.length() > 0) && (day != null)
				&& (day.length() > 0)) {
			dob = "" + "-" + month + "-" + day;

		} else if ((month != null) && (month.length() > 0)) {
			dob = "" + "-" + month + "-" + "";

		} else if ((day != null) && (day.length() > 0)) {
			dob = "" + "-" + "" + "-" + day;

		} else if ((year != null) && (year.length() > 0)) {
			dob = year;

		}

		return dob;

	}

	@Override
	public void setProxyConfig(Type proxyType, String proxyIp, int proxyPort) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public AuthDataFromDeviceToAUA prepareAuaData(
			DeviceCollectedAuthData authData, boolean useProto,
			String uidaiPublicKeyFile, boolean useSSK, String terminalId,
			String fdc, String idc, String lot, String lov, String pip,
			String udc) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String prepareAsaData(String clientId, String transactionId,
			String auaCode, String subAuaCode, String licenseKey,
			boolean usesBio, String usesBt, boolean usesOtp, boolean usesPa,
			boolean usesPfa, boolean usesPi, boolean usesPin, Tkn token,
			AuthDataFromDeviceToAUA auaData, DigitalSigner auasigner,
			DigitalSigner gatewaySigner) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String sendToAsa(String url, String data) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public AsaAuthResponse processAsaResponse(String xmlData,
			SignatureVerifier gatewayVerifier) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public AuthRes getAuthRes(AsaAuthResponse asaRes, Object object) {
		// TODO Auto-generated method stub
		return null;
	}
}