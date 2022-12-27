package com.blu.reservation;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import javax.annotation.Resource;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;

@AutoConfigureMockMvc
public class SpringMockMVCHelper {

    @Resource
    protected MockMvc mockMvc;

    private final static ObjectMapper JSON_MAPPER = new ObjectMapper()
        .enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
        .setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.NONE);

    public String performPostRequestByJson(final String path, final String content, int expectedStatus) throws Exception {

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders
                .post(path)
                .content(content)
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .accept(MediaType.APPLICATION_JSON_UTF8))
            .andExpect(MockMvcResultMatchers.status().is(expectedStatus))
            .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON_UTF8))
            .andDo(MockMvcResultHandlers.print())
            .andReturn();

        return responseContentAsString(mvcResult);
    }
    public String performPostRequest(final String path, final Object content, int expectedStatus) throws Exception {

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders
                .post(path)
                .content(createJSONFromObject(content))
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .accept(MediaType.APPLICATION_JSON_UTF8))
            .andExpect(MockMvcResultMatchers.status().is(expectedStatus))
            .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON_UTF8))
            .andDo(MockMvcResultHandlers.print())
            .andReturn();

        return responseContentAsString(mvcResult);
    }



    public String performGetRequest(final String path, int expectedStatus) throws Exception {
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get(path)
                .accept(MediaType.APPLICATION_JSON_UTF8))
            .andExpect(MockMvcResultMatchers.status().is(expectedStatus))
            .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON_UTF8))
            .andDo(MockMvcResultHandlers.print())
            .andReturn();

        return responseContentAsString(mvcResult);
    }

    public String performDeleteRequest(final String path, int expectedStatus) throws Exception {
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.delete(path)
                .content(createJSONFromObject(null))
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .accept(MediaType.APPLICATION_JSON_UTF8))
            .andExpect(MockMvcResultMatchers.status().is(expectedStatus))
            .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON_UTF8))
            .andDo(MockMvcResultHandlers.print())
            .andReturn();
        return responseContentAsString(mvcResult);
    }

    private String responseContentAsString(MvcResult mvcResult) throws UnsupportedEncodingException {
        return mvcResult.getResponse().getContentAsString(StandardCharsets.UTF_8);
    }

    public static <T> String createJSONFromObject(final T object) {

        try {
            return JSON_MAPPER.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }


}
