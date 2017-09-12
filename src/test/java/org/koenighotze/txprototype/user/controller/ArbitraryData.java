package org.koenighotze.txprototype.user.controller;

import static io.vavr.test.Arbitrary.string;
import static io.vavr.test.Gen.choose;

import io.vavr.*;
import io.vavr.collection.*;
import io.vavr.test.*;

/**
 * @author David Schmitz
 */
class ArbitraryData {
    private static Gen<Character> localPart() {
        Array<Character> characters = Array.ofAll("!#$%&'*+-/=?^_`{|}~".toCharArray());

        return Gen.frequency(
            Tuple.of(1, Gen.choose('a', 'z')),
            Tuple.of(1, Gen.choose('0', '9')),
            Tuple.of(1, Gen.choose('A', 'Z')),
            Tuple.of(1, Gen.choose(characters)),
            Tuple.of(1, Gen.of('.'))
        );
    }

    private static Gen<Character> domainPart() {
        return Gen.frequency(
            Tuple.of(1, Gen.choose('a', 'z')),
            Tuple.of(1, Gen.choose('A', 'Z')),
            Tuple.of(1, Gen.choose('0', '9')),
            Tuple.of(1, Gen.of('-')),
            Tuple.of(1, Gen.of('.'))
        );
    }

    // not really 100% rfc compatible
    static Arbitrary<String> rfcEmail() {
        //@formatter:off
        Arbitrary<String> arbitraryLocal =
            string(localPart())
                    .filter(localPart -> !"".equals(localPart))
                    .filter(localPart -> !localPart.startsWith("."))
                    .filter(localPart -> !localPart.endsWith("."))
                    .filter(localPart -> localPart.split("\\.").length <= 1);

        Arbitrary<String> arbitraryDomain =
            string(domainPart())
                    .filter(domainPart -> !"".equals(domainPart))
                    .filter(domainPart -> !domainPart.startsWith("-"))
                    .filter(domainPart -> !domainPart.endsWith("-"))
                    .filter(domainPart -> !domainPart.startsWith("."))
                    .filter(domainPart -> !domainPart.endsWith("."))
                    .filter(domainPart -> !domainPart.contains(".."));
        //@formatter:on

        return arbitraryLocal
            .flatMap(localPart -> arbitraryDomain.map(domain -> localPart + "@" + domain));
    }

    static Arbitrary<String> arbitraryNick() {
        return string(choose('a', 'z'));
    }

    static Arbitrary<String> arbitraryUnicodeString() {
        Gen<Character> randomUnicode = random -> (char) random.nextInt();

        return string(randomUnicode);
    }
}
