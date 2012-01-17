package controllers;

import play.*;
import play.libs.OpenID;
import play.libs.OpenID.UserInfo;
import play.mvc.*;

import java.util.*;

import models.*;

public class Application extends Controller {

	@Before(unless={"login", "authenticate"})
	static void checkAuthenticated() {
	    if(!session.contains("user")) {
	        authenticate();
	    }
	}
	 
	public static void index() {
		String email = session.get("email");
	    render(email);
	}
	     
	public static void login() {
	    render();
	}
	    
	public static void authenticate() {
	    if(OpenID.isAuthenticationResponse()) {
	    	//get the verified user
	        UserInfo verifiedUser = OpenID.getVerifiedID();
	        if(verifiedUser == null) {
	            flash.put("error", "Oops. Authentication has failed. Could be an issue with Google");
	            login();
	        } 
	        
	        //check that the e-mail is from your organization (gmail used as an example)
	        String email = verifiedUser.extensions.get("email");
	        String org = "gmail";
	        if(email != null && email.endsWith("@" + org + ".com")) {
	        	session.put("user", verifiedUser.id);
	        	session.put("email", email);
	        	index();	        	
	        } else {
	        	flash.put("error", "Only " + org + " accounts are allowed.");
	        	login();
	        }
	    } else {
	    	//only check against Google and require an e-mail
	    	OpenID.id("https://www.google.com/accounts/o8/id").required("email", "http://axschema.org/contact/email").verify();
	    }
	}

}