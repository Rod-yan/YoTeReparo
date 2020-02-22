package com.yotereparo.configuration;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer;

import com.yotereparo.util.ASCIIArtGenerator;
import com.yotereparo.util.ASCIIArtGenerator.ASCIIArtFont;

@Component
public class AppInitializer extends AbstractAnnotationConfigDispatcherServletInitializer {
	
	@Autowired
    private ASCIIArtGenerator artGen;
	
    @Override
    protected Class<?>[] getRootConfigClasses() {
        return new Class[] { AppConfig.class };
    }
  
    @Override
    protected Class<?>[] getServletConfigClasses() {
        return null;
    }
  
    @Override
    protected String[] getServletMappings() {
        return new String[] { "/" };
    }
    
    @PostConstruct
    public void init() throws Exception {
    	String title = getClass().getPackage().getImplementationTitle();
    	String version = getClass().getPackage().getImplementationVersion();
		System.out.println("===========================================================");
		artGen.printTextArt(title, 10, ASCIIArtFont.ART_FONT_DIALOG, "x");
		System.out.println("============================================= Version "+version);
	}
 
}