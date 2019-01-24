package org.opensrp.web.controller;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.json.JSONException;
import org.opensrp.core.entity.HealthId;
import org.opensrp.core.service.HealthIdService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class HealthIdController {
	
	@Autowired
	private HealthIdService healthIdService;

	@Autowired
	private HealthId healthId;
	
	
	
	@PostAuthorize("hasPermission(returnObject, 'PERM_UPLOAD_HEALTH_ID')")
	@RequestMapping(value = "/healthId/upload_csv.html", method = RequestMethod.GET)
	public String csvUpload(HttpSession session, ModelMap model, Locale locale) throws JSONException {
		model.addAttribute("locale", locale);
		return "/health-id/upload_csv";
	}
	
	@PostAuthorize("hasPermission(returnObject, 'PERM_UPLOAD_HEALTH_ID')")
	@RequestMapping(value = "/healthId/upload_csv.html", method = RequestMethod.POST)
	public ModelAndView csvUpload(@RequestParam MultipartFile file, HttpServletRequest request, ModelMap model, Locale locale)
	    throws Exception {
		if (file.isEmpty()) {
			model.put("msg", "failed to upload file because its empty");
			model.addAttribute("msg", "failed to upload file because its empty");
			return new ModelAndView("/health-id/upload_csv");
		} else if (!"text/csv".equalsIgnoreCase(file.getContentType())) {
			model.addAttribute("msg", "file type should be '.csv'");
			return new ModelAndView("/health-id/upload_csv");
		}
		
		String rootPath = request.getSession().getServletContext().getRealPath("/");
		File dir = new File(rootPath + File.separator + "uploadedfile");
		if (!dir.exists()) {
			dir.mkdirs();
		}
		
		File csvFile = new File(dir.getAbsolutePath() + File.separator + file.getOriginalFilename());
		
		try {
			try (InputStream is = file.getInputStream();
			        BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(csvFile))) {
				int i;
				
				while ((i = is.read()) != -1) {
					stream.write(i);
				}
				stream.flush();
			}
		}
		catch (IOException e) {
			model.put("msg", "failed to process file because : " + e.getMessage());
			return new ModelAndView("/health-id/upload_csv");
		}
		String msg = healthIdService.uploadHealthId(csvFile);
		
		model.addAttribute("locale", locale);
		if (!msg.isEmpty()) {
			model.put("msg", msg);
			return new ModelAndView("/health-id/upload_csv");
		}
		return new ModelAndView("redirect:/cbhc-dashboard?lang=" + locale);
	}
	
}
