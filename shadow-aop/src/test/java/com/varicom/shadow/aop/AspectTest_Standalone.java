package com.varicom.shadow.aop;

import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

import com.varicom.shadow.aop.demo.controller.LoggerController;

public class AspectTest_Standalone {

	private MockMvc mockMvc;

	@Before
	public void setUp() {
		LoggerController loggerController = new LoggerController();
		mockMvc = standaloneSetup(loggerController).build();
	}

	@Test
	public void testView() throws Exception {
		MvcResult result = mockMvc
				.perform(MockMvcRequestBuilders.get("/logger").header("traceId", "122112121212"))
				.andDo(MockMvcResultHandlers.print()).andReturn();

		Assert.assertNotNull(result.getModelAndView().getModel().get("user"));
	}

}
