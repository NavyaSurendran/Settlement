package com.spring.settlement.controller;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.sql.Date;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.spring.dto.TransactionDTO;
import com.spring.settlement.entity.Transaction;
import com.spring.settlement.service.TransactionService;


@RunWith(SpringRunner.class)
@WebMvcTest(SettlementController.class)
public class SettlementControllerTests {
	
	@Autowired
	private transient MockMvc mvc;
	
	@MockBean
	private transient TransactionService service;
	
	@InjectMocks
	private SettlementController controller;
	
	private Transaction transaction;
	
	static List<Transaction> transactions;	
	
	private LocalDateTime localDateTime;

	@Before
	public void setup() throws ParseException {
		MockitoAnnotations.initMocks(this);
		mvc = MockMvcBuilders.standaloneSetup(controller).build();
		List<Transaction> transactions = new ArrayList<>();
		
		
		java.sql.Timestamp date = java.sql.Timestamp.valueOf("2018-10-20 12:47:55");
		java.sql.Timestamp date2 = java.sql.Timestamp.valueOf("2018-10-21 12:47:55");
		
		
		transaction = new Transaction("TX10006", "ACC334455", "ACC998877",date, 10.5, "PAYMENT", null);
		transactions.add(transaction);
		transaction = new Transaction("TX10007", "ACC334455", "ACC998877",date, 10.5, "PAYMENT", null);
		 transactions.add(transaction);
		 transaction = new Transaction("TX100088", "ACC334455", "ACC998877",date2, 10.5, "PAYMENT", "TX10006");
		 transactions.add(transaction);

	}
	
	@Test
	public void testLoadDataFromCSV() throws Exception {
		when(service.saveAll(transactions)).thenReturn(true);
		mvc.perform(get("/api/v1/settlement")
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isCreated());
		verify(service, times(1)).saveAll(Mockito.any());		
	}
	
	@Test
	public void testRetrieveBalanceForPeriod() throws Exception {
		
		
		java.sql.Timestamp date = java.sql.Timestamp.valueOf("2018-10-20 12:47:55");
		java.sql.Timestamp date2 = java.sql.Timestamp.valueOf("2018-10-21 12:47:55");
		
		TransactionDTO dto = new TransactionDTO();
		dto.setAccountId("ACC334455");
		dto.setToDate(date2);
		dto.setFromDate(date);
		Map<String, Object> result = new HashMap<>();
		result.put("Relative Amount", 7.5);
		result.put("Number of Transactions Included", 3);
			
		when(service.getRelativeAccountBalace("ACC334455", date, date2)).thenReturn(result);
		mvc.perform(post("/api/v1/settlement/transactions")
				.contentType(MediaType.APPLICATION_JSON)
				.content(jsonToString(dto)))
			.andExpect(status().isOk());
	}
	
	
	private static String jsonToString(final Object obj) {
		String result;
		try {
			final ObjectMapper mapper = new ObjectMapper();
			final String jsonContent = mapper.writeValueAsString(obj);
			result = jsonContent;
			} catch (JsonProcessingException e) {
				result = "JSON Parsing error";				
			}
		return result;
	}
}
