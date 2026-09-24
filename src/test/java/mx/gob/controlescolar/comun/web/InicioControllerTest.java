package mx.gob.controlescolar.comun.web;

import mx.gob.controlescolar.comun.aplicacion.EstadoPlataformaService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(InicioController.class)
@AutoConfigureMockMvc(addFilters = false)
class InicioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private EstadoPlataformaService estadoPlataformaService;

    @Test
    void laPantallaDeEjemploResponde() throws Exception {
        when(estadoPlataformaService.nombre()).thenReturn("Control escolar");

        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Control escolar")));
    }
}
