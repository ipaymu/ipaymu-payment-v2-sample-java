package com.example.ipaymupaymentv2samplejava;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.security.MessageDigest;
import java.util.Date;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;

import com.alibaba.fastjson.JSONObject;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

@SpringBootApplication
public class Ipaymu {

	public static String VirtualAccount = "0000008159733989";
	public static String ApiKey = "SANDBOXE33B64DB-C804-4982-9074-77E46CD96179";
	public static String PayMentUrl = "https://sandbox.ipaymu.com/api/v2/payment";

	public static void main(String[] args) {
		doPayMent();
	}

	@SuppressWarnings("deprecation")
	public static void doPayMent() {
		OkHttpClient client = new OkHttpClient().newBuilder().build();
		JSONObject jsonBody = new JSONObject();

		String[] product = {"Baju"};
		int[] qty = {1};
		int[] price = {100000};

		jsonBody.put("account", VirtualAccount);
		jsonBody.put("product", product);
		jsonBody.put("qty", qty);
		jsonBody.put("price", price);
		jsonBody.put("returnUrl", "https://XXXXX/ipaymu/returncallback");
		jsonBody.put("notifyUrl", "https://XXXXX/ipaymu/notifycallback");
		jsonBody.put("cancelUrl", "https://XXXXX/ipaymu/cancelcallback");
		try {
			MediaType JSON = MediaType.parse("application/json; charset=utf-8");
			RequestBody body = RequestBody.create(JSON, jsonBody.toJSONString());
			Request request = new Request.Builder().url(PayMentUrl).post(body)
					.addHeader("Content-Type", "application/json")//
					.addHeader("signature", getSignature(jsonBody.toJSONString()))//
					.addHeader("va", VirtualAccount)//
					.addHeader("timestamp", String.valueOf(new Date().getTime()))//
					.build();
			Response response = client.newCall(request).execute();
			ResponseBody resBody = response.body();
			JSONObject resJson = JSONObject.parseObject(resBody.string());
			System.out.println(resJson);
			System.out.println(resBody.string());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static String getSignature(String jsonBody) {
		System.out.println("request json:" + jsonBody);
		String requestBody = getSHA256Hash(jsonBody).toLowerCase();
		System.out.println("request body for SHA256 :" + requestBody);
		String stringToSign = "POST:" + VirtualAccount + ":" + requestBody + ":" + ApiKey;
		System.out.println("stringToSign :" + stringToSign);
		String signature = sha256_HMAC(ApiKey, stringToSign);
		System.out.println("final signature :" + signature);
		return signature;
	}

	private static String getSHA256Hash(String data) {
		String result = null;
		try {
			MessageDigest digest = MessageDigest.getInstance("SHA-256");
			byte[] hash = digest.digest(data.getBytes("UTF-8"));
			return bytesToHex(hash); // make it printable
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return result;
	}

	private static String bytesToHex(byte[] hash) {
		return DatatypeConverter.printHexBinary(hash);
	}

	private static String byteArrayToHexString(byte[] b) {
		StringBuilder hs = new StringBuilder();
		String stmp;
		for (int n = 0; b != null && n < b.length; n++) {
			stmp = Integer.toHexString(b[n] & 0XFF);
			if (stmp.length() == 1)
				hs.append('0');
			hs.append(stmp);
		}
		return hs.toString().toLowerCase();
	}

	public static String sha256_HMAC(String secret, String message) {
		String hash = "";
		try {
			Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
			SecretKeySpec secret_key = new SecretKeySpec(secret.getBytes(), "HmacSHA256");
			sha256_HMAC.init(secret_key);
			byte[] bytes = sha256_HMAC.doFinal(message.getBytes());
			hash = byteArrayToHexString(bytes);
			System.out.println(hash);
		} catch (Exception e) {

		}
		return hash;
	}
}
