package org.koenighotze.txprototype.user.resources;

import org.springframework.hateoas.ResourceSupport;
import org.springframework.ui.Model;

/**
 * @author David Schmitz
 */
public class ResourceUtils {
    public static Model addLinksToModel(ResourceSupport resource, Model model) {
        resource.getLinks().forEach(link -> model.addAttribute(link.getRel(), link.getHref()));
        return model;
    }
}
