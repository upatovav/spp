package com.github.upatovav.spp.testtask;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.upatovav.spp.testtask.dto.TransactionDto;
import com.github.upatovav.spp.testtask.dto.ValidationErrorDto;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.util.Assert;

import javax.validation.constraints.AssertTrue;

@WebMvcTest
@AutoConfigureMockMvc
class TestTaskApplicationTests {

	@Autowired
	Controller controller;

	@Autowired
	MockMvc mockMvc;

	ObjectMapper objectMapper = new ObjectMapper();

	private static String SPRING_VALIDATION = "/validateWithSpring";

	private static String SELLER = "seller";

	private static String CUSTOMER = "customer";

	private static String PRODUCTS = "products";

	private static String NOT_EMPTY_MESSAGE = "must not be empty";

	private static String LENGTH_9 = "length must be between 9 and 9";

	private static String LENGTH_13 = "length must be between 13 and 13";

	private static String NOT_NULL = "must not be null";

	@Test
	void contextLoads() {
	}

	@Test
	void testSpringValidationOk() throws Exception {
		mockMvc.perform(
				MockMvcRequestBuilders.post(SPRING_VALIDATION)
				.content(asJsonString(buildOkTransactionDto()))
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(MockMvcResultMatchers.status().isOk());

	}

	@Test
	void testSpringValidationEmpty() throws Exception {
		ValidationErrorDto result = parseResult(mockMvc.perform(
				MockMvcRequestBuilders.post(SPRING_VALIDATION)
						.content("{}")
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(MockMvcResultMatchers.status().is(400))
				.andReturn().getResponse().getContentAsString());

		Assert.isTrue(result.getFieldErrors().size() == 3, "field count does not match");
		Assert.isTrue(result.getFieldErrors().get(SELLER).size() == 1, "seller messages count does not match");
		Assert.isTrue(result.getFieldErrors().get(SELLER).get(0).equals(NOT_NULL), "seller message text does not match");
		Assert.isTrue(result.getFieldErrors().get(CUSTOMER).size() == 1, "customer messages count does not match");
		Assert.isTrue(result.getFieldErrors().get(CUSTOMER).get(0).equals(NOT_NULL), "customer message text does not match");
		Assert.isTrue(result.getFieldErrors().get(PRODUCTS).size() == 1, "products messages count does not match");
		Assert.isTrue(result.getFieldErrors().get(PRODUCTS).get(0).equals(NOT_EMPTY_MESSAGE), "products message text does not match");
	}

	@Test
	void testSpringValidationLength() throws Exception {
		TransactionDto transactionDto = buildOkTransactionDto();
		transactionDto.setCustomer("0123456");
		transactionDto.setSeller("123");
		transactionDto.getProducts().get(0).setName("");
		transactionDto.getProducts().get(1).setCode("321456");

		ValidationErrorDto result = parseResult(mockMvc.perform(
				MockMvcRequestBuilders.post(SPRING_VALIDATION)
						.content(asJsonString(transactionDto))
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(MockMvcResultMatchers.status().is(400))
				.andReturn().getResponse().getContentAsString());

		Assert.isTrue(result.getFieldErrors().size() == 4, "field count does not match");
		Assert.isTrue(result.getFieldErrors().get(SELLER).size() == 1, "seller messages count does not match");
		Assert.isTrue(result.getFieldErrors().get(SELLER).get(0).equals(LENGTH_9), "seller message text does not match");
		Assert.isTrue(result.getFieldErrors().get(CUSTOMER).size() == 1, "customer messages count does not match");
		Assert.isTrue(result.getFieldErrors().get(CUSTOMER).get(0).equals(LENGTH_9), "customer message text does not match");
		Assert.isTrue(result.getFieldErrors().get(PRODUCTS+"[0].name").size() == 1, "product name messages count does not match");
		Assert.isTrue(result.getFieldErrors().get(PRODUCTS+"[0].name").get(0).equals(NOT_EMPTY_MESSAGE), "product name message text does not match");
		Assert.isTrue(result.getFieldErrors().get(PRODUCTS+"[0].name").size() == 1, "product code messages count does not match");
		Assert.isTrue(result.getFieldErrors().get(PRODUCTS+"[1].code").get(0).equals(LENGTH_13), "product code message text does not match");
	}

	@Test
	void testSpringValidationNull() throws Exception {
		TransactionDto transactionDto = buildOkTransactionDto();
		transactionDto.setCustomer(null);
		transactionDto.setSeller(null);
		transactionDto.getProducts().get(0).setName(null);
		transactionDto.getProducts().get(1).setCode(null);

		ValidationErrorDto result = parseResult(mockMvc.perform(
				MockMvcRequestBuilders.post(SPRING_VALIDATION)
						.content(asJsonString(transactionDto))
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(MockMvcResultMatchers.status().is(400))
				.andReturn().getResponse().getContentAsString());

		Assert.isTrue(result.getFieldErrors().size() == 4, "field count does not match");
		Assert.isTrue(result.getFieldErrors().get(SELLER).size() == 1, "seller messages count does not match");
		Assert.isTrue(result.getFieldErrors().get(SELLER).get(0).equals(NOT_NULL), "seller message text does not match");
		Assert.isTrue(result.getFieldErrors().get(CUSTOMER).size() == 1, "customer messages count does not match");
		Assert.isTrue(result.getFieldErrors().get(CUSTOMER).get(0).equals(NOT_NULL), "customer message text does not match");
		Assert.isTrue(result.getFieldErrors().get(PRODUCTS+"[0].name").size() == 1, "product name messages count does not match");
		Assert.isTrue(result.getFieldErrors().get(PRODUCTS+"[0].name").get(0).equals(NOT_EMPTY_MESSAGE), "product name message text does not match");
		Assert.isTrue(result.getFieldErrors().get(PRODUCTS+"[0].name").size() == 1, "product code messages count does not match");
		Assert.isTrue(result.getFieldErrors().get(PRODUCTS+"[1].code").get(0).equals(NOT_NULL), "product code message text does not match");
	}

	@SneakyThrows
	private String asJsonString(final Object obj) {
		return objectMapper.writeValueAsString(obj);
	}

	@SneakyThrows
	private ValidationErrorDto parseResult(String result){
		return objectMapper.readValue(result, ValidationErrorDto.class);
	}

	private TransactionDto buildOkTransactionDto(){
		return TransactionDto.builder()
				.customer("012345678")
				.seller("012345679")
				.product(ProductDto.builder()
						.name("product1")
						.code("0123456789012")
						.build())
				.product(ProductDto.builder()
						.name("product2")
						.code("0123456789013")
						.build())
				.build();
	}

}
