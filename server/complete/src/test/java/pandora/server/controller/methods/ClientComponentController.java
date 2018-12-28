package pandora.server.controller.methods;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import pandora.server.controller.ClientController;
import pandora.server.dto.PandoraClientDTO;
import pandora.server.service.PandoraService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

import java.util.Arrays;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ClientComponentController {

    private PandoraClientDTO pandoraClient;

    @MockBean
    private PandoraService pandoraService;

    @Before
    public void loadLocalIntances() throws Exception {
        pandoraClient =  PandoraClientDTO.ofDefault();
    }

    @Test
    public void index() throws Exception {

        //GIVEN
        given(pandoraService.findAll())
                .willReturn(Arrays.asList(pandoraClient));

        //WHEN
        List<PandoraClientDTO> pandoraClientList =
                pandoraService.findAll();

        //THEN
        assertThat(pandoraClientList.size()).isEqualTo(1);
        assertThat(pandoraClientList.get(0).getId())
                .isEqualTo(pandoraClient.getId());
        assertThat(pandoraClientList.get(0).getHostname())
                .isEqualTo(pandoraClient.getHostname());
        assertThat(pandoraClientList.get(0).getState())
                .isEqualTo(pandoraClient.getState());

    }


}
