package com.volchok.productinfo;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest
public class ApiControllerTest {
    @Autowired
    private WebApplicationContext webApplicationContext;

    @Test
    public void positiveScenarioEnrich() throws Exception {
        MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();

        MockHttpServletRequestBuilder builder =
                MockMvcRequestBuilders.post("/api/v1/enrich")
                        .contentType("text/csv")
                        .accept("text/csv")
                        .content("date,product_id,currency,price\n20160101,1,EUR,10");
        mockMvc.perform(builder)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content()
                        .string("DATE,PRODUCT_NAME,CURRENCY,PRICE\n20160101,Test1,EUR,10\n"));
        ;
    }

    @Test
    public void wrongDataTypeEnrich() throws Exception {
        MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();

        MockHttpServletRequestBuilder builder =
                MockMvcRequestBuilders.post("/api/v1/enrich")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept("text/csv")
                        .content("date,product_id,currency,price\n20160101,1,EUR,10");
        mockMvc.perform(builder)
                .andExpect(MockMvcResultMatchers.status().is(415));
        ;
    }
}