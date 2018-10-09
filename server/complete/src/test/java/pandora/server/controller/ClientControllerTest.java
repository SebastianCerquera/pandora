package pandora.server.controller;

import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.core.Is.is;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import pandora.server.dto.PandoraClientDTO;
import pandora.server.util.JsonUtils;
import java.util.Arrays;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class ClientControllerTest {

	private PandoraClientDTO pandoraClient;
	private String clientPath;

	@Autowired
	private MockMvc mvc;

	@MockBean
	private ClientController clientController;

	@Before
	public void loadLocalIntances() throws Exception {
		pandoraClient =  PandoraClientDTO.ofDefault();
		clientPath = String.valueOf("/v1/clients");
	}


	@Test
	public void getAll() throws Exception{
		//GIVEN
		given(clientController.index())
				.willReturn(new ResponseEntity<>(
						Arrays.asList(pandoraClient), HttpStatus.OK));

		mvc.perform(
				//WHEN
				get(clientPath)
						.accept(APPLICATION_JSON_VALUE))
				//THEN
				.andExpect(status().isOk())
				.andExpect(jsonPath("$", hasSize(1)))
				.andExpect(jsonPath("$[0].id", is(pandoraClient.getId().intValue())))
				.andExpect(jsonPath("$[0].hostname", is(pandoraClient.getHostname())))
				.andExpect(jsonPath("$[0].state", is(pandoraClient.getState().toString())))
				.andExpect(jsonPath("$[0].problems[0].delay",
						is(pandoraClient.getProblems().get(0).getDelay())))
				.andExpect(jsonPath("$[0].problems[0].id",
						is(pandoraClient.getProblems().get(0).getId().intValue())))
				.andExpect(jsonPath("$[0].problems[0].modulus",
						is(pandoraClient.getProblems().get(0).getModulus())))
				.andExpect(jsonPath("$[0].problems[0].secret",
						is(pandoraClient.getProblems().get(0).getSecret())))
				.andExpect(jsonPath("$[0].problems[0].state",
						is(pandoraClient.getProblems().get(0).getState().toString())))
				.andExpect(jsonPath("$[0].problems[0].images[0].id",
						is(pandoraClient.getProblems().get(0).getImages().get(0).getId().intValue())))
				.andExpect(jsonPath("$[0].problems[0].images[0].name",
						is(pandoraClient.getProblems().get(0).getImages().get(0).getName())))
		//;
		.andDo(print());

		verify(clientController, times (1)).index();
		verifyNoMoreInteractions(clientController);

	}

	@Test
	public void create() throws Exception{

		given(clientController.create(pandoraClient))
				.willReturn(new ResponseEntity<>(pandoraClient, HttpStatus.CREATED));

		mvc.perform(
				post(clientPath)
						.content(JsonUtils.asJsonString(pandoraClient))
						.contentType(APPLICATION_JSON_VALUE)
						.accept(APPLICATION_JSON_VALUE))
				.andDo(print());
				//.andExpect(status().isCreated()).andDo(print());

//		verify(clientController, times (1)).create(pandoraClient);
//		verifyNoMoreInteractions(clientController);
	}

	@Test
	public void deleteById()  throws Exception {

		given(clientController.deleteById(Long.valueOf(1)))
				.willReturn(new ResponseEntity<>(Boolean.TRUE, HttpStatus.OK));

		mvc.perform(
				delete(clientPath.concat("/{id}"), "1")
						.contentType(APPLICATION_JSON_VALUE)
						.accept(APPLICATION_JSON_VALUE))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$", is(Boolean.TRUE)))
		;
		//.andDo(print());

		verify(clientController, times (1)).deleteById(Long.valueOf(1));
		verifyNoMoreInteractions(clientController);
	}

	@Test
	public void update () throws Exception {

		pandoraClient.setHostname("127.0.0.1");
		given(clientController.update(pandoraClient))
				.willReturn(new ResponseEntity<>(pandoraClient, HttpStatus.OK));

		mvc.perform(
				put(clientPath)
						.content(JsonUtils.asJsonString(pandoraClient))
						.contentType(APPLICATION_JSON_UTF8)
						.accept(APPLICATION_JSON_UTF8))
				.andExpect(status().isOk()).andDo(print());

//		verify(clientController, times (1)).update(pandoraClient);
//		verifyNoMoreInteractions(clientController);

	}

}
