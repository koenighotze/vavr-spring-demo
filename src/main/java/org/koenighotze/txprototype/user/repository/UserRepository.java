package org.koenighotze.txprototype.user.repository;

import io.vavr.collection.*;
import io.vavr.control.*;
import org.koenighotze.txprototype.user.model.*;
import org.springframework.data.mongodb.repository.*;

/**
 * @author David Schmitz
 */
public interface UserRepository extends MongoRepository<User, String> {
    Option<User> findByPublicId(String publicId);

    Seq<User> findByLastname(String lastname);
}
