package com.yotereparo.security;

import java.io.IOException;
import java.util.Locale;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yotereparo.util.MiscUtils;
import com.yotereparo.util.error.CustomResponseError;

@Component
public class CustomUnauthorizedHandler implements AuthenticationEntryPoint {

	private static final Logger logger = LoggerFactory.getLogger(CustomUnauthorizedHandler.class);
	
	@Autowired
    private MessageSource messageSource;
	@Autowired
	private MiscUtils miscUtils;

	@Override
	public void commence(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException authException) throws IOException, ServletException {
		logger.error("[401] Unauthorized error: {}", authException.getMessage());
		ObjectMapper objectMapper = new ObjectMapper();
		CustomResponseError error = new CustomResponseError(
				"Authentication","error",messageSource.getMessage("client.error.unauthorized", null, Locale.getDefault()));
		String jsonObject = objectMapper.writeValueAsString(miscUtils.getFormatedResponseError(error)); 
		response.setContentType("application/json; charset=UTF-8");
		response.setStatus(401);
		response.getWriter().write(jsonObject);
	}

}
