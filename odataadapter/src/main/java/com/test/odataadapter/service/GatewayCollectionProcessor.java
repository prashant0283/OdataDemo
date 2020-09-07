package com.test.odataadapter.service;

import org.apache.olingo.commons.api.data.EntityCollection;
import org.apache.olingo.commons.api.edm.EdmEntitySet;
import org.apache.olingo.commons.api.format.ContentType;
import org.apache.olingo.commons.api.http.HttpHeader;
import org.apache.olingo.commons.api.http.HttpStatusCode;
import org.apache.olingo.server.api.*;
import org.apache.olingo.server.api.processor.EntityCollectionProcessor;
import org.apache.olingo.server.api.processor.EntityProcessor;
import org.apache.olingo.server.api.serializer.EntityCollectionSerializerOptions;
import org.apache.olingo.server.api.serializer.EntitySerializerOptions;
import org.apache.olingo.server.api.serializer.ODataSerializer;
import org.apache.olingo.server.api.serializer.SerializerResult;
import org.apache.olingo.server.api.uri.UriInfo;
import org.apache.olingo.server.api.uri.queryoption.ExpandOption;
import org.apache.olingo.server.api.uri.queryoption.SelectOption;

public class GatewayCollectionProcessor extends GatewayProcessor implements EntityCollectionProcessor {
    public GatewayCollectionProcessor(OData oData, ServiceMetadata serviceMetadata, EdmProvider edmProvider) {
        super(oData, serviceMetadata, edmProvider);
    }

    @Override
    public void readEntityCollection(ODataRequest oDataRequest, ODataResponse oDataResponse, UriInfo uriInfo, ContentType contentType) throws ODataApplicationException, ODataLibraryException {
        logger.info("Inside read");
        final EdmEntitySet edmEntitySet = getEdmEntitySet(uriInfo.asUriInfoResource());
        EntityCollection entityCollection = new DataProvider().readAll(edmEntitySet);

        ODataSerializer serializer = oData.createSerializer(contentType);
        final ExpandOption expand = uriInfo.getExpandOption();
        final SelectOption select = uriInfo.getSelectOption();
        SerializerResult serializedContent = serializer.entityCollection(serviceMetadata,edmEntitySet.getEntityType(), entityCollection,
                EntityCollectionSerializerOptions.with()
                        .contextURL(getContextUrl(serializer, edmEntitySet, false, expand, select, null))
                        .expand(expand).select(select)
                        .build());
        oDataResponse.setContent(serializedContent.getContent());
        oDataResponse.setStatusCode(HttpStatusCode.OK.getStatusCode());
        oDataResponse.setHeader(HttpHeader.CONTENT_TYPE, contentType.toContentTypeString());
    }
}
