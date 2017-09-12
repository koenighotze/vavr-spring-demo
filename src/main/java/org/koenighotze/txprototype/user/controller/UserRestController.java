package org.koenighotze.txprototype.user.controller;

import static io.vavr.API.$;
import static io.vavr.API.Case;
import static io.vavr.API.Match;
import static io.vavr.Patterns.$Some;
import static io.vavr.Predicates.instanceOf;
import static org.koenighotze.txprototype.user.controller.IanaRel.COLLECTION;
import static org.springframework.hateoas.Link.REL_SELF;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.HttpStatus.PERMANENT_REDIRECT;
import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.DELETE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.PUT;

import java.net.*;
import javax.inject.*;

import io.vavr.collection.*;
import io.vavr.control.*;
import org.koenighotze.txprototype.user.model.*;
import org.koenighotze.txprototype.user.repository.*;
import org.koenighotze.txprototype.user.resources.*;
import org.springframework.http.*;
import org.springframework.stereotype.*;
import org.springframework.web.bind.annotation.*;

/**
 * @author David Schmitz
 */
@Controller
@RequestMapping(value = "/users", produces = APPLICATION_JSON_UTF8_VALUE)
public class UserRestController {

    private final UserRepository userRepository;

    @Inject
    public UserRestController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @RequestMapping(method = GET)
    public HttpEntity<UsersResource> getAllUsers() {
        //@formatter:off
        return new ResponseEntity<>(
                new UsersResource(List.ofAll(userRepository.findAll())
                        .map(UserResource::new)), OK);
        //@formatter:on
    }

    // example PUT for creating resource
    @RequestMapping(value = "/{publicId}", method = PUT, consumes = APPLICATION_JSON_UTF8_VALUE)
    public HttpEntity<UserResource> newUser(@PathVariable("publicId") String publicId, @RequestBody User user) {
        Option<User> storedUser = userRepository.findByPublicId(publicId);
        HttpStatus httpStatus = storedUser.map(u -> OK)
                                          .getOrElse(CREATED);

        User userToStore = storedUser.map(foundUser -> updateUserFields(user, foundUser))
                                     .getOrElse(user);

        UserResource userResource = new UserResource(userToStore);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setLocation(URI.create(userResource.getLink(REL_SELF)
                                                       .getHref()));
        userRepository.save(userToStore);

        return new ResponseEntity<>(userResource, httpHeaders, httpStatus);
    }

    private User updateUserFields(User user, User foundUser) {
        foundUser.setEmail(user.getEmail());
        foundUser.setLastname(user.getLastname());
        foundUser.setFirstname(user.getFirstname());
        return foundUser;
    }

    @RequestMapping(value = "/{publicId}", method = DELETE)
    public ResponseEntity<?> deleteUserByPublicId(@PathVariable String publicId) {
        //@formatter:off
        Option<User> userOption = userRepository.findByPublicId(publicId);

        Option<HttpHeaders> result =
                userOption
                        .map(user -> {
                            userRepository.delete(user);
                            return user;
                        })
                        .map(UserResource::new)
                        .map(userResource -> userResource.getLink(COLLECTION.getRel()).getHref())
                        .map(href -> {
                            HttpHeaders httpHeaders = new HttpHeaders();
                            httpHeaders.setLocation(URI.create(href));
                            return httpHeaders;
                        });

        return Match(result).of(
                Case($Some($(instanceOf(HttpHeaders.class))), h -> new ResponseEntity<>(null, h, PERMANENT_REDIRECT)),
                Case($(), new ResponseEntity<>(null, NOT_FOUND))
        );
        //@formatter:on
    }

    @RequestMapping(value = "/{publicId}", method = GET)
    public ResponseEntity<UserResource> userByPublicId(@PathVariable String publicId) {
        //@formatter:off
        return Match(Option.of(userRepository.findByPublicId(publicId))).of(
                Case($Some($(instanceOf(User.class))), user -> new ResponseEntity<>(new UserResource(user), OK)),
                Case($(), new ResponseEntity<>(NOT_FOUND))
        );
        //@formatter:on
    }

}
