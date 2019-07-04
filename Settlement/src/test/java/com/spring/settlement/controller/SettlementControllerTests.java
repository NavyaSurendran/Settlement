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
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss");
		java.util.Date date =  sdf.parse("2018-10-20 12:47:55"); 
		java.sql.Date date1 = new java.sql.Date(date.getTime()); 
		
		java.util.Date date2 = sdf.parse("2018-10-21 12:47:55");
		java.sql.Date date3 = new java.sql.Date(date2.getTime());
		
		
		
		transaction = new Transaction("TX10006", "ACC334455", "ACC998877",date1, 10.5, "PAYMENT", null);
		transactions.add(transaction);
		transaction = new Transaction("TX10007", "ACC334455", "ACC998877",date1, 10.5, "PAYMENT", null);
		 transactions.add(transaction);
		 transaction = new Transaction("TX100088", "ACC334455", "ACC998877",date3, 10.5, "PAYMENT", "TX10006");
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
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss");
		java.util.Date date =  sdf.parse("2018-10-20 12:47:55"); 
		java.sql.Date date1 = new java.sql.Date(date.getTime()); 
		
		java.util.Date date2 = sdf.parse("2018-10-21 12:47:55");
		java.sql.Date date3 = new java.sql.Date(date2.getTime());
		
		TransactionDTO dto = new TransactionDTO();
		dto.setAccountId("ACC334455");
		dto.setToDate(date3);
		dto.setFromDate(date1);
		Map<String, Object> result = new HashMap<>();
		result.put("Relative Amount", 7.5);
		result.put("Number of Transactions Included", 3);
			
		when(service.getRelativeAccountBalace("ACC334455", date1, date3)).thenReturn(result);
		mvc.perform(post("/api/v1/settlement/transactions")
				.contentType(MediaType.APPLICATION_JSON)
				.content(jsonToString(dto)))
			.andExpect(status().isOk());
		//verify(service, times(1)).getRelativeAccountBalace("ACC334455", date1, date3);
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
