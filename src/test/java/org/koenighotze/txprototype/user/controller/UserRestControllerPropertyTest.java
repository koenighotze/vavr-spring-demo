package org.koenighotze.txprototype.user.controller;

import static java.util.UUID.randomUUID;
import static org.koenighotze.txprototype.user.controller.ArbitraryData.arbitraryNick;
import static org.koenighotze.txprototype.user.controller.ArbitraryData.arbitraryUnicodeString;
import static org.koenighotze.txprototype.user.controller.ArbitraryData.rfcEmail;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

import java.io.*;

import io.vavr.collection.*;
import io.vavr.test.*;
import org.junit.*;
import org.junit.runner.*;
import org.koenighotze.txprototype.user.*;
import org.koenighotze.txprototype.user.model.*;
import org.slf4j.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.boot.test.context.*;
import org.springframework.http.converter.*;
import org.springframework.http.converter.json.*;
import org.springframework.mock.http.*;
import org.springframework.test.context.junit4.*;
import org.springframework.test.context.web.*;
import org.springframework.test.web.servlet.*;
import org.springframework.web.context.*;

/**
 * @author David Schmitz
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = {UserAdministrationApplication.class }) //, EmbeddedMongoAutoConfiguration.class})
@WebAppConfiguration
public class UserRestControllerPropertyTest {
    private static final Logger logger = LoggerFactory.getLogger(UserRestControllerPropertyTest.class);

    @Autowired
    private WebApplicationContext webApplicationContext;
    private MockMvc mockMvc;
    private HttpMessageConverter mappingJackson2HttpMessageConverter;

    @Autowired
    void setConverters(HttpMessageConverter<?>[] converters) {
        mappingJackson2HttpMessageConverter = List.of(converters)
                                                  .filter(hmc -> hmc instanceof MappingJackson2HttpMessageConverter)
                                                  .take(1)
                                                  .getOrElseThrow(() -> new RuntimeException("No Converter found!"));
    }

    @SuppressWarnings("unchecked")
    private String json(Object o) throws IOException {
        MockHttpOutputMessage mockHttpOutputMessage = new MockHttpOutputMessage();
        mappingJackson2HttpMessageConverter.write(o, APPLICATION_JSON, mockHttpOutputMessage);
        return mockHttpOutputMessage.getBodyAsString();
    }

    @Before
    public void setup() {
        this.mockMvc = webAppContextSetup(webApplicationContext).build();
    }

    @Test
    public void getting_users_should_always_return_404_or_200() {
        Property.def("Should not go boom")
                .forAll(arbitraryUnicodeString())
                .suchThat((String id) -> List.ofAll(NOT_FOUND.value(), OK.value())
                                             .contains(fetchUser(id)))
                .check()
                .assertIsSatisfied();
    }

    @Test
    public void create_a_user_using_put() throws Exception {
        //@formatter:off
        Property.def("Should not go boom")
                .forAll(arbitraryUnicodeString(),
                        arbitraryUnicodeString(),
                        arbitraryNick(),
                        rfcEmail())
                .suchThat((String name,
                           String last,
                           String nick,
                           String email) -> createUser(name, last, nick, email) == CREATED.value())
                .check()
                .assertIsSatisfied();
        //@formatter:on
    }

    private int createUser(String name, String last, String nick, String email) throws Exception {
        User user = new User(randomUUID().toString(), name, last, nick, email);

        logger.info("Creating publicid={}, name = {}, email = {}", user.getPublicId(), name, email);
        ResultActions perform = mockMvc.perform(put("/users/{id}", user.getPublicId()).contentType(APPLICATION_JSON)
                                                                                      .content(json(user)));
        return perform.andReturn()
                      .getResponse()
                      .getStatus();
    }

    private Integer fetchUser(String id) throws Exception {
        logger.info("Fetching publicid={}", id);
        return mockMvc.perform(get("/users/{id}", id))
                      .andReturn()
                      .getResponse()
                      .getStatus();
    }

}
