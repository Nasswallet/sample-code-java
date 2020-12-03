package Payment.API.Sample.Code.Java.Payment.API.Sample.Code.Java;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Base64;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.springframework.boot.autoconfigure.SpringBootApplication;
 

@SpringBootApplication
public class PaymentApiSampleCodeJavaApplication {
	
    
	final static String url = "https://uatgw.nasswallet.com/payment/transaction/";           
    final static String paymentUrl = "https://uatcheckout.nasswallet.com/payment-gateway?";
	
	final static String userIdentifier = "";  //Merchant's User identifier 
    final static String userPassword = ""; // Merchant's User Password 
    final static String transactionPin = "";  //Merchant's MPIN
    final static String orderId = "123456";  //Order ID which will be provided by the Merchant
    final static String amount = "10";  //order amount 
    final static String languageCode = "en";  //default language to show the payment page 
	
	
	static String methodName = null;
    static String requestData = null;
    static String token = null;    
	static String transactionId = null;
	static String txnToken = null;
	
	public static void main(String[] args) throws IOException { 				
		Login();
		if (token != null){
		Transaction();		
		} else {
			System.out.println("Login failed, Please check!");
		}
	}
	
	public static void Login(){		
		Base64.Encoder encoder = Base64.getEncoder();
	    String auth =  "TUVSQ0hBTlRfQVBQOk1lcmNoYW50QEFkbWluIzEyMw==";
	    
		String methodName = "login";
		String requestData = "{\"data\":{\"grantType\":\"password\",\"username\":\"" + userIdentifier + "\",\"password\":\"" + userPassword + "\"}}";
		
		try {			
			String response = MyPOSTRequest(url, methodName, requestData, "Basic " + auth);
			
			Pattern pattern = Pattern.compile("(\"access_token\\\":\\\"[^\"]*)");
		    Matcher matcher = pattern.matcher(response);		   
		    if (matcher.find())
		    {
		    	token = matcher.group(1).replace("\"access_token\":\"", "");		    
			    System.out.println("myTokn: " + token);
		    } else{
		    	System.out.println("Nothing");
		    }		    		 
		} catch (IOException e) {		
			e.printStackTrace();
		}
	}
	
	public static void Transaction(){
		methodName = "initTransaction";
		requestData = "{\"data\":{\"userIdentifier\":\"" + userIdentifier + "\",\"transactionPin\":\"" + transactionPin + "\",\"orderId\":\"" + orderId + "\",\"amount\":\"" + amount + "\",\"languageCode\":\"" + languageCode + "\"}}";		
		try{
			String response = MyPOSTRequest(url, methodName, requestData, "Bearer " + token);
			
			Pattern pattern = Pattern.compile("(\"transactionId\\\":\\\"[^\"]*)");
		    Matcher matcher = pattern.matcher(response);		   
		    if (matcher.find())
		    {
		    	transactionId = matcher.group(1).replace("\"transactionId\":\"", "");		    
			    System.out.println("TxnID: " + transactionId);
		    } else{
		    	System.out.println("Nothing");
		    }	
		    
		    Pattern pattern2 = Pattern.compile("(\"token\\\":[^}]*)");
		    Matcher matcher2 = pattern2.matcher(response);		   
		    if (matcher2.find())
		    {
				
				txnToken = matcher2.group(1).replaceAll("[^0-9]", "");	
				

			    System.out.println("txnToken: " + txnToken);
		    } else{
		    	System.out.println("Nothing");
		    }	
		    
		    if (transactionId != null && txnToken != null){		    
		    	String requestData =  paymentUrl + "id=" + transactionId + "&token=" + txnToken + "&userIdentifier=" + userIdentifier;
		    	System.out.println("Browser: " + requestData);
		    	boolean flag = MakePayment(requestData);
		    	System.out.println("flag: " + flag);
		    } else {
		    	System.out.println("Transaction failed, Please check!");
		    }
		    
		} catch (IOException e){		
			e.printStackTrace();
		}
	}
	
	public static String MyPOSTRequest(String url, String methodName, String requestData, String auth) throws IOException { 		
	    URL obj = new URL(url + methodName);
	    System.out.println("URL: " + obj);
	    HttpURLConnection postConnection = (HttpURLConnection) obj.openConnection();
	    postConnection.setRequestMethod("POST");	    	   
	    postConnection.setRequestProperty("authorization", auth);
	    System.out.println("Header : " +  auth);
	    postConnection.setRequestProperty("Content-Type", "application/json");
	    postConnection.setDoOutput(true);
	    OutputStream os = postConnection.getOutputStream();
	    os.write(requestData.getBytes());
	    os.flush();
	    os.close();
	    int responseCode = postConnection.getResponseCode();
	    System.out.println("POST Response Code : " + responseCode);
	    System.out.println("POST Response Message : " + postConnection.getResponseMessage());
	    String responseData = null;
	    if (responseCode == HttpURLConnection.HTTP_OK) { //success 
	    	BufferedReader in = new BufferedReader(new InputStreamReader( postConnection.getInputStream()));
	    	String inputLine;
	    	StringBuffer response = new StringBuffer();
	    	while ((inputLine = in .readLine()) != null) { 
	    		response.append(inputLine); 
	    		} in .close(); 
	    		// print result 
	    		System.out.println(response.toString());
	    		responseData = response.toString();
	    } else { 
	    	System.out.println("POST NOT WORKED");
	    	responseData = "error";
	    }
	    return responseData;
	}
	
	public static boolean MakePayment(String requestData) throws IOException{
		Runtime rt = Runtime.getRuntime();	    
	    String os = System.getProperty("os.name").toLowerCase();
	    
	    if (os.indexOf("win") >= 0){
	    	rt.exec("rundll32 url.dll,FileProtocolHandler " + requestData);
	    	return true;
	    } else if (os.indexOf("mac") >= 0){
	    	rt.exec("open " + requestData);
	    	return true;
	    } else if (os.indexOf("nix") >=0 || os.indexOf("nux") >=0){	    		    	
	    	rt.exec("/usr/bin/firefox -new-window " + requestData);
	    	return true;
	    } else {
	    	return false;
	    }
	}	
	        
 
    
}


