package com.ecom.service;

import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;


import java.text.NumberFormat;
import java.util.Locale;

@Service
public class CommonServiceImpl implements CommonService {

		@Override
		public String formatVnd(Double price) {
			if (price == null) return "0";
			NumberFormat formatter = NumberFormat.getInstance(new Locale("vi", "VN"));
			return formatter.format(price);
		}



		@Override
		public void removeSessionMessage() {
			HttpServletRequest request = ((ServletRequestAttributes) (RequestContextHolder.getRequestAttributes()))
					.getRequest();
			HttpSession session = request.getSession();
			session.removeAttribute("succMsg");
			session.removeAttribute("errorMsg");
		}
	}


