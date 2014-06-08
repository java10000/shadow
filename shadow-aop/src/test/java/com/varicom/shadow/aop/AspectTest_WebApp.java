package com.varicom.shadow.aop;

import static com.varicom.shadow.aop.constant.AopConstant.DTS_TRACE_ID;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.ContextHierarchy;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.varicom.shadow.utils.SleepUtil;

//注解风格
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration(value = "src/main/webapp")
//@ContextHierarchy({
//		@ContextConfiguration(name = "parent", classes = AppConfig.class),
//		@ContextConfiguration(name = "child", classes = MvcConfig.class) })

@ContextHierarchy({
    @ContextConfiguration(name = "parent", locations = "classpath:/applicationContextTest.xml")
})
public class AspectTest_WebApp {

	@Autowired
	private WebApplicationContext wac;

	private MockMvc mockMvc;

	@Before
	public void setUp() {
		mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
	}

	@Test
	public void testView() throws Exception {
		for (int i = 0; i < 10000; i++) {
			mockMvc
			.perform(MockMvcRequestBuilders.get("/logger").header(DTS_TRACE_ID,"122112121212"))
			.andDo(MockMvcResultHandlers.print()).andReturn();
			SleepUtil.sleepSeconds(1);
		}


		// Assert.assertNotNull(result.getModelAndView().getModel().get("user"));
	}
}