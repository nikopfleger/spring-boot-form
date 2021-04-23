package com.bolsadeideas.springboot.form.app.controllers;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;

import com.bolsadeideas.springboot.form.app.editors.NombreMayusculaEditor;
import com.bolsadeideas.springboot.form.app.models.domain.Usuario;
import com.bolsadeideas.springboot.form.app.validation.UsuarioValidador;

//SESSIONATTRIBUTES ES PARA NO PERDER LOS DATOS Y PERSISTIR EN LA SESION HTTP

@Controller
@SessionAttributes("usuario")
public class FormController {
	
	@Autowired
	private UsuarioValidador validador;
	
	//Setea el validador y puebla los datos del formulario
	@InitBinder
	public void initBinder(WebDataBinder binder) {
		binder.addValidators(validador); //agrego un nuevo validador al stack (annotations + el validador), si pongo setvalidator me lo reemplaza
		
		//OTRA FORMA SI NO USO LA ANOTACION
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		dateFormat.setLenient(false);
		binder.registerCustomEditor(Date.class, "fechaNacimiento", new CustomDateEditor(dateFormat, true));
		
		//SI QUIERO QUE SE APLIQUE A TODOS SACO fechaNacimiento
		//binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, false));
		
		binder.registerCustomEditor(String.class, "nombre", new NombreMayusculaEditor());
		binder.registerCustomEditor(String.class, "apellido", new NombreMayusculaEditor());
		//SI QUIERO TODOS LOS CAMPOS STRING LE SACO EL 2do parametro
	}
	
	@ModelAttribute("paises")
	public List<String> paises() {
		return Arrays.asList("España","México","Chile","Argentina","Perú","Colombia","Venezuela");				
	}

	@GetMapping("/form")
	public String form(Model model) {

		model.addAttribute("titulo", "Formulario Usuarios");
		
		Usuario usuario = new Usuario();
		usuario.setNombre("Nicolas");
		usuario.setApellido("Pfleger");
		usuario.setUsername("savemen");
		usuario.setEmail("nicolas.pfleger@gmail.com");
		usuario.setCuenta(1234);
		usuario.setIdentificador("123.456.789-K");
		
		model.addAttribute("usuario", usuario);
		return "form";
	}

	// BINDINGRESULT ES EL RESULTADO DE LA VALIDACION Y CONTIENE LOS MENSAJES DE
	// ERROR, ES PROPIO DE SPRING Y SE INYECTA DE FORMA AUTOMATICA SI ESTA EL VALID
	// EL BINDINGRESULT DEBE ESTAR INMEDIATAMENTE DESPUES DEL OBJETO QUE SE VALIDA

	@PostMapping("/form")
	public String procesar(@Valid Usuario usuario, BindingResult result, Model model, SessionStatus status) {

		//validador.validate(usuario, result); lo comento porque ahora lo automatizo con initBinder
		
		if (result.hasErrors()) {			
			return "form";
		}

		model.addAttribute("titulo", "Resultado form");
		model.addAttribute("usuario", usuario);
		
		//para terminar la sesion
		status.setComplete();
		
		return "resultado";
	}

}
