package com.imizsoft.backendsecurity.security.jwt;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletResponse;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.imizsoft.backendsecurity.payload.MessageResponse;

public class SecurityUtils {
	
	private static final ObjectMapper mapper = new ObjectMapper();
	
	public SecurityUtils(){
		
	}
	
	public static void sendError(HttpServletResponse response, Exception exception, int status, String message) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(status);
        PrintWriter writer = response.getWriter();
        writer.write(mapper.writeValueAsString(new MessageResponse(status, message)));
        writer.flush();
        writer.close();
    }

}
