package org.koenighotze.txprototype.user.resources;

import static java.util.Comparator.comparing;
import static java.util.Objects.requireNonNull;
import static org.koenighotze.txprototype.user.controller.IanaRel.COLLECTION;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

import java.util.Comparator;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.koenighotze.txprototype.user.controller.UserRestController;
import org.koenighotze.txprototype.user.model.User;
import org.springframework.hateoas.ResourceSupport;

/**
 * @author David Schmitz
 */
public class UserResource extends ResourceSupport {
    private User user;

    @JsonCreator
    public UserResource(@JsonProperty("user") User user) {
        this.user = requireNonNull(user);
        add(linkTo(methodOn(UserRestController.class).getAllUsers()).withRel(COLLECTION.getRel()));
        add(linkTo(methodOn(UserRestController.class, user.getPublicId()).userByPublicId(user.getPublicId())).withSelfRel());
    }

    public static Comparator<UserResource> compareByLastAndFirstName() {
        //@formatter:off
        return comparing((UserResource user) -> user.getUser().getLastname())
                .thenComparing((UserResource user) -> user.getUser().getFirstname());
        //@formatter:on
    }

    public User getUser() {
        return user;
    }
}
