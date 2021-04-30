package com.bolsadeideas.springboot.form.app.interceptors;

import java.util.Random;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

@Component("tiempoTranscurridoInterceptor")
public class TiempoTranscurridoInterceptor implements HandlerInterceptor {

	private static final Logger logger = LoggerFactory.getLogger(TiempoTranscurridoInterceptor.class);

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {

		if (request.getMethod().equalsIgnoreCase("post")) {
			return true; // OMITO EL INTERCEPTOR SI ES PARA /FORM EN POST
		}

		// instanceof es para ver si es un método del controlador
		if (handler instanceof HandlerMethod) {
			HandlerMethod metodo = (HandlerMethod) handler;
			logger.info("Es un método del controlador: " + metodo.getMethod().getName());
		}

		logger.info("TiempoTranscurridoInterceptor: preHandle() entrando ...");
		logger.info("Interceptando " + handler);
		long tiempoInicio = System.currentTimeMillis();
		request.setAttribute("tiempoInicio", tiempoInicio);

		// DEMORA AL AZAR SIMULADA
		Random random = new Random();
		Integer demora = random.nextInt(100);

		Thread.sleep(demora);

		// PODRIA INTERCEPTAR Y REDIRIGIR EN ALGUN ERROR CON ESTO
//		response.sendRedirect(request.getContextPath().concat("/login"));
//		return false;

		return true; // SI ES FALSE TIRA ERROR SINO CONTINUA NORMALMENTE

	}

	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
			ModelAndView modelAndView) throws Exception {

		// OMITO EL INTERCEPTOR SI ES PARA /FORM EN POST
		if (!request.getMethod().equalsIgnoreCase("post")) {

			long tiempoInicio = (Long) request.getAttribute("tiempoInicio");
			long tiempoFin = System.currentTimeMillis();

			long tiempoTranscurrido = tiempoFin - tiempoInicio;

			// ESTO INTERCEPTA TODO Y A VECES NO TIENE EL MODELANDVIEW POR LO QUE ES
			// IMPORANTE

			if (handler instanceof HandlerMethod && modelAndView != null) {
				modelAndView.addObject("tiempoTranscurrido", tiempoTranscurrido);
			}

			logger.info("Tiempo transcurrido: " + tiempoTranscurrido + " ms");
			logger.info("TiempoTranscurridoInterceptor: postHandle() saliendo ...");

		}

	}

}
