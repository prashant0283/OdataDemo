package com.test.odataadapter.service;

import org.apache.olingo.commons.api.data.ContextURL;
import org.apache.olingo.commons.api.data.Entity;
import org.apache.olingo.commons.api.data.Property;
import org.apache.olingo.commons.api.edm.EdmComplexType;
import org.apache.olingo.commons.api.edm.EdmEntitySet;
import org.apache.olingo.commons.api.edm.EdmPrimitiveType;
import org.apache.olingo.commons.api.edm.EdmProperty;
import org.apache.olingo.commons.api.format.ContentType;
import org.apache.olingo.commons.api.http.HttpHeader;
import org.apache.olingo.commons.api.http.HttpStatusCode;
import org.apache.olingo.server.api.*;
import org.apache.olingo.server.api.processor.EntityProcessor;
import org.apache.olingo.server.api.serializer.*;
import org.apache.olingo.server.api.uri.*;
import org.apache.olingo.server.api.uri.queryoption.ExpandOption;
import org.apache.olingo.server.api.uri.queryoption.SelectOption;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.List;
import java.util.Locale;

public class GatewayEntityProcessor extends GatewayProcessor implements EntityProcessor {

    public GatewayEntityProcessor(OData oData, ServiceMetadata serviceMetadata, EdmProvider edmProvider) {
        super(oData, serviceMetadata, edmProvider);
        this.oData = oData;
        this.serviceMetadata = serviceMetadata;
        this.edmProvider = edmProvider;
    }

    @Override
    public void readEntity(ODataRequest oDataRequest, ODataResponse oDataResponse, UriInfo uriInfo, ContentType contentType) throws ODataApplicationException, ODataLibraryException {
        logger.info("Inside read");
        final EdmEntitySet edmEntitySet = getEdmEntitySet(uriInfo.asUriInfoResource());

        // Next we fetch the requested entity from the database
        Entity entity = null;
        try {
            entity = readEntityInternal(uriInfo.asUriInfoResource(), edmEntitySet);
        } catch (Exception e) {
            throw new ODataApplicationException(e.getMessage(), 500, Locale.ENGLISH);
        }

        if (entity == null) {
            // If no entity was found for the given key we throw an exception.
            throw new ODataApplicationException("No entity found for this key", HttpStatusCode.NOT_FOUND
                    .getStatusCode(), Locale.ENGLISH);
        } else {
            // If an entity was found we proceed by serializing it and sending it to the client.
            ODataSerializer serializer = oData.createSerializer(contentType);
            final ExpandOption expand = uriInfo.getExpandOption();
            final SelectOption select = uriInfo.getSelectOption();
            SerializerResult serializedContent = serializer.entity(serviceMetadata,edmEntitySet.getEntityType(), entity,
                    EntitySerializerOptions.with()
                            .contextURL(getContextUrl(serializer, edmEntitySet, true, expand, select, null))
                            .expand(expand).select(select)
                            .build());
            oDataResponse.setContent(serializedContent.getContent());
            oDataResponse.setStatusCode(HttpStatusCode.OK.getStatusCode());
            oDataResponse.setHeader(HttpHeader.CONTENT_TYPE, contentType.toContentTypeString());
        }
    }

    @Override
    public void createEntity(ODataRequest oDataRequest, ODataResponse oDataResponse, UriInfo uriInfo, ContentType contentType, ContentType contentType1) throws ODataApplicationException, ODataLibraryException {
        throw new UnsupportedOperationException("Create Operation not Supported!!");
    }

    @Override
    public void updateEntity(ODataRequest oDataRequest, ODataResponse oDataResponse, UriInfo uriInfo, ContentType contentType, ContentType contentType1) throws ODataApplicationException, ODataLibraryException {
        throw new UnsupportedOperationException("Update Operation not Supported!!");
    }

    @Override
    public void deleteEntity(ODataRequest oDataRequest, ODataResponse oDataResponse, UriInfo uriInfo) throws ODataApplicationException, ODataLibraryException {
        throw new UnsupportedOperationException("Delete Operation not Supported!!");
    }

    private Entity readEntityInternal(final UriInfoResource uriInfo, final EdmEntitySet entitySet) throws Exception {
        final UriResourceEntitySet resourceEntitySet = (UriResourceEntitySet) uriInfo.getUriResourceParts().get(0);
        return new DataProvider().read(entitySet, resourceEntitySet.getKeyPredicates());
    }

    private void readProperty(ODataResponse response, UriInfo uriInfo, ContentType contentType, boolean complex) throws ODataApplicationException, SerializerException {

        // To read a property we have to first get the entity out of the entity set
        final EdmEntitySet edmEntitySet = getEdmEntitySet(uriInfo.asUriInfoResource());
        Entity entity;
        try {
            entity = readEntityInternal(uriInfo.asUriInfoResource(), edmEntitySet);
        } catch (Exception e) {
            throw new ODataApplicationException(e.getMessage(), 500, Locale.ENGLISH);
        }

        if (entity == null) {
            // If no entity was found for the given key we throw an exception.
            throw new ODataApplicationException("No entity found for this key",
                    HttpStatusCode.NOT_FOUND.getStatusCode(), Locale.ENGLISH);
        } else {
            // Next we get the property value from the entity and pass the value to serialization
            UriResourceProperty uriProperty = (UriResourceProperty) uriInfo.getUriResourceParts()
                    .get(uriInfo.getUriResourceParts().size() - 1); // Last segment
            EdmProperty edmProperty = uriProperty.getProperty();
            Property property = entity.getProperty(edmProperty.getName());
            if (property == null) {
                throw new ODataApplicationException("No property found", HttpStatusCode.NOT_FOUND.getStatusCode(),
                        Locale.ENGLISH);
            } else {
                if (property.getValue() == null) {
                    response.setStatusCode(HttpStatusCode.NO_CONTENT.getStatusCode());
                } else {

                }
            }
        }
    }



}
