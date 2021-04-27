package com.bolsadeideas.springboot.form.app.controllers;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;

import com.bolsadeideas.springboot.form.app.editors.NombreMayusculaEditor;
import com.bolsadeideas.springboot.form.app.editors.PaisPropertyEditor;
import com.bolsadeideas.springboot.form.app.editors.RolesEditor;
import com.bolsadeideas.springboot.form.app.models.domain.Pais;
import com.bolsadeideas.springboot.form.app.models.domain.Role;
import com.bolsadeideas.springboot.form.app.models.domain.Usuario;
import com.bolsadeideas.springboot.form.app.services.PaisService;
import com.bolsadeideas.springboot.form.app.services.RoleService;
import com.bolsadeideas.springboot.form.app.validation.UsuarioValidador;

//SESSIONATTRIBUTES ES PARA NO PERDER LOS DATOS Y PERSISTIR EN LA SESION HTTP

@Controller
@SessionAttributes("usuario")
public class FormController {

	@Autowired
	private UsuarioValidador validador;
	
	@Autowired
	private PaisService paisService;
	
	@Autowired
	private PaisPropertyEditor paisEditor;
	
	@Autowired
	private RoleService roleService;
	
	@Autowired
	private RolesEditor roleEditor;

	// Setea el validador y puebla los datos del formulario
	@InitBinder
	public void initBinder(WebDataBinder binder) {
		binder.addValidators(validador); // agrego un nuevo validador al stack (annotations + el validador), si pongo
											// setvalidator me lo reemplaza

		// OTRA FORMA SI NO USO LA ANOTACION
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		dateFormat.setLenient(false);
		binder.registerCustomEditor(Date.class, "fechaNacimiento", new CustomDateEditor(dateFormat, true));

		// SI QUIERO QUE SE APLIQUE A TODOS SACO fechaNacimiento
		// binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat,
		// false));

		binder.registerCustomEditor(String.class, "nombre", new NombreMayusculaEditor());
		binder.registerCustomEditor(String.class, "apellido", new NombreMayusculaEditor());
		binder.registerCustomEditor(Pais.class, "pais",  paisEditor);
		binder.registerCustomEditor(Role.class, "roles", roleEditor);
		// SI QUIERO TODOS LOS CAMPOS STRING LE SACO EL 2do parametro
	}
	
	@ModelAttribute("listaRolesString")
	public List<String> listaRolesString() {
		List<String> roles = new ArrayList<>();
		roles.add("ROLE_ADMIN"); //ASI SE GUARDAN LOS ROLES CON SPRING SECURITY
		roles.add("ROLE_USER");
		roles.add("ROLE_MODERATOR");
		return roles;
		
	}
	
	
	@ModelAttribute("listaRolesMap")
	public Map<String, String> listaRolesMap() {
		Map<String, String> roles = new HashMap<String, String>();
		roles.put("ROLE_ADMIN", "Administrador");
		roles.put("ROLE_USER", "Usuario");
		roles.put("ROLE_MODERATOR", "Moderador");
		return roles;

	}
	
	@ModelAttribute("listaRoles")
	public List<Role> listaRoles() {
		return this.roleService.listar();
	}

	@ModelAttribute("listaPaises")
	public List<Pais> listaPaises() {
		return paisService.listar();
	}

	@ModelAttribute("paises")
	public List<String> paises() {
		return Arrays.asList("España", "México", "Chile", "Argentina", "Perú", "Colombia", "Venezuela");
	}

	@ModelAttribute("paisesMap")
	public Map<String, String> paisesMap() {
		Map<String, String> paises = new HashMap<String, String>();
		paises.put("ES", "España");
		paises.put("ME", "México");
		paises.put("CL", "Chile");
		paises.put("AR", "Argentina");
		paises.put("PE", "Perú");
		paises.put("CO", "Colombia");
		paises.put("VE", "Venezuela");
		return paises;

	}
	
	@ModelAttribute("genero")
	public List<String> genero() {
		return Arrays.asList("Hombre","Mujer");
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
		usuario.setHabilitar(true);
		usuario.setValorSecreto("Algún valor");
		usuario.setPais(new Pais(4, "AR", "Argentina"));
		usuario.setRoles(Arrays.asList(new Role(2, "Usuario", "ROLE_USER")));
		model.addAttribute("usuario", usuario);
		return "form";
	}

	// BINDINGRESULT ES EL RESULTADO DE LA VALIDACION Y CONTIENE LOS MENSAJES DE
	// ERROR, ES PROPIO DE SPRING Y SE INYECTA DE FORMA AUTOMATICA SI ESTA EL VALID
	// EL BINDINGRESULT DEBE ESTAR INMEDIATAMENTE DESPUES DEL OBJETO QUE SE VALIDA

	@PostMapping("/form")
	public String procesar(@Valid Usuario usuario, BindingResult result, Model model) {

		// validador.validate(usuario, result); lo comento porque ahora lo automatizo
		// con initBinder

		if (result.hasErrors()) {
			model.addAttribute("titulo", "Resultado form");
			return "form";
		}

		return "redirect:/ver";
	}

	//ESTE REDIRECT ES PARA QUE NO SE REENVIE EL FORMULARIO CON F5
	@GetMapping("/ver")
	public String ver(@SessionAttribute(name= "usuario", required= false) Usuario usuario, Model model, SessionStatus status) {
		
		if(usuario == null) {
			return "redirect:/form";
		}
		
		
		model.addAttribute("titulo", "Resultado form");
		//COMO USUARIO ESTA EN EL SESSIONATTRIBUTE NO HACE FALTA PASARLO
		
		// para terminar la sesion
		status.setComplete();
		return "resultado";
	}
}
