package ar.edu.utn.sigmaproject.web;

import java.util.EnumSet;

import javax.servlet.DispatcherType;
import javax.servlet.FilterRegistration;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;

import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;

public class WebAppInitializer implements WebApplicationInitializer {

	@Override
	public void onStartup(ServletContext servletContext) throws ServletException {
		final AnnotationConfigWebApplicationContext springContext = new AnnotationConfigWebApplicationContext();
		springContext.register(ApplicationConfig.class);
		servletContext.addListener(org.zkoss.zk.ui.http.HttpSessionListener.class);
		servletContext.addListener(new org.springframework.web.context.ContextLoaderListener(springContext));
		servletContext.addListener(org.springframework.web.context.request.RequestContextListener.class);
		ServletRegistration.Dynamic zkLoader = servletContext.addServlet("zkLoader", org.zkoss.zk.ui.http.DHtmlLayoutServlet.class);
		zkLoader.setInitParameter("update-uri", "/zkau");
		zkLoader.setLoadOnStartup(1);
		zkLoader.addMapping("*.zul");
		ServletRegistration.Dynamic auEngine = servletContext.addServlet("auEngine", org.zkoss.zk.au.http.DHtmlUpdateServlet.class);
		auEngine.addMapping("/zkau/*");
		FilterRegistration.Dynamic emFilter = servletContext.addFilter("OpenEntityManagerInViewFilter", org.springframework.orm.jpa.support.OpenEntityManagerInViewFilter.class);
		emFilter.addMappingForUrlPatterns(EnumSet.of(DispatcherType.REQUEST), true, "/*");
	}

}
