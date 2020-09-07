package com.test.odataadapter.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.olingo.commons.api.data.ContextURL;
import org.apache.olingo.commons.api.edm.EdmEntitySet;
import org.apache.olingo.commons.api.format.ContentType;
import org.apache.olingo.commons.api.http.HttpStatusCode;
import org.apache.olingo.server.api.*;
import org.apache.olingo.server.api.processor.EntityProcessor;
import org.apache.olingo.server.api.processor.Processor;
import org.apache.olingo.server.api.serializer.ODataSerializer;
import org.apache.olingo.server.api.serializer.SerializerException;
import org.apache.olingo.server.api.uri.UriInfo;
import org.apache.olingo.server.api.uri.UriInfoResource;
import org.apache.olingo.server.api.uri.UriResource;
import org.apache.olingo.server.api.uri.UriResourceEntitySet;
import org.apache.olingo.server.api.uri.queryoption.ExpandOption;
import org.apache.olingo.server.api.uri.queryoption.SelectOption;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Locale;

abstract class GatewayProcessor implements Processor {
    static Logger logger = LogManager.getLogger(GatewayProcessor.class);
    protected OData oData;
    protected ServiceMetadata serviceMetadata;
    protected EdmProvider edmProvider;

    public GatewayProcessor(OData oData, ServiceMetadata serviceMetadata, EdmProvider edmProvider) {
        this.oData = oData;
        this.serviceMetadata = serviceMetadata;
        this.edmProvider = edmProvider;
    }

    @Override
    public void init(OData oData, ServiceMetadata serviceMetadata) {
        this.oData = oData;
        this.serviceMetadata = serviceMetadata;
    }

    protected EdmEntitySet getEdmEntitySet(final UriInfoResource uriInfo) throws ODataApplicationException {
        final List<UriResource> resourcePaths = uriInfo.getUriResourceParts();
        /*
         * To get the entity set we have to interpret all URI segments
         */
        if (!(resourcePaths.get(0) instanceof UriResourceEntitySet)) {
            throw new ODataApplicationException("Invalid resource type for first segment.",
                    HttpStatusCode.NOT_IMPLEMENTED.getStatusCode(), Locale.ENGLISH);
        }

        /*
         * Here we should interpret the whole URI but in this example we do not support navigation so we throw an exception
         */

        final UriResourceEntitySet uriResource = (UriResourceEntitySet) resourcePaths.get(0);
        return uriResource.getEntitySet();
    }

    protected ContextURL getContextUrl(final ODataSerializer serializer,
                                     final EdmEntitySet entitySet, final boolean isSingleEntity,
                                     final ExpandOption expand, final SelectOption select, final String navOrPropertyPath)
            throws SerializerException {

        return ContextURL.with().entitySet(entitySet)
                .selectList(oData.createUriHelper().buildContextURLSelectList(entitySet.getEntityType(), expand, select))
                .suffix(isSingleEntity ? ContextURL.Suffix.ENTITY : null)
                .navOrPropertyPath(navOrPropertyPath)
                .build();
    }
}
